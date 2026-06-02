package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Sharable
public class LedNettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "\\b(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}\\b"
    );
    private static final Pattern MAC_PATTERN = Pattern.compile(
            "\\b([0-9A-F]{2}([-:])){5}[0-9A-F]{2}\\b|\\b[0-9A-F]{12}\\b",
            Pattern.CASE_INSENSITIVE
    );
    private static final byte[] HEARTBEAT_FRAME = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};

    private final Set<SocketAddress> activeConnections = ConcurrentHashMap.newKeySet();
    private final Map<SocketAddress, Channel> activeChannels = new ConcurrentHashMap<>();
    private final Map<SocketAddress, String> activeClientMacAddresses = new ConcurrentHashMap<>();
    private final Map<SocketAddress, ConcurrentLinkedQueue<CompletableFuture<ClientResponse>>> pendingResponses = new ConcurrentHashMap<>();
    private final DeviceReporter deviceReporter;
    private volatile boolean clientReportRegistrationEnabled = true;
    private volatile ClientReportListener clientReportListener = (macAddress, ipAddress, remoteAddress) -> {
    };
    private volatile OutboundSender outboundSender = (channel, data) ->
            channel.writeAndFlush(Unpooled.wrappedBuffer(data)).syncUninterruptibly();

    public LedNettyServerHandler() {
        this((macAddress, ipAddress) -> {
        });
    }

    public LedNettyServerHandler(DeviceReporter deviceReporter) {
        this.deviceReporter = deviceReporter == null ? (macAddress, ipAddress) -> {
        } : deviceReporter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        activeConnections.add(ctx.channel().remoteAddress());
        activeChannels.put(ctx.channel().remoteAddress(), ctx.channel());
        log.debug("LED netty server connection active: {}", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        activeConnections.remove(ctx.channel().remoteAddress());
        activeChannels.remove(ctx.channel().remoteAddress());
        activeClientMacAddresses.remove(ctx.channel().remoteAddress());
        pendingResponses.remove(ctx.channel().remoteAddress());
        log.debug("LED netty server connection inactive: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf byteBuf) {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            byteBuf.release();
            boolean clientReportFrame = isClientReportFrame(data);
            byte[] payload = extractPayload(data);
            String payloadAscii = clientReportFrame ? toAsciiText(payload) : "";
            String mac = clientReportFrame ? extractMac(payload) : "";
            String ip = clientReportFrame ? extractIp(payload) : "";
            if (clientReportFrame) {
                if (!mac.isBlank()) {
                    activeClientMacAddresses.put(ctx.channel().remoteAddress(), mac);
                }
                if (ip.isBlank()) {
                    ip = extractRemoteIp(ctx.channel().remoteAddress());
                }
                if (clientReportRegistrationEnabled && !mac.isBlank()) {
                    deviceReporter.report(mac, ip);
                }
            }
            String crc = extractCrcHex(data);
            ClientResponse response = new ClientResponse(
                    normalizeRemoteAddress(String.valueOf(ctx.channel().remoteAddress())),
                    true,
                    false,
                    LedProtocolCodec.toHex(data),
                    payloadAscii,
                    mac,
                    ip,
                    crc
            );
            completePendingResponse(ctx.channel().remoteAddress(), response);
            if (shouldReplyHeartbeat(data)) {
                outboundSender.send(ctx.channel(), HEARTBEAT_FRAME);
                log.info("LED netty server heartbeat reply sent to {}", ctx.channel().remoteAddress());
            }
            if (clientReportFrame && !mac.isBlank()) {
                clientReportListener.onReport(mac, ip, normalizeRemoteAddress(String.valueOf(ctx.channel().remoteAddress())));
            }
            if (clientReportFrame) {
                log.info(
                        "LED netty server recv client report from {}: hex={}, payloadAscii={}, mac={}, ip={}, crc={}",
                        ctx.channel().remoteAddress(),
                        LedProtocolCodec.toHex(data),
                        payloadAscii,
                        mac,
                        ip,
                        crc
                );
            }
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        activeConnections.remove(ctx.channel().remoteAddress());
        activeChannels.remove(ctx.channel().remoteAddress());
        activeClientMacAddresses.remove(ctx.channel().remoteAddress());
        pendingResponses.remove(ctx.channel().remoteAddress());
        log.warn("LED netty server channel exception: {}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent idleStateEvent
                && idleStateEvent.state() == IdleState.READER_IDLE) {
            log.debug("LED netty server client read idle, keep channel open: {}", ctx.channel().remoteAddress());
            return;
        }
        ctx.fireUserEventTriggered(evt);
    }

    public int getActiveConnectionCount() {
        return activeConnections.size();
    }

    public void setOutboundSender(OutboundSender outboundSender) {
        this.outboundSender = outboundSender == null
                ? (channel, data) -> channel.writeAndFlush(Unpooled.wrappedBuffer(data)).syncUninterruptibly()
                : outboundSender;
    }

    public void setClientReportRegistrationEnabled(boolean clientReportRegistrationEnabled) {
        this.clientReportRegistrationEnabled = clientReportRegistrationEnabled;
    }

    public void setClientReportListener(ClientReportListener clientReportListener) {
        this.clientReportListener = clientReportListener == null ? (macAddress, ipAddress, remoteAddress) -> {
        } : clientReportListener;
    }

    public void resetActiveConnections() {
        activeConnections.clear();
        activeChannels.clear();
        activeClientMacAddresses.clear();
        pendingResponses.clear();
    }

    public List<String> listActiveRemoteAddresses() {
        List<String> result = new ArrayList<>(activeChannels.size());
        for (SocketAddress address : activeChannels.keySet()) {
            result.add(normalizeRemoteAddress(String.valueOf(address)));
        }
        Collections.sort(result);
        return result;
    }

    public List<ActiveClient> listActiveClients() {
        List<ActiveClient> result = new ArrayList<>(activeChannels.size());
        for (SocketAddress address : activeChannels.keySet()) {
            result.add(new ActiveClient(
                    normalizeRemoteAddress(String.valueOf(address)),
                    activeClientMacAddresses.getOrDefault(address, "")
            ));
        }
        result.sort((left, right) -> left.remoteAddress().compareTo(right.remoteAddress()));
        return result;
    }

    public String findActiveRemoteAddressByMac(String macAddress) {
        String normalizedMac = normalizeMac(macAddress);
        if (normalizedMac.isBlank()) {
            return "";
        }
        for (Map.Entry<SocketAddress, String> entry : activeClientMacAddresses.entrySet()) {
            if (normalizedMac.equalsIgnoreCase(entry.getValue())) {
                Channel channel = activeChannels.get(entry.getKey());
                if (channel != null && channel.isActive()) {
                    return normalizeRemoteAddress(String.valueOf(entry.getKey()));
                }
            }
        }
        return "";
    }

    public String findActiveMacByRemoteAddress(String remoteAddress) {
        String normalizedRemoteAddress = normalizeRemoteAddress(remoteAddress);
        if (normalizedRemoteAddress.isBlank()) {
            return "";
        }
        for (Map.Entry<SocketAddress, String> entry : activeClientMacAddresses.entrySet()) {
            if (normalizedRemoteAddress.equals(normalizeRemoteAddress(String.valueOf(entry.getKey())))) {
                return entry.getValue();
            }
        }
        return "";
    }

    public SendResult sendTo(String remoteAddress, byte[] data) {
        return sendTo(remoteAddress, data, false);
    }

    public SendResult sendTo(String remoteAddress, byte[] data, boolean waitResponse) {
        if (remoteAddress == null || remoteAddress.isBlank() || data == null || data.length == 0) {
            return new SendResult(0, 0, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        }
        CopyOnWriteArrayList<String> failed = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<ClientResponse> responses = new CopyOnWriteArrayList<>();
        int total = 0;
        int success = 0;
        String normalizedTarget = normalizeRemoteAddress(remoteAddress);
        for (Map.Entry<SocketAddress, Channel> entry : activeChannels.entrySet()) {
            String current = normalizeRemoteAddress(String.valueOf(entry.getKey()));
            if (!normalizedTarget.equals(current)) {
                continue;
            }
            total++;
            Channel channel = entry.getValue();
            if (channel != null && channel.isActive()) {
                CompletableFuture<ClientResponse> responseFuture = waitResponse ? registerResponseWaiter(entry.getKey()) : null;
                channel.writeAndFlush(Unpooled.wrappedBuffer(data)).syncUninterruptibly();
                success++;
                if (responseFuture != null) {
                    responses.add(awaitResponse(entry.getKey(), normalizedTarget, responseFuture));
                }
            } else {
                failed.add(String.valueOf(entry.getKey()));
            }
        }
        return new SendResult(total, success, failed, responses);
    }

    public SendResult broadcast(byte[] data) {
        if (data == null || data.length == 0) {
            return new SendResult(0, 0, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        }
        CopyOnWriteArrayList<String> failed = new CopyOnWriteArrayList<>();
        int total = 0;
        int success = 0;
        for (Map.Entry<SocketAddress, Channel> entry : activeChannels.entrySet()) {
            total++;
            Channel channel = entry.getValue();
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(Unpooled.wrappedBuffer(data)).syncUninterruptibly();
                success++;
            } else {
                failed.add(String.valueOf(entry.getKey()));
            }
        }
        return new SendResult(total, success, failed, new CopyOnWriteArrayList<>());
    }

    private String normalizeRemoteAddress(String remoteAddress) {
        String value = remoteAddress == null ? "" : remoteAddress.trim();
        if (value.startsWith("/")) {
            return value.substring(1);
        }
        return value;
    }

    private boolean shouldReplyHeartbeat(byte[] data) {
        return Arrays.equals(data, HEARTBEAT_FRAME) || isClientReportFrame(data);
    }

    private boolean isClientReportFrame(byte[] data) {
        if (data == null || data.length < 8) {
            return false;
        }
        if (data[0] != 0x66 || data[1] != (byte) 0xAB || data[2] != (byte) 0x97) {
            return false;
        }
        byte[] payload = extractPayload(data);
        if (payload.length < 2) {
            return false;
        }
        int macLen = payload[0] & 0xFF;
        int ipLen = payload[1] & 0xFF;
        return macLen > 0 && ipLen > 0 && 2 + macLen + ipLen == payload.length;
    }

    private CompletableFuture<ClientResponse> registerResponseWaiter(SocketAddress address) {
        CompletableFuture<ClientResponse> future = new CompletableFuture<>();
        pendingResponses.computeIfAbsent(address, ignored -> new ConcurrentLinkedQueue<>()).add(future);
        return future;
    }

    private ClientResponse awaitResponse(SocketAddress address, String remoteAddress, CompletableFuture<ClientResponse> future) {
        try {
            return future.get(3000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            return ClientResponse.timeout(remoteAddress);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ClientResponse.timeout(remoteAddress);
        } catch (Exception exception) {
            return ClientResponse.timeout(remoteAddress);
        } finally {
            ConcurrentLinkedQueue<CompletableFuture<ClientResponse>> queue = pendingResponses.get(address);
            if (queue != null) {
                queue.remove(future);
                if (queue.isEmpty()) {
                    pendingResponses.remove(address, queue);
                }
            }
        }
    }

    private void completePendingResponse(SocketAddress address, ClientResponse response) {
        ConcurrentLinkedQueue<CompletableFuture<ClientResponse>> queue = pendingResponses.get(address);
        if (queue == null) {
            return;
        }
        CompletableFuture<ClientResponse> future = queue.poll();
        if (future != null) {
            future.complete(response);
        }
        if (queue.isEmpty()) {
            pendingResponses.remove(address, queue);
        }
    }

    public record SendResult(
            int totalTargets,
            int successCount,
            CopyOnWriteArrayList<String> failedTargets,
            CopyOnWriteArrayList<ClientResponse> responses
    ) {
    }

    public record ActiveClient(String remoteAddress, String macAddress) {
    }

    public record ClientResponse(
            String remoteAddress,
            boolean received,
            boolean timeout,
            String rawResponseHex,
            String payloadAscii,
            String macAddress,
            String ipAddress,
            String crc
    ) {
        static ClientResponse timeout(String remoteAddress) {
            return new ClientResponse(remoteAddress, false, true, "", "", "", "", "");
        }
    }

    @FunctionalInterface
    public interface DeviceReporter {
        void report(String macAddress, String ipAddress);
    }

    @FunctionalInterface
    public interface ClientReportListener {
        void onReport(String macAddress, String ipAddress, String remoteAddress);
    }

    @FunctionalInterface
    public interface OutboundSender {
        void send(Channel channel, byte[] data);
    }

    static String toAsciiText(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        String raw = new String(data, StandardCharsets.US_ASCII);
        StringBuilder builder = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch >= 32 && ch <= 126) {
                builder.append(ch);
            } else {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    static String toPayloadAsciiText(byte[] frame) {
        return toAsciiText(extractPayload(frame));
    }

    static byte[] extractPayload(byte[] frame) {
        if (frame == null || frame.length == 0) {
            return new byte[0];
        }
        if (frame.length <= 5) {
            return new byte[0];
        }
        int payloadLength = frame.length - 5;
        byte[] payload = new byte[payloadLength];
        System.arraycopy(frame, 3, payload, 0, payloadLength);
        return payload;
    }

    static String extractCrcHex(byte[] frame) {
        if (frame == null || frame.length < 2) {
            return "";
        }
        byte[] crc = new byte[]{frame[frame.length - 2], frame[frame.length - 1]};
        return LedProtocolCodec.toHex(crc);
    }

    static String extractMac(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        String lengthPrefixedMac = extractLengthPrefixedMac(payload);
        if (!lengthPrefixedMac.isBlank()) {
            return lengthPrefixedMac;
        }
        Matcher matcher = MAC_PATTERN.matcher(toAsciiText(payload));
        if (!matcher.find()) {
            return "";
        }
        return normalizeMac(matcher.group());
    }

    static String extractIp(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        String lengthPrefixedIp = extractLengthPrefixedIp(payload);
        if (!lengthPrefixedIp.isBlank()) {
            return lengthPrefixedIp;
        }
        Matcher matcher = IPV4_PATTERN.matcher(toAsciiText(payload));
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private static String extractLengthPrefixedMac(byte[] payload) {
        if (payload.length < 2) {
            return "";
        }
        int macLen = payload[0] & 0xFF;
        int ipLen = payload[1] & 0xFF;
        int expected = 2 + macLen + ipLen;
        if (macLen <= 0 || ipLen <= 0 || expected > payload.length) {
            return "";
        }
        return normalizeMac(readAscii(payload, 2, macLen));
    }

    private static String extractLengthPrefixedIp(byte[] payload) {
        if (payload.length < 2) {
            return "";
        }
        int macLen = payload[0] & 0xFF;
        int ipLen = payload[1] & 0xFF;
        int expected = 2 + macLen + ipLen;
        if (macLen <= 0 || ipLen <= 0 || expected > payload.length) {
            return "";
        }
        String ipRaw = readAscii(payload, 2 + macLen, ipLen);
        Matcher matcher = IPV4_PATTERN.matcher(ipRaw);
        return matcher.find() ? matcher.group() : "";
    }

    private static String readAscii(byte[] data, int offset, int length) {
        if (length <= 0 || offset < 0 || offset + length > data.length) {
            return "";
        }
        return new String(data, offset, length, StandardCharsets.US_ASCII).trim();
    }

    private static String normalizeMac(String rawMac) {
        if (rawMac == null || rawMac.isBlank()) {
            return "";
        }
        String compact = rawMac.replace("-", "").replace(":", "").toUpperCase(Locale.ROOT);
        if (compact.length() != 12) {
            return rawMac;
        }
        return compact.substring(0, 2) + ":" + compact.substring(2, 4) + ":" + compact.substring(4, 6)
                + ":" + compact.substring(6, 8) + ":" + compact.substring(8, 10) + ":" + compact.substring(10, 12);
    }

    private static String extractRemoteIp(SocketAddress remoteAddress) {
        if (remoteAddress instanceof InetSocketAddress inetSocketAddress
                && inetSocketAddress.getAddress() != null) {
            return inetSocketAddress.getAddress().getHostAddress();
        }
        String normalized = remoteAddress == null ? "" : String.valueOf(remoteAddress);
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        int colonIndex = normalized.lastIndexOf(':');
        if (colonIndex > 0) {
            return normalized.substring(0, colonIndex);
        }
        return normalized;
    }
}
