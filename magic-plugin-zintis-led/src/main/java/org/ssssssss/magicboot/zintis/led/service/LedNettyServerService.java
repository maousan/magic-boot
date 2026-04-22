package org.ssssssss.magicboot.zintis.led.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class LedNettyServerService {

    public static final int DEFAULT_NETTY_SERVER_PORT = 9834;
    public static final int DEFAULT_CLIENT_READ_IDLE_SECONDS = 120;

    private final ReentrantLock lock = new ReentrantLock();
    private final LedNettyServerHandler serverHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;

    public LedNettyServerService() {
        this(new LedNettyServerHandler());
    }

    LedNettyServerService(LedNettyServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @PostConstruct
    public void autoStart() {
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
                        .message("Netty server already running")
                        .build();
            }

            int port = requestedPort == null ? DEFAULT_NETTY_SERVER_PORT : requestedPort;
            if (port < 0 || port > 65535) {
                return LedNettyServerStatusResponse.builder()
                        .running(false)
                        .port(-1)
                        .activeConnections(0)
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

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            log.info("LED netty server started on port {}", boundPort);
            return status("Netty server started");
        } catch (Exception ex) {
            log.error("start netty server failed: {}", ex.getMessage(), ex);
            safeStopInternal();
            return LedNettyServerStatusResponse.builder()
                    .running(false)
                    .port(-1)
                    .activeConnections(0)
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
                        .message("Netty server is not running")
                        .build();
            }
            safeStopInternal();
            return LedNettyServerStatusResponse.builder()
                    .running(false)
                    .port(-1)
                    .activeConnections(0)
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

    public LedNettyClientListResponse listClients() {
        List<String> clients = serverHandler.listActiveRemoteAddresses();
        return LedNettyClientListResponse.builder()
                .running(isRunning())
                .totalClients(clients.size())
                .clients(clients)
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
        LedNettyServerHandler.SendResult result = serverHandler.sendTo(request.getRemoteAddress(), data);
        int failedCount = result.totalTargets() - result.successCount();
        return LedNettySendResponse.builder()
                .success(result.totalTargets() > 0 && failedCount == 0)
                .message(result.totalTargets() == 0 ? "Target client not found" : "Send completed")
                .totalTargets(result.totalTargets())
                .successCount(result.successCount())
                .failedCount(failedCount)
                .failedTargets(new ArrayList<>(result.failedTargets()))
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
        LedNettyServerHandler.SendResult result = serverHandler.broadcast(data);
        int failedCount = result.totalTargets() - result.successCount();
        return LedNettySendResponse.builder()
                .success(result.totalTargets() > 0 && failedCount == 0)
                .message(result.totalTargets() == 0 ? "No active clients" : "Broadcast completed")
                .totalTargets(result.totalTargets())
                .successCount(result.successCount())
                .failedCount(failedCount)
                .failedTargets(new ArrayList<>(result.failedTargets()))
                .build();
    }

    private LedNettyServerStatusResponse status(String message) {
        return LedNettyServerStatusResponse.builder()
                .running(isRunning())
                .port(isRunning() ? boundPort : -1)
                .activeConnections(serverHandler.getActiveConnectionCount())
                .message(message)
                .build();
    }

    private void safeStopInternal() {
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
