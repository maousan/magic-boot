package org.ssssssss.magicboot.zintis.rfid.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;

import java.net.InetSocketAddress;

@Slf4j
@RequiredArgsConstructor
public class TcpServerHandler extends SimpleChannelInboundHandler<String> {

    private static final AttributeKey<String> DEVICE_ID_KEY = AttributeKey.valueOf("deviceId");

    private final DeviceManager deviceManager;
    private final MessageService messageService;
    private final int maxConnections;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String deviceId = extractRemoteIp(ctx);
        if (deviceId == null || deviceId.isEmpty()) {
            log.warn("No remote IP for TCP connection, closing connection");
            ctx.close();
            return;
        }

        if (deviceManager.getActiveCount() >= maxConnections && deviceManager.getDevice(deviceId) == null) {
            log.warn("Max connections ({}) reached, rejecting device: {}", maxConnections, deviceId);
            ctx.close();
            return;
        }

        ctx.channel().attr(DEVICE_ID_KEY).set(deviceId);
        deviceManager.register(deviceId, ctx.channel());
        log.info("TCP connection registered, device: {}", deviceId);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent idleStateEvent
                && idleStateEvent.state() == IdleState.READER_IDLE) {
            String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
            log.warn("Read idle timeout for device: {}, closing", deviceId);
            ctx.close();
            return;
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String text) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.debug("Received from {}: {}", deviceId, text);

        deviceManager.updateActivity(deviceId);

        if ("ping".equalsIgnoreCase(text.trim())) {
            ctx.writeAndFlush("pong");
            return;
        }

        String reply = messageService.handleMessage(text, deviceId);
        if (reply != null) {
            ctx.writeAndFlush(reply);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        if (deviceId != null) {
            if (deviceManager.unregister(deviceId, ctx.channel())) {
                log.info("Device disconnected: {}", deviceId);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.error("Channel exception for device {}: {}", deviceId, cause.getMessage());
        ctx.close();
    }

    private String extractRemoteIp(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress address) {
            return address.getAddress().getHostAddress();
        }
        return null;
    }
}
