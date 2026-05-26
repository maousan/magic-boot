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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class NettyTcpServer {

    private final RfidSocketProperties properties;
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
                log.info("TCP server already running on port {}", boundPort);
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
                                    .addLast(new IdleStateHandler(properties.getIdleTimeoutSeconds(), 0, 0))
                                    .addLast(new LengthFieldBasedFrameDecoder(
                                            properties.getMaxFrameSize(), 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast(new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast(new TcpServerHandler(deviceManager, messageService, properties.getMaxConnections()));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            log.info("RFID TCP server started on port {}", boundPort);
        } catch (Exception e) {
            log.error("Failed to start TCP server: {}", e.getMessage(), e);
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
            log.info("RFID TCP server stopped");
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
