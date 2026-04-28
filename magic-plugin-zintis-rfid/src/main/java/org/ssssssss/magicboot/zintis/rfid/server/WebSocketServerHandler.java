package org.ssssssss.magicboot.zintis.rfid.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;

@Slf4j
@RequiredArgsConstructor
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final AttributeKey<String> DEVICE_ID_KEY = AttributeKey.valueOf("deviceId");

    private final DeviceManager deviceManager;
    private final MessageService messageService;
    private final int maxConnections;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete handshake) {
            String deviceId = extractDeviceId(handshake.requestUri());
            if (deviceId == null || deviceId.isEmpty()) {
                log.warn("No deviceId in WebSocket URL, closing connection");
                ctx.close();
                return;
            }

            if (deviceManager.getActiveCount() >= maxConnections) {
                log.warn("Max connections ({}) reached, rejecting device: {}", maxConnections, deviceId);
                ctx.close();
                return;
            }

            ctx.channel().attr(DEVICE_ID_KEY).set(deviceId);
            deviceManager.register(deviceId, ctx.channel());
            log.info("WebSocket handshake complete, device: {}", deviceId);
            return;
        }

        if (evt instanceof IdleStateEvent idleStateEvent
                && idleStateEvent.state() == IdleState.READER_IDLE) {
            String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
            if (deviceId == null) {
                log.warn("Read idle timeout for unregistered WebSocket connection from {}, closing",
                        ctx.channel().remoteAddress());
                ctx.close();
                return;
            }
            log.warn("Read idle timeout for device: {}, closing", deviceId);
            ctx.close();
            return;
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String text = frame.text();
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.debug("Received from {}: {}", deviceId, text);

        deviceManager.updateActivity(deviceId);

        String reply = messageService.handleMessage(text, deviceId);
        if (reply != null) {
            ctx.writeAndFlush(new TextWebSocketFrame(reply));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        if (deviceId != null) {
            deviceManager.unregister(deviceId);
            log.info("Device disconnected: {}", deviceId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.error("Channel exception for device {}: {}", deviceId, cause.getMessage());
        ctx.close();
    }

    private String extractDeviceId(String requestUri) {
        if (requestUri == null) return null;
        int queryIndex = requestUri.indexOf('?');
        if (queryIndex < 0) return null;
        String query = requestUri.substring(queryIndex + 1);
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if ("deviceId".equals(kv[0]) && kv.length == 2) {
                return kv[1];
            }
        }
        return null;
    }
}
