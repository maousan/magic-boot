package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
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
    private static final long WRITE_TIMEOUT_MILLIS = 3000;

    private final Set<SocketAddress> activeConnections = ConcurrentHashMap.newKeySet();
    private final Map<SocketAddress, Channel> activeChannels = new ConcurrentHashMap<>();
    private final Map<SocketAddress, String> activeClientMacAddresses = new ConcurrentHashMap<>();
    private final Map<SocketAddress, ConcurrentLinkedQueue<CompletableFuture<ClientResponse>>> pendingResponses = new ConcurrentHashMap<>();
    private final DeviceReporter deviceReporter;
    private volatile boolean clientReportRegistrationEnabled = true;
    private volatile boolean clientReportReplyAckEnabled = true;
    private volatile boolean traceEnabled = false;
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
            if (traceEnabled) {
                log.info("[LED-RECV] inbound frame: remoteAddress={}, len={}, rawHex={}, clientReportFrame={}",
                        ctx.channel().remoteAddress(), data.length, LedProtocolCodec.toHex(data), isClientReportFrame(data));
            }
            boolean clientReportFrame = isClientReportFrame(data);
            byte[] payload = extractPayload(data);
            String payloadAscii = clientReportFrame ? toAsciiText(payload) : "";
            String mac = clientReportFrame ? extractMac(payload) : "";
            String ip = clientReportFrame ? extractIp(payload) : "";
            if (clientReportFrame) {
                if (!mac.isBlank()) {
                    // 保证一个 MAC 只有一个有效连接：关闭并移除该 MAC 的旧连接（如有）
                    closeStaleConnectionsByMac(ctx.channel().remoteAddress(), mac);
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
            String rawHex = LedProtocolCodec.toHex(data);
            ClientResponse response = new ClientResponse(
                    normalizeRemoteAddress(String.valueOf(ctx.channel().remoteAddress())),
                    true,
                    false,
                    rawHex,
                    payloadAscii,
                    mac,
                    ip,
                    crc
            );
            completePendingResponse(ctx.channel().remoteAddress(), response);
            if (clientReportReplyAckEnabled && shouldReplyHeartbeat(data)) {
                outboundSender.send(ctx.channel(), HEARTBEAT_FRAME);
                log.info("LED netty server client report ack sent to {}", ctx.channel().remoteAddress());
            }
            if (clientReportFrame && !mac.isBlank()) {
                clientReportListener.onReport(mac, ip, normalizeRemoteAddress(String.valueOf(ctx.channel().remoteAddress())));
            }
            // if (clientReportFrame) {
            //     log.info(
            //             "LED netty server recv client report from {}: hex={}, payloadAscii={}, mac={}, ip={}, crc={}",
            //             ctx.channel().remoteAddress(),
            //             LedProtocolCodec.toHex(data),
            //             payloadAscii,
            //             mac,
            //             ip,
            //             crc
            //     );
            // }
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
            log.info("LED netty client read idle, closing half-open connection: {}", ctx.channel().remoteAddress());
            ctx.close();
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

    public void setClientReportReplyAckEnabled(boolean clientReportReplyAckEnabled) {
        this.clientReportReplyAckEnabled = clientReportReplyAckEnabled;
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
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

    /**
     * 关闭并移除同一 MAC 的旧连接（半开残留），保证一个 MAC 只有一个有效连接。
     * 在收到上报帧、确认新连接的 MAC 后调用。currentAddress 是新连接，不会被关。
     */
    private void closeStaleConnectionsByMac(SocketAddress currentAddress, String mac) {
        String normalizedMac = normalizeMac(mac);
        if (normalizedMac.isBlank()) {
            return;
        }
        for (Map.Entry<SocketAddress, String> entry : activeClientMacAddresses.entrySet()) {
            if (entry.getKey().equals(currentAddress)) {
                continue;
            }
            if (!normalizedMac.equalsIgnoreCase(entry.getValue())) {
                continue;
            }
            SocketAddress staleAddress = entry.getKey();
            Channel staleChannel = activeChannels.get(staleAddress);
            log.info("LED netty closing stale connection for mac={}: old={}, new={}",
                    normalizedMac, staleAddress, currentAddress);
            if (staleChannel != null) {
                staleChannel.close();
            }
            activeConnections.remove(staleAddress);
            activeChannels.remove(staleAddress);
            activeClientMacAddresses.remove(staleAddress);
            pendingResponses.remove(staleAddress);
        }
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
        return sendTo(remoteAddress, data, false, false);
    }

    public SendResult sendTo(String remoteAddress, byte[] data, boolean waitResponse) {
        return sendTo(remoteAddress, data, waitResponse, false);
    }

    /**
     * 向指定远端地址发送数据。
     *
     * @param waitResponse 是否同步等待设备回显（阻塞当前线程）
     * @param registerOnly 注册回显 waiter 但不同步等待（用于异步确认场景）。
     *                     registerOnly=true 时 waiter 的 future 会放入 responses 返回，由调用方异步等待。
     *                     waitResponse=true 时 registerOnly 被忽略（同步等待优先）。
     */
    public SendResult sendTo(String remoteAddress, byte[] data, boolean waitResponse, boolean registerOnly) {
        if (remoteAddress == null || remoteAddress.isBlank() || data == null || data.length == 0) {
            return new SendResult(0, 0, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        }
        CopyOnWriteArrayList<String> failed = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<ClientResponse> responses = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<CompletableFuture<ClientResponse>> pendingFutures = new CopyOnWriteArrayList<>();
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
                boolean needWaiter = waitResponse || registerOnly;
                CompletableFuture<ClientResponse> responseFuture = needWaiter ? registerResponseWaiter(entry.getKey()) : null;
                if (traceEnabled) {
                    log.info("[LED-SEND] write to channel start: remoteAddress={}, waitResponse={}, registerOnly={}, payloadHex={}",
                            normalizedTarget, waitResponse, registerOnly, LedProtocolCodec.toHex(data));
                }
                ChannelFuture writeFuture = channel.writeAndFlush(Unpooled.wrappedBuffer(data));
                boolean done;
                try {
                    done = writeFuture.await(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    failed.add(String.valueOf(entry.getKey()));
                    continue;
                }
                if (!done) {
                    log.warn("LED netty write timeout: remoteAddress={}, payloadHex={}",
                            normalizedTarget, LedProtocolCodec.toHex(data));
                    failed.add(String.valueOf(entry.getKey()));
                    continue;
                }
                if (!writeFuture.isSuccess()) {
                    log.warn("LED netty write failed: remoteAddress={}, cause={}",
                            normalizedTarget, writeFuture.cause() == null ? "unknown" : writeFuture.cause().getMessage());
                    failed.add(String.valueOf(entry.getKey()));
                    continue;
                }
                success++;
                if (traceEnabled) {
                    log.info("[LED-SEND] write to channel ok: remoteAddress={}, isActive={}", normalizedTarget, channel.isActive());
                }
                if (responseFuture != null) {
                    if (waitResponse) {
                        ClientResponse response = awaitResponse(entry.getKey(), normalizedTarget, responseFuture);
                        if (traceEnabled) {
                            log.info("[LED-SEND] await response done: remoteAddress={}, received={}, timeout={}, rawResponseHex={}, payloadAscii={}, crc={}",
                                    normalizedTarget, response.received(), response.timeout(),
                                    response.rawResponseHex(), response.payloadAscii(), response.crc());
                        }
                        responses.add(response);
                    } else {
                        // registerOnly 模式：返回 future 给调用方异步等待
                        pendingFutures.add(responseFuture);
                    }
                }
            } else {
                if (traceEnabled) {
                    log.warn("[LED-SEND] channel not active, skip write: remoteAddress={}, isActive={}",
                            normalizedTarget, channel == null ? "null" : channel.isActive());
                }
                failed.add(String.valueOf(entry.getKey()));
            }
        }
        return new SendResult(total, success, failed, responses, pendingFutures);
    }
    /**
     * 向所有活跃连接非阻塞地发送服务端心跳帧（38 46 55 64 73 82）。
     * 厂家规范：服务器须每 6 秒内发送一次心跳（建议 3 秒），设备收不到会主动断开重连。
     * 与 broadcast 不同：不等待写入完成、不加发送间隔锁，
     * 个别半开/慢连接不得阻塞心跳调度线程或拖累其他连接的心跳。
     *
     * @return 投递（提交写入）的客户端数量
     */
    public int broadcastHeartbeat() {
        int sent = 0;
        for (Map.Entry<SocketAddress, Channel> entry : activeChannels.entrySet()) {
            Channel channel = entry.getValue();
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(Unpooled.wrappedBuffer(HEARTBEAT_FRAME));
                sent++;
            }
        }
        return sent;
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
        return isClientReportFrame(data);
    }

    private boolean isClientReportFrame(byte[] data) {
        if (data == null || data.length < 8) {
            return false;
        }
        // 帧结构：[帧头字节][0xAB][0x97][macLen][ipLen][mac...][ip...][CRC2字节]
        // 第1字节实测有 0x66/0x02/0x03 等多种值（疑似帧序号），不作为判定条件。
        // 用第2、3字节 0xAB 0x97 + 长度前缀结构自洽来识别上报帧。
        if (data.length < 2 || data[1] != (byte) 0xAB || data[2] != (byte) 0x97) {
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
            CopyOnWriteArrayList<ClientResponse> responses,
            CopyOnWriteArrayList<CompletableFuture<ClientResponse>> pendingFutures
    ) {
        public SendResult(int totalTargets, int successCount,
                          CopyOnWriteArrayList<String> failedTargets,
                          CopyOnWriteArrayList<ClientResponse> responses) {
            this(totalTargets, successCount, failedTargets, responses, new CopyOnWriteArrayList<>());
        }
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
