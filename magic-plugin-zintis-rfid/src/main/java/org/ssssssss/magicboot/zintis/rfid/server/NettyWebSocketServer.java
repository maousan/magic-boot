package org.ssssssss.magicboot.zintis.rfid.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class NettyWebSocketServer {

    private final RfidWebSocketProperties properties;
    private final DeviceManager deviceManager;
    private final MessageService messageService;

    private final ReentrantLock lock = new ReentrantLock();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;

    @PostConstruct
    public void autoStart() {
        start(properties.getPort());
    }

    @PreDestroy
    public void shutdown() {
        deviceManager.sendDisconnectAll();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        stop();
    }

    public void start(int port) {
        lock.lock();
        try {
            if (isRunning()) {
                log.info("WebSocket server already running on port {}", boundPort);
                return;
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
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(properties.getMaxFrameSize()))
                                    .addLast(new IdleStateHandler(properties.getIdleTimeoutSeconds(), 0, 0))
                                    .addLast(new WebSocketServerProtocolHandler("/ws", null, true, properties.getMaxFrameSize()))
                                    .addLast(new WebSocketServerHandler(deviceManager, messageService, properties.getMaxConnections()));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            log.info("RFID WebSocket server started on port {}", boundPort);
        } catch (Exception e) {
            log.error("Failed to start WebSocket server: {}", e.getMessage(), e);
            safeStopInternal();
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            if (!isRunning()) return;
            safeStopInternal();
            log.info("RFID WebSocket server stopped");
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return serverChannel != null && serverChannel.isOpen() && serverChannel.isActive();
    }

    public int getBoundPort() {
        return boundPort;
    }

    private void safeStopInternal() {
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
