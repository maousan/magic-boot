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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;
import org.ssssssss.magicboot.zintis.led.transport.netty.LedNettyFrameDecoder;
import org.ssssssss.magicboot.zintis.led.transport.netty.LedNettyServerHandler;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class LedNettyServerService {

    public static final int DEFAULT_NETTY_SERVER_PORT = 9834;

    private final ReentrantLock lock = new ReentrantLock();
    private final LedNettyServerHandler serverHandler = new LedNettyServerHandler();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;

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
            if (port < 1 || port > 65535) {
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
}
