package org.ssssssss.magicboot.zintis.led.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyBroadcastRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyClientListResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;
import org.ssssssss.magicboot.zintis.led.transport.netty.LedNettyFrameDecoder;
import org.ssssssss.magicboot.zintis.led.transport.netty.LedNettyServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class LedNettyServerService {

    public static final int DEFAULT_NETTY_SERVER_PORT = 9834;
    public static final int DEFAULT_CLIENT_READ_IDLE_SECONDS = 120;
    public static final int DEFAULT_TCP_KEEP_IDLE_SECONDS = 60;
    public static final int DEFAULT_TCP_KEEP_INTERVAL_SECONDS = 10;
    public static final int DEFAULT_TCP_KEEP_COUNT = 3;
    public static final int DEFAULT_HEARTBEAT_INTERVAL_SECONDS = 3;
    public static final long DEFAULT_MIN_SEND_INTERVAL_MILLIS = 500;
    public static final int DEFAULT_PENDING_COMMAND_CACHE_LIMIT = 200;
    private static final byte[] HEARTBEAT_FRAME = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};

    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock outboundSendLock = new ReentrantLock();
    private final Map<String, PendingClientCommand> pendingClientCommands = new ConcurrentHashMap<>();
    private final ExecutorService commandRetryExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "zintis-led-command-retry");
        thread.setDaemon(true);
        return thread;
    });
    private final LedNettyServerHandler serverHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;
    private ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatTask;
    @Value("${zintis.led.netty.heartbeat.enabled:false}")
    private boolean heartbeatEnabled;
    @Value("${zintis.led.netty.client-report.registration.enabled:true}")
    private boolean clientReportRegistrationEnabled = true;
    private volatile Long lastHeartbeatAt;
    private volatile int lastHeartbeatTargets;
    private volatile int lastHeartbeatSuccessCount;
    private volatile int lastHeartbeatFailedCount;
    private volatile long lastOutboundSendAt;

    public LedNettyServerService() {
        this(new LedNettyServerHandler());
    }

    @Autowired
    public LedNettyServerService(LedDeviceRegistryService deviceRegistryService) {
        this(new LedNettyServerHandler(deviceRegistryService::saveClientDevice));
    }

    LedNettyServerService(LedNettyServerHandler serverHandler) {
        this.serverHandler = serverHandler;
        this.serverHandler.setOutboundSender(this::sendToChannelWithInterval);
        this.serverHandler.setClientReportListener(this::retryPendingCommand);
    }

    @PostConstruct
    public void autoStart() {
        serverHandler.setClientReportRegistrationEnabled(clientReportRegistrationEnabled);
        start(DEFAULT_NETTY_SERVER_PORT);
    }

    @PreDestroy
    public void shutdown() {
        stop();
    }

    public LedNettyServerStatusResponse start(Integer requestedPort) {
        lock.lock();
        try {
            if (isRunning()) {
                return LedNettyServerStatusResponse.builder()
                        .running(true)
                        .port(boundPort)
                        .activeConnections(serverHandler.getActiveConnectionCount())
                        .heartbeatEnabled(heartbeatEnabled)
                        .heartbeatRunning(isHeartbeatRunning())
                        .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                        .message("Netty server already running")
                        .build();
            }

            int port = requestedPort == null ? DEFAULT_NETTY_SERVER_PORT : requestedPort;
            if (port < 0 || port > 65535) {
                return LedNettyServerStatusResponse.builder()
                        .running(false)
                        .port(-1)
                        .activeConnections(0)
                        .heartbeatEnabled(heartbeatEnabled)
                        .heartbeatRunning(false)
                        .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                        .message("Start failed: invalid port " + port)
                        .build();
            }
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(DEFAULT_CLIENT_READ_IDLE_SECONDS, 0, 0))
                                    .addLast(new LedNettyFrameDecoder())
                                    .addLast(serverHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            configureTcpKeepAliveOptions(bootstrap);

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            if (heartbeatEnabled) {
                startHeartbeat();
            }
            log.info("LED netty server started on port {}", boundPort);
            return status("Netty server started");
        } catch (Exception ex) {
            log.error("start netty server failed: {}", ex.getMessage(), ex);
            safeStopInternal();
            return LedNettyServerStatusResponse.builder()
                    .running(false)
                    .port(-1)
                    .activeConnections(0)
                    .heartbeatEnabled(heartbeatEnabled)
                    .heartbeatRunning(false)
                    .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                    .message("Start failed: " + ex.getMessage())
                    .build();
        } finally {
            lock.unlock();
        }
    }

    public LedNettyServerStatusResponse stop() {
        lock.lock();
        try {
            if (!isRunning()) {
                return LedNettyServerStatusResponse.builder()
                        .running(false)
                        .port(-1)
                        .activeConnections(0)
                        .heartbeatEnabled(heartbeatEnabled)
                        .heartbeatRunning(false)
                        .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                        .message("Netty server is not running")
                        .build();
            }
            safeStopInternal();
            return LedNettyServerStatusResponse.builder()
                    .running(false)
                    .port(-1)
                    .activeConnections(0)
                    .heartbeatEnabled(heartbeatEnabled)
                    .heartbeatRunning(false)
                    .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                    .message("Netty server stopped")
                    .build();
        } finally {
            lock.unlock();
        }
    }

    public LedNettyServerStatusResponse status() {
        lock.lock();
        try {
            return status("Netty server status");
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return serverChannel != null && serverChannel.isOpen() && serverChannel.isActive();
    }

    public LedNettyServerStatusResponse updateHeartbeat(boolean enabled) {
        lock.lock();
        try {
            heartbeatEnabled = enabled;
            if (enabled && isRunning()) {
                startHeartbeat();
            } else {
                stopHeartbeat();
            }
            return status(enabled ? "Netty heartbeat enabled" : "Netty heartbeat disabled");
        } finally {
            lock.unlock();
        }
    }

    public LedNettyServerStatusResponse updateClientReportRegistration(boolean enabled) {
        lock.lock();
        try {
            clientReportRegistrationEnabled = enabled;
            serverHandler.setClientReportRegistrationEnabled(enabled);
            return status(enabled ? "Netty client report registration enabled" : "Netty client report registration disabled");
        } finally {
            lock.unlock();
        }
    }

    private void configureTcpKeepAliveOptions(ServerBootstrap bootstrap) {
        childExtendedSocketOption(bootstrap, "TCP_KEEPIDLE", DEFAULT_TCP_KEEP_IDLE_SECONDS);
        childExtendedSocketOption(bootstrap, "TCP_KEEPINTERVAL", DEFAULT_TCP_KEEP_INTERVAL_SECONDS);
        childExtendedSocketOption(bootstrap, "TCP_KEEPCOUNT", DEFAULT_TCP_KEEP_COUNT);
    }

    private void childExtendedSocketOption(ServerBootstrap bootstrap, String optionName, int value) {
        extendedSocketOption(optionName).ifPresent(option -> {
            try {
                bootstrap.childOption(NioChannelOption.of(option), value);
                log.info("LED netty server tcp keepalive option configured: {}={}", optionName, value);
            } catch (RuntimeException exception) {
                log.info("LED netty server tcp keepalive option unsupported: {}, error={}",
                        optionName, exception.getMessage());
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Optional<SocketOption<Integer>> extendedSocketOption(String optionName) {
        return Arrays.stream(jdk.net.ExtendedSocketOptions.class.getFields())
                .filter(field -> optionName.equals(field.getName()))
                .findFirst()
                .flatMap(field -> {
                    try {
                        Object option = field.get(null);
                        if (option instanceof SocketOption<?> socketOption) {
                            return Optional.of((SocketOption<Integer>) socketOption);
                        }
                    } catch (IllegalAccessException exception) {
                        log.info("LED netty server tcp keepalive option inaccessible: {}, error={}",
                                optionName, exception.getMessage());
                    }
                    return Optional.empty();
                });
    }

    public LedNettyClientListResponse listClients() {
        List<String> clients = serverHandler.listActiveRemoteAddresses();
        List<LedNettyClientListResponse.ClientInfo> clientDetails = serverHandler.listActiveClients().stream()
                .map(client -> LedNettyClientListResponse.ClientInfo.builder()
                        .remoteAddress(client.remoteAddress())
                        .macAddress(client.macAddress())
                        .build())
                .toList();
        return LedNettyClientListResponse.builder()
                .running(isRunning())
                .totalClients(clients.size())
                .clients(clients)
                .clientDetails(clientDetails)
                .message(isRunning() ? "Active netty clients" : "Netty server is not running")
                .build();
    }

    public LedNettySendResponse sendToClient(LedNettySendRequest request) {
        if (!isRunning()) {
            return LedNettySendResponse.builder()
                    .success(false)
                    .message("Netty server is not running")
                    .totalTargets(0)
                    .successCount(0)
                    .failedCount(0)
                    .failedTargets(List.of())
                    .build();
        }
        byte[] data = parsePayload(request.getPayload(), request.getPayloadArray(), request.getPayloadFormat());
        boolean waitResponse = Boolean.TRUE.equals(request.getWaitResponse());
        String macAddress = resolveSendMacAddress(request);
        LedNettyServerHandler.SendResult result = sendWithInterval(
                "client-command",
                () -> serverHandler.sendTo(request.getRemoteAddress(), data, waitResponse));
        int failedCount = result.totalTargets() - result.successCount();
        cacheCommandIfNeeded(macAddress, request.getRemoteAddress(), data, waitResponse, result, failedCount);
        List<LedNettySendResponse.ClientResponse> responses = result.responses().stream()
                .map(response -> LedNettySendResponse.ClientResponse.builder()
                        .remoteAddress(response.remoteAddress())
                        .received(response.received())
                        .timeout(response.timeout())
                        .rawResponseHex(response.rawResponseHex())
                        .payloadAscii(response.payloadAscii())
                        .macAddress(response.macAddress())
                        .ipAddress(response.ipAddress())
                        .crc(response.crc())
                        .build())
                .toList();
        return LedNettySendResponse.builder()
                .success(result.totalTargets() > 0 && failedCount == 0)
                .message(buildSendMessage(result, failedCount, waitResponse))
                .totalTargets(result.totalTargets())
                .successCount(result.successCount())
                .failedCount(failedCount)
                .failedTargets(new ArrayList<>(result.failedTargets()))
                .waitResponse(waitResponse)
                .responseCount(responses.size())
                .responses(responses)
                .build();
    }

    public LedNettySendResponse broadcast(LedNettyBroadcastRequest request) {
        if (!isRunning()) {
            return LedNettySendResponse.builder()
                    .success(false)
                    .message("Netty server is not running")
                    .totalTargets(0)
                    .successCount(0)
                    .failedCount(0)
                    .failedTargets(List.of())
                    .build();
        }
        byte[] data = parsePayload(request.getPayload(), request.getPayloadArray(), request.getPayloadFormat());
        LedNettyServerHandler.SendResult result = sendWithInterval(
                "broadcast-command",
                () -> serverHandler.broadcast(data));
        int failedCount = result.totalTargets() - result.successCount();
        return LedNettySendResponse.builder()
                .success(result.totalTargets() > 0 && failedCount == 0)
                .message(result.totalTargets() == 0 ? "No active clients" : "Broadcast completed")
                .totalTargets(result.totalTargets())
                .successCount(result.successCount())
                .failedCount(failedCount)
                .failedTargets(new ArrayList<>(result.failedTargets()))
                .waitResponse(false)
                .responseCount(0)
                .responses(List.of())
                .build();
    }

    private String buildSendMessage(LedNettyServerHandler.SendResult result, int failedCount, boolean waitResponse) {
        if (result.totalTargets() == 0) {
            return "Target client not found";
        }
        if (failedCount > 0) {
            return "Send completed with failures";
        }
        if (waitResponse && result.responses().stream().noneMatch(LedNettyServerHandler.ClientResponse::received)) {
            return "Send completed, response timeout";
        }
        return waitResponse ? "Send completed with response" : "Send completed";
    }

    private String resolveSendMacAddress(LedNettySendRequest request) {
        String macAddress = normalizeMac(request.getMacAddress());
        if (!macAddress.isBlank()) {
            return macAddress;
        }
        return normalizeMac(serverHandler.findActiveMacByRemoteAddress(request.getRemoteAddress()));
    }

    private void cacheCommandIfNeeded(
            String macAddress,
            String remoteAddress,
            byte[] data,
            boolean waitResponse,
            LedNettyServerHandler.SendResult result,
            int failedCount) {
        if (result.totalTargets() > 0 && failedCount == 0) {
            removePendingCommand(macAddress);
            return;
        }
        if (macAddress.isBlank()) {
            log.warn("LED netty command send failed but cannot cache: missing mac, remoteAddress={}", remoteAddress);
            return;
        }
        if (pendingClientCommands.size() >= DEFAULT_PENDING_COMMAND_CACHE_LIMIT
                && !pendingClientCommands.containsKey(macAddress)) {
            log.warn("LED netty pending command cache is full, skip cache: mac={}, remoteAddress={}", macAddress, remoteAddress);
            return;
        }
        pendingClientCommands.put(macAddress, new PendingClientCommand(
                macAddress,
                remoteAddress,
                Arrays.copyOf(data, data.length),
                waitResponse,
                System.currentTimeMillis(),
                0
        ));
        log.info("LED netty command cached for retry: mac={}, remoteAddress={}, bytes={}",
                macAddress, remoteAddress, data.length);
    }

    private void removePendingCommand(String macAddress) {
        if (!macAddress.isBlank()) {
            pendingClientCommands.remove(macAddress);
        }
    }

    private void retryPendingCommand(String macAddress, String ipAddress, String remoteAddress) {
        String normalizedMac = normalizeMac(macAddress);
        PendingClientCommand pendingCommand = pendingClientCommands.get(normalizedMac);
        if (pendingCommand == null || remoteAddress == null || remoteAddress.isBlank()) {
            return;
        }
        commandRetryExecutor.execute(() -> retryPendingCommand(pendingCommand, remoteAddress));
    }

    private void retryPendingCommand(PendingClientCommand pendingCommand, String remoteAddress) {
        if (!isRunning()) {
            return;
        }
        try {
            LedNettyServerHandler.SendResult result = sendWithInterval(
                    "cached-client-command",
                    () -> serverHandler.sendTo(remoteAddress, pendingCommand.data(), pendingCommand.waitResponse()));
            int failedCount = result.totalTargets() - result.successCount();
            if (result.totalTargets() > 0 && failedCount == 0) {
                pendingClientCommands.remove(pendingCommand.macAddress());
                log.info("LED netty cached command retry succeeded: mac={}, remoteAddress={}, attempts={}",
                        pendingCommand.macAddress(), remoteAddress, pendingCommand.retryCount() + 1);
                return;
            }
            pendingClientCommands.put(pendingCommand.macAddress(), pendingCommand.nextRetry(remoteAddress));
            log.warn("LED netty cached command retry failed: mac={}, remoteAddress={}, total={}, success={}, failed={}",
                    pendingCommand.macAddress(), remoteAddress, result.totalTargets(), result.successCount(), failedCount);
        } catch (Exception exception) {
            pendingClientCommands.put(pendingCommand.macAddress(), pendingCommand.nextRetry(remoteAddress));
            log.warn("LED netty cached command retry error: mac={}, remoteAddress={}, error={}",
                    pendingCommand.macAddress(), remoteAddress, exception.getMessage(), exception);
        }
    }

    private LedNettyServerStatusResponse status(String message) {
        return LedNettyServerStatusResponse.builder()
                .running(isRunning())
                .port(isRunning() ? boundPort : -1)
                .activeConnections(serverHandler.getActiveConnectionCount())
                .heartbeatEnabled(heartbeatEnabled)
                .heartbeatRunning(isHeartbeatRunning())
                .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                .lastHeartbeatAt(lastHeartbeatAt)
                .lastHeartbeatTargets(lastHeartbeatTargets)
                .lastHeartbeatSuccessCount(lastHeartbeatSuccessCount)
                .lastHeartbeatFailedCount(lastHeartbeatFailedCount)
                .message(message)
                .build();
    }

    private void safeStopInternal() {
        stopHeartbeat();
        serverHandler.resetActiveConnections();
        if (serverChannel != null) {
            try {
                serverChannel.close().syncUninterruptibly();
            } catch (Exception ignored) {
            }
            serverChannel = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
            workerGroup = null;
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            bossGroup = null;
        }
        boundPort = -1;
    }

    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "zintis-led-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
        heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(
                this::sendHeartbeat,
                DEFAULT_HEARTBEAT_INTERVAL_SECONDS,
                DEFAULT_HEARTBEAT_INTERVAL_SECONDS,
                TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
            heartbeatTask = null;
        }
        if (heartbeatExecutor != null) {
            heartbeatExecutor.shutdownNow();
            heartbeatExecutor = null;
        }
    }

    private boolean isHeartbeatRunning() {
        return heartbeatTask != null && !heartbeatTask.isCancelled() && !heartbeatTask.isDone();
    }

    private void sendHeartbeat() {
        if (!isRunning()) {
            return;
        }
        try {
            LedNettyServerHandler.SendResult result = sendWithInterval(
                    "heartbeat",
                    () -> serverHandler.broadcast(HEARTBEAT_FRAME));
            recordHeartbeat(result);
            if (result.totalTargets() > 0) {
                log.info("LED netty heartbeat sent: total={}, success={}, failed={}",
                        result.totalTargets(), result.successCount(), result.totalTargets() - result.successCount());
            } else {
                log.debug("LED netty heartbeat skipped: no active clients");
            }
            if (result.totalTargets() > 0 && result.successCount() != result.totalTargets()) {
                log.warn("LED netty heartbeat sent with failures: total={}, success={}, failed={}",
                        result.totalTargets(), result.successCount(), result.failedTargets());
            }
        } catch (Exception exception) {
            recordHeartbeatFailure();
            log.warn("LED netty heartbeat send failed: {}", exception.getMessage(), exception);
        }
    }

    private LedNettyServerHandler.SendResult sendWithInterval(
            String sendType,
            java.util.function.Supplier<LedNettyServerHandler.SendResult> sender) {
        outboundSendLock.lock();
        try {
            waitForSendInterval(sendType);
            LedNettyServerHandler.SendResult result = sender.get();
            if (result.successCount() > 0) {
                lastOutboundSendAt = System.currentTimeMillis();
            }
            return result;
        } finally {
            outboundSendLock.unlock();
        }
    }

    private void sendToChannelWithInterval(Channel channel, byte[] data) {
        outboundSendLock.lock();
        try {
            waitForSendInterval("client-heartbeat-reply");
            channel.writeAndFlush(Unpooled.wrappedBuffer(data)).syncUninterruptibly();
            lastOutboundSendAt = System.currentTimeMillis();
        } finally {
            outboundSendLock.unlock();
        }
    }

    private void waitForSendInterval(String sendType) {
        long elapsed = System.currentTimeMillis() - lastOutboundSendAt;
        long waitMillis = DEFAULT_MIN_SEND_INTERVAL_MILLIS - elapsed;
        if (waitMillis <= 0) {
            return;
        }
        log.debug("LED netty outbound send delayed: type={}, waitMillis={}", sendType, waitMillis);
        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for LED netty outbound send interval", exception);
        }
    }

    private void recordHeartbeat(LedNettyServerHandler.SendResult result) {
        lastHeartbeatAt = System.currentTimeMillis();
        lastHeartbeatTargets = result.totalTargets();
        lastHeartbeatSuccessCount = result.successCount();
        lastHeartbeatFailedCount = result.totalTargets() - result.successCount();
    }

    private void recordHeartbeatFailure() {
        lastHeartbeatAt = System.currentTimeMillis();
        lastHeartbeatTargets = 0;
        lastHeartbeatSuccessCount = 0;
        lastHeartbeatFailedCount = 1;
    }

    private String normalizeMac(String rawMac) {
        if (rawMac == null || rawMac.isBlank()) {
            return "";
        }
        String compact = rawMac.replace("-", "").replace(":", "").toUpperCase(Locale.ROOT);
        if (compact.length() != 12) {
            return rawMac.trim();
        }
        return compact.substring(0, 2) + ":" + compact.substring(2, 4) + ":" + compact.substring(4, 6)
                + ":" + compact.substring(6, 8) + ":" + compact.substring(8, 10) + ":" + compact.substring(10, 12);
    }

    private record PendingClientCommand(
            String macAddress,
            String originalRemoteAddress,
            byte[] data,
            boolean waitResponse,
            long cachedAt,
            int retryCount
    ) {
        PendingClientCommand nextRetry(String remoteAddress) {
            return new PendingClientCommand(macAddress, remoteAddress, data, waitResponse, cachedAt, retryCount + 1);
        }
    }

    void setHeartbeatEnabled(boolean heartbeatEnabled) {
        this.heartbeatEnabled = heartbeatEnabled;
    }

    void setClientReportRegistrationEnabled(boolean clientReportRegistrationEnabled) {
        this.clientReportRegistrationEnabled = clientReportRegistrationEnabled;
        serverHandler.setClientReportRegistrationEnabled(clientReportRegistrationEnabled);
    }

    static byte[] parsePayload(String payload, List<Integer> payloadArray, String payloadFormat) {
        if (payloadArray != null && !payloadArray.isEmpty()) {
            byte[] data = new byte[payloadArray.size()];
            for (int i = 0; i < payloadArray.size(); i++) {
                Integer value = payloadArray.get(i);
                if (value == null || value < 0 || value > 255) {
                    throw new IllegalArgumentException("payloadArray value must be in range 0..255");
                }
                data[i] = (byte) (value & 0xFF);
            }
            return data;
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank when payloadArray is empty");
        }
        String format = payloadFormat == null ? "ascii" : payloadFormat.trim().toLowerCase(Locale.ROOT);
        if ("ascii".equals(format)) {
            return payload.getBytes(StandardCharsets.US_ASCII);
        }
        if ("hex".equals(format)) {
            String normalized = payload
                    .replace("0x", "")
                    .replace("0X", "")
                    .replaceAll("[\\s,]", "");
            if (normalized.isBlank() || normalized.length() % 2 != 0 || !normalized.matches("[0-9a-fA-F]+")) {
                throw new IllegalArgumentException("payload hex format invalid");
            }
            byte[] data = new byte[normalized.length() / 2];
            for (int i = 0; i < normalized.length(); i += 2) {
                data[i / 2] = (byte) Integer.parseInt(normalized.substring(i, i + 2), 16);
            }
            return data;
        }
        throw new IllegalArgumentException("payloadFormat must be ascii or hex");
    }
}
