package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;

import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Sharable
public class LedNettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Set<SocketAddress> activeConnections = ConcurrentHashMap.newKeySet();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        activeConnections.add(ctx.channel().remoteAddress());
        log.info("LED netty server connection active: {}", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        activeConnections.remove(ctx.channel().remoteAddress());
        log.info("LED netty server connection inactive: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf byteBuf) {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            byteBuf.release();
            log.info("LED netty server recv from {}: {}", ctx.channel().remoteAddress(), LedProtocolCodec.toHex(data));
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        activeConnections.remove(ctx.channel().remoteAddress());
        log.warn("LED netty server channel exception: {}", cause.getMessage());
        ctx.close();
    }

    public int getActiveConnectionCount() {
        return activeConnections.size();
    }

    public void resetActiveConnections() {
        activeConnections.clear();
    }
}
