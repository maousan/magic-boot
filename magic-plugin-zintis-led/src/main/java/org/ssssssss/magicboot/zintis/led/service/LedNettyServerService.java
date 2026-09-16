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
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class LedNettyServerService {

    public static final int DEFAULT_NETTY_SERVER_PORT = 9834;
    public static final int DEFAULT_CLIENT_READ_IDLE_SECONDS = 120;
    /** 厂家规范：服务器心跳须 6 秒内发送一次，建议 3 秒 */
    public static final long DEFAULT_HEARTBEAT_INTERVAL_SECONDS = 3;
    public static final int DEFAULT_TCP_KEEP_IDLE_SECONDS = 60;
    public static final int DEFAULT_TCP_KEEP_INTERVAL_SECONDS = 10;
    public static final int DEFAULT_TCP_KEEP_COUNT = 3;
    public static final long DEFAULT_MIN_SEND_INTERVAL_MILLIS = 500;
    public static final int DEFAULT_PENDING_COMMAND_CACHE_LIMIT = 200;
    public static final long DEFAULT_RETRY_SCAN_INITIAL_DELAY_SECONDS = 30;
    public static final long DEFAULT_RETRY_SCAN_PERIOD_SECONDS = 10;
    public static final long SERVER_LOCK_TIMEOUT_SECONDS = 10;
    public static final long GROUP_SHUTDOWN_QUIET_PERIOD_SECONDS = 2;
    public static final long GROUP_SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock outboundSendLock = new ReentrantLock();
    private final ExecutorService commandRetryExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "zintis-led-command-retry");
        thread.setDaemon(true);
        return thread;
    });
    private ScheduledExecutorService retryScanExecutor;
    private ScheduledExecutorService heartbeatExecutor;
    private long heartbeatIntervalSeconds = DEFAULT_HEARTBEAT_INTERVAL_SECONDS;
    private final LedNettyServerHandler serverHandler;
    private final LedPendingCommandRepository pendingCommandRepository;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;
    @Value("${zintis.led.netty.heartbeat.enabled:false}")
    private boolean heartbeatEnabled;
    @Value("${zintis.led.netty.client-report.registration.enabled:true}")
    private boolean clientReportRegistrationEnabled = true;
    @Value("${zintis.led.netty.client-report.reply-ack.enabled:true}")
    private boolean clientReportReplyAckEnabled;
    @Value("${zintis.led.netty.trace.enabled:false}")
    private boolean traceEnabled;
    @Value("${zintis.led.netty.client-read-idle-seconds:120}")
    private int clientReadIdleSeconds;
    @Value("${zintis.led.netty.retry.scan.enabled:true}")
    private boolean retryScanEnabled;
    @Value("${zintis.led.netty.retry.scan.initial-delay-seconds:30}")
    private long retryScanInitialDelaySeconds;
    @Value("${zintis.led.netty.retry.scan.period-seconds:10}")
    private long retryScanPeriodSeconds;
    @Value("${zintis.led.netty.ack-timeout-ms:3000}")
    private long ackTimeoutMs;
    private volatile long lastOutboundSendAt;

    public LedNettyServerService() {
        this(new LedNettyServerHandler(), null);
    }

    @Autowired
    public LedNettyServerService(LedDeviceRegistryService deviceRegistryService, LedPendingCommandRepository pendingCommandRepository) {
        this(new LedNettyServerHandler(deviceRegistryService::saveClientDevice), pendingCommandRepository);
    }

    LedNettyServerService(LedNettyServerHandler serverHandler, LedPendingCommandRepository pendingCommandRepository) {
        this.serverHandler = serverHandler;
        this.pendingCommandRepository = pendingCommandRepository;
        this.serverHandler.setOutboundSender(this::sendToChannelWithInterval);
        this.serverHandler.setClientReportListener(this::retryPendingCommand);
    }

    @PostConstruct
    public void autoStart() {
        serverHandler.setClientReportRegistrationEnabled(clientReportRegistrationEnabled);
        serverHandler.setClientReportReplyAckEnabled(clientReportReplyAckEnabled);
        serverHandler.setTraceEnabled(traceEnabled);
        log.info("LED netty server trace log enabled={}, client-report reply ack enabled={}", traceEnabled, clientReportReplyAckEnabled);
        start(DEFAULT_NETTY_SERVER_PORT);
        startRetryScan();
        if (heartbeatEnabled) {
            startHeartbeat();
        }
    }

    @PreDestroy
    public void shutdown() {
        stopRetryScan();
        stop();
    }

    private void startRetryScan() {
        if (!retryScanEnabled || pendingCommandRepository == null) {
            log.info("LED pending command retry scan disabled (enabled={}, hasRepository={})",
                    retryScanEnabled, pendingCommandRepository != null);
            return;
        }
        long initialDelay = retryScanInitialDelaySeconds > 0 ? retryScanInitialDelaySeconds : DEFAULT_RETRY_SCAN_INITIAL_DELAY_SECONDS;
        long period = retryScanPeriodSeconds > 0 ? retryScanPeriodSeconds : DEFAULT_RETRY_SCAN_PERIOD_SECONDS;
        retryScanExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "zintis-led-retry-scan");
            thread.setDaemon(true);
            return thread;
        });
        retryScanExecutor.scheduleAtFixedRate(this::scanAndRetryPending, initialDelay, period, TimeUnit.SECONDS);
        log.info("LED pending command retry scan started: initialDelay={}s, period={}s", initialDelay, period);
    }

    private void stopRetryScan() {
        if (retryScanExecutor != null) {
            retryScanExecutor.shutdownNow();
            retryScanExecutor = null;
        }
    }

    public LedNettyServerStatusResponse start(Integer requestedPort) {
        try {
            if (!lock.tryLock(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return status("Start failed: server is busy, try again later");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return status("Start failed: interrupted");
        }
        try {
            if (isRunning()) {
                return LedNettyServerStatusResponse.builder()
                        .running(true)
                        .port(boundPort)
                        .activeConnections(serverHandler.getActiveConnectionCount())
                        .heartbeatEnabled(heartbeatEnabled)
                        .heartbeatRunning(heartbeatExecutor != null)
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
            safeStopInternal();
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // clientReadIdleSeconds=0 传给 IdleStateHandler 表示不检测读空闲（不主动关闭半开连接）
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(clientReadIdleSeconds, 0, 0))
                                    .addLast(new LedNettyFrameDecoder())
                                    .addLast(serverHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
//            configureTcpKeepAliveOptions(bootstrap);

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            log.info("LED netty server started on port {}", boundPort);
            if (heartbeatEnabled) {
                startHeartbeat();
            }
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
        try {
            if (!lock.tryLock(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return status("Stop failed: server is busy, try again later");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return status("Stop failed: interrupted");
        }
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
        try {
            if (!lock.tryLock(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return status("Status query timeout: server is busy");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return status("Status query interrupted");
        }
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
            if (enabled) {
                startHeartbeat();
            } else {
                stopHeartbeat();
            }
            return status(enabled ? "Netty server heartbeat enabled" : "Netty server heartbeat disabled");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 启动服务端周期心跳：每 heartbeatIntervalSeconds 秒向所有活跃连接发送心跳帧。
     * 厂家规范要求 6 秒内至少一次（建议 3 秒），设备收不到会主动断开重连。
     */
    private void startHeartbeat() {
        if (heartbeatExecutor != null) {
            return;
        }
        long interval = heartbeatIntervalSeconds > 0 ? heartbeatIntervalSeconds : DEFAULT_HEARTBEAT_INTERVAL_SECONDS;
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "zintis-led-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
        heartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeatToAllClients, interval, interval, TimeUnit.SECONDS);
        log.info("LED netty server heartbeat started: interval={}s", interval);
    }

    private void stopHeartbeat() {
        if (heartbeatExecutor != null) {
            heartbeatExecutor.shutdownNow();
            heartbeatExecutor = null;
            log.info("LED netty server heartbeat stopped");
        }
    }

    private void sendHeartbeatToAllClients() {
        if (!isRunning()) {
            return;
        }
        try {
            int sent = serverHandler.broadcastHeartbeat();
            if (traceEnabled) {
                log.info("[LED-HEARTBEAT] server heartbeat sent to {} clients", sent);
            }
        } catch (Exception exception) {
            log.warn("LED netty server heartbeat broadcast error: {}", exception.getMessage());
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
        String color = request.getColor();
        String command = request.getCommand();
        // waitResponse=false 时一律走异步等回显（color 为空时按 ALL 处理）
        boolean asyncAck = !waitResponse;
        if (traceEnabled) {
            log.info("[LED-CMD] sendToClient start: remoteAddress={}, mac={}, waitResponse={}, asyncAck={}, payloadHex={}, payloadFormat={}, color={}",
                    request.getRemoteAddress(), macAddress, waitResponse, asyncAck,
                    LedProtocolCodec.toHex(data), request.getPayloadFormat(), color);
        }
        LedNettyServerHandler.SendResult result = sendWithInterval(
                "client-command",
                () -> serverHandler.sendTo(request.getRemoteAddress(), data, waitResponse, asyncAck));
        int failedCount = result.totalTargets() - result.successCount();
        boolean responseTimeout = waitResponse && result.responses().stream().anyMatch(LedNettyServerHandler.ClientResponse::timeout);
        if (traceEnabled) {
            log.info("[LED-CMD] sendToClient result: total={}, success={}, failed={}, responseCount={}, responseTimeout={}, pendingFutures={}",
                    result.totalTargets(), result.successCount(), failedCount,
                    result.responses().size(), responseTimeout, result.pendingFutures().size());
        }
        if (asyncAck && failedCount == 0 && !result.pendingFutures().isEmpty()) {
            // 异步确认：立即返回写入成功，后台等回显
            ackAsynchronously(macAddress, color, command, data, request.getRemoteAddress(), result.pendingFutures());
        } else {
            handleSendOutcome(macAddress, color, command, data, request.getRemoteAddress(), result, failedCount, responseTimeout);
        }
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

    /**
     * 异步等待设备回显确认。waitResponse=false 且有 color 时走此路径。
     * 调用方已立即返回，后台等 waiter future（最多 ackTimeoutMs）：
     * - 收到回显 → 判定成功，删除 DB 待重试指令
     * - 超时 → 判定失败，写入 DB 走重试
     */
    private void ackAsynchronously(
            String macAddress,
            String color,
            String command,
            byte[] data,
            String remoteAddress,
            List<java.util.concurrent.CompletableFuture<LedNettyServerHandler.ClientResponse>> futures) {
        if (pendingCommandRepository == null || macAddress == null || macAddress.isBlank() || futures.isEmpty()) {
            return;
        }
        if (futures.size() > 1) {
            log.warn("LED async ack expected 1 future but got {}, using first: mac={}", futures.size(), macAddress);
        }
        java.util.concurrent.CompletableFuture<LedNettyServerHandler.ClientResponse> future = futures.get(0);
        commandRetryExecutor.execute(() -> {
            try {
                LedNettyServerHandler.ClientResponse response = future.get(ackTimeoutMs, TimeUnit.MILLISECONDS);
                pendingCommandRepository.deleteByMacAndColor(macAddress, color);
                if (traceEnabled) {
                    log.info("[LED-ASYNC-ACK] received: mac={}, color={}, command={}, rawResponseHex={}",
                            macAddress, color, command, response.rawResponseHex());
                }
            } catch (java.util.concurrent.TimeoutException timeout) {
                String frameHex = LedProtocolCodec.toHex(data);
                String commandText = (command == null || command.isBlank()) ? "UNKNOWN" : command.trim().toUpperCase();
                pendingCommandRepository.upsert(macAddress, color, commandText, frameHex, remoteAddress);
                if (traceEnabled) {
                    log.warn("[LED-ASYNC-ACK] timeout, cached for retry: mac={}, color={}, command={}, ackTimeoutMs={}",
                            macAddress, color, commandText, ackTimeoutMs);
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            } catch (Exception exception) {
                log.warn("LED async ack error: mac={}, color={}, error={}", macAddress, color, exception.getMessage());
            }
        });
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

    /**
     * 处理发送结果：成功则清除待重试指令，失败则写入/覆盖 DB（per-(mac,color) 覆盖语义）。
     * 失败 = 写失败 OR (开启了等待回显但响应超时)。
     * 仅当调用方明确传入 color 时才纳入持久化重试（color 为 null 表示非灯开关类指令，不重试）。
     */
    private void handleSendOutcome(
            String macAddress,
            String color,
            String command,
            byte[] data,
            String remoteAddress,
            LedNettyServerHandler.SendResult result,
            int failedCount,
            boolean responseTimeout) {
        if (pendingCommandRepository == null || macAddress == null || macAddress.isBlank()) {
            return;
        }
        if (color == null || color.isBlank()) {
            return;
        }
        boolean writeSuccess = result.totalTargets() > 0 && failedCount == 0;
        boolean success = writeSuccess && !responseTimeout;
        if (success) {
            pendingCommandRepository.deleteByMacAndColor(macAddress, color);
            return;
        }
        String frameHex = LedProtocolCodec.toHex(data);
        String commandText = (command == null || command.isBlank()) ? "UNKNOWN" : command.trim().toUpperCase();
        pendingCommandRepository.upsert(macAddress, color, commandText, frameHex, remoteAddress);
    }

    private void removePendingCommand(String macAddress) {
        if (pendingCommandRepository != null && macAddress != null && !macAddress.isBlank()) {
            pendingCommandRepository.deleteByMac(macAddress);
        }
    }

    /**
     * 心跳即时触发：设备上报 MAC 时，查 DB 该 mac 的待重试指令并逐条重发。
     * 走异步线程避免阻塞 Netty IO 线程。
     * 注意：不直接使用回调传入的 remoteAddress（断网重连后会变化），
     * 而是按 MAC 查当前活跃连接，保证用最新地址。
     */
    private void retryPendingCommand(String macAddress, String ipAddress, String remoteAddress) {
        if (pendingCommandRepository == null) {
            return;
        }
        String normalizedMac = normalizeMac(macAddress);
        if (normalizedMac.isBlank()) {
            return;
        }
        commandRetryExecutor.execute(() -> retryPendingByMac(normalizedMac));
    }

    /**
     * 定时扫描兜底：扫描 DB 全部待重试指令，对当前在线的 mac 逐条重发。
     * 不依赖设备主动上报心跳，保证服务重启/设备恢复后能主动清空积压。
     */
    private void scanAndRetryPending() {
        if (!isRunning() || pendingCommandRepository == null) {
            return;
        }
        try {
            List<LedPendingCommandRepository.LedPendingCommand> pendingList = pendingCommandRepository.findAllPending();
            if (pendingList.isEmpty()) {
                return;
            }
            if (traceEnabled) {
                log.info("[LED-RETRY] scan found {} pending commands", pendingList.size());
            }
            for (LedPendingCommandRepository.LedPendingCommand pending : pendingList) {
                String remoteAddress = serverHandler.findActiveRemoteAddressByMac(pending.macAddress());
                if (remoteAddress == null || remoteAddress.isBlank()) {
                    continue;
                }
                retryOnePending(pending, remoteAddress);
            }
        } catch (Exception exception) {
            log.warn("LED pending command scan error: {}", exception.getMessage());
        }
    }

    /**
     * 按 mac 查待重试指令并逐条重发（心跳触发路径）。
     * 按 MAC 查当前活跃连接地址，避免使用回调传入的旧 remoteAddress。
     */
    private void retryPendingByMac(String normalizedMac) {
        if (!isRunning()) {
            return;
        }
        try {
            List<LedPendingCommandRepository.LedPendingCommand> pendingList =
                    pendingCommandRepository.findPendingByMac(normalizedMac);
            if (pendingList.isEmpty()) {
                return;
            }
            String remoteAddress = serverHandler.findActiveRemoteAddressByMac(normalizedMac);
            if (remoteAddress == null || remoteAddress.isBlank()) {
                if (traceEnabled) {
                    log.warn("[LED-RETRY] heartbeat triggered but mac not online, skip: mac={}", normalizedMac);
                }
                return;
            }
            if (traceEnabled) {
                log.info("[LED-RETRY] heartbeat triggered, mac={} has {} pending commands, remoteAddress={}",
                        normalizedMac, pendingList.size(), remoteAddress);
            }
            for (LedPendingCommandRepository.LedPendingCommand pending : pendingList) {
                retryOnePending(pending, remoteAddress);
            }
        } catch (Exception exception) {
            log.warn("LED pending command retry-by-mac error: mac={}, error={}", normalizedMac, exception.getMessage());
        }
    }

    /**
     * 重发单条待重试指令。强制 waitResponse=true 以确认设备收到。
     * 成功则从 DB 删除；失败则递增 retry_count 留待下一轮。
     */
    private void retryOnePending(LedPendingCommandRepository.LedPendingCommand pending, String remoteAddress) {
        if (!isRunning()) {
            return;
        }
        byte[] data;
        try {
            data = parsePayload(pending.frameHex(), null, "hex");
        } catch (Exception exception) {
            log.warn("LED pending command frame parse failed, delete it: mac={}, color={}, frameHex={}, error={}",
                    pending.macAddress(), pending.color(), pending.frameHex(), exception.getMessage());
            pendingCommandRepository.deleteByMacAndColor(pending.macAddress(), pending.color());
            return;
        }
        try {
            if (traceEnabled) {
                log.info("[LED-RETRY] resend: mac={}, color={}, command={}, remoteAddress={}, frameHex={}, retryCount={}",
                        pending.macAddress(), pending.color(), pending.command(), remoteAddress,
                        pending.frameHex(), pending.retryCount());
            }
            LedNettyServerHandler.SendResult result = sendWithInterval(
                    "retry-pending-command",
                    () -> serverHandler.sendTo(remoteAddress, data, true));
            int failedCount = result.totalTargets() - result.successCount();
            boolean responseTimeout = result.responses().stream().anyMatch(LedNettyServerHandler.ClientResponse::timeout);
            boolean success = result.totalTargets() > 0 && failedCount == 0 && !responseTimeout;
            if (success) {
                pendingCommandRepository.deleteByMacAndColor(pending.macAddress(), pending.color());
                log.info("LED pending command retry succeeded: mac={}, color={}, attempts={}",
                        pending.macAddress(), pending.color(), pending.retryCount() + 1);
            } else {
                pendingCommandRepository.incrementRetry(pending.macAddress(), pending.color(), remoteAddress);
                if (traceEnabled) {
                    log.warn("[LED-RETRY] retry failed: mac={}, color={}, total={}, success={}, timeout={}, remoteAddress={}, activeClients={}",
                            pending.macAddress(), pending.color(),
                            result.totalTargets(), result.successCount(), responseTimeout,
                            remoteAddress, serverHandler.listActiveRemoteAddresses());
                }
            }
        } catch (Exception exception) {
            pendingCommandRepository.incrementRetry(pending.macAddress(), pending.color(), remoteAddress);
            log.warn("LED pending command retry error: mac={}, color={}, error={}",
                    pending.macAddress(), pending.color(), exception.getMessage());
        }
    }

    private LedNettyServerStatusResponse status(String message) {
        return LedNettyServerStatusResponse.builder()
                .running(isRunning())
                .port(isRunning() ? boundPort : -1)
                .activeConnections(serverHandler.getActiveConnectionCount())
                .heartbeatEnabled(heartbeatEnabled)
                .heartbeatRunning(heartbeatExecutor != null)
                .clientReportRegistrationEnabled(clientReportRegistrationEnabled)
                .message(message)
                .build();
    }

    private void safeStopInternal() {
        stopHeartbeat();
        serverHandler.resetActiveConnections();
        if (serverChannel != null) {
            try {
                serverChannel.close().await(GROUP_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            serverChannel = null;
        }
        if (workerGroup != null) {
            try {
                workerGroup.shutdownGracefully(GROUP_SHUTDOWN_QUIET_PERIOD_SECONDS, GROUP_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .await(GROUP_SHUTDOWN_TIMEOUT_SECONDS + 1, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            workerGroup = null;
        }
        if (bossGroup != null) {
            try {
                bossGroup.shutdownGracefully(GROUP_SHUTDOWN_QUIET_PERIOD_SECONDS, GROUP_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .await(GROUP_SHUTDOWN_TIMEOUT_SECONDS + 1, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            bossGroup = null;
        }
        boundPort = -1;
    }

    private LedNettyServerHandler.SendResult sendWithInterval(
            String sendType,
            java.util.function.Supplier<LedNettyServerHandler.SendResult> sender) {
        try {
            if (!outboundSendLock.tryLock(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("LED netty outbound send skipped (lock busy): type={}", sendType);
                return new LedNettyServerHandler.SendResult(0, 0, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new LedNettyServerHandler.SendResult(0, 0, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        }
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
        try {
            if (!outboundSendLock.tryLock(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("LED netty heartbeat reply skipped (lock busy): channel={}", channel.remoteAddress());
                return;
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            waitForSendInterval("client-heartbeat-reply");
            ChannelFuture future = channel.writeAndFlush(Unpooled.wrappedBuffer(data));
            if (!future.await(SERVER_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("LED netty heartbeat reply write timeout: channel={}", channel.remoteAddress());
            }
            lastOutboundSendAt = System.currentTimeMillis();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
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

    void setHeartbeatEnabled(boolean heartbeatEnabled) {
        this.heartbeatEnabled = heartbeatEnabled;
    }

    void setHeartbeatIntervalSeconds(long heartbeatIntervalSeconds) {
        this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
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
