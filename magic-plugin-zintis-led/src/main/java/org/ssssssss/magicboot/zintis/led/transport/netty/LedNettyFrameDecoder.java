package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.ssssssss.magicboot.zintis.led.protocol.Crc16Modbus;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

import java.util.List;
import java.util.Set;

public class LedNettyFrameDecoder extends ByteToMessageDecoder {

    private static final int MIN_FRAME_LENGTH = 5;
    private static final int HEARTBEAT_FRAME_LENGTH = 6;
    private static final int MAX_FRAME_LENGTH = 512;
    private static final byte[] HEARTBEAT_FRAME = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};
    private static final Crc16Modbus CRC16 = new Crc16Modbus();
    private static final Set<Integer> VALID_CONTROL_COMMANDS = Set.of(
            LedCommandConstants.CMD_QUERY & 0xFF,
            LedCommandConstants.CMD_CIRCUIT_ON & 0xFF,
            LedCommandConstants.CMD_CIRCUIT_OFF & 0xFF,
            LedCommandConstants.CMD_MOMENTARY & 0xFF,
            LedCommandConstants.CMD_WIFI_CONFIG & 0xFF,
            LedCommandConstants.CMD_TCP_CONFIG & 0xFF,
            LedCommandConstants.CMD_AUTH_DOWNLOAD & 0xFF,
            LedCommandConstants.CMD_OTA & 0xFF
    );

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.readableBytes() >= MIN_FRAME_LENGTH) {
            int readerIndex = in.readerIndex();
            int readable = Math.min(in.readableBytes(), MAX_FRAME_LENGTH);
            byte[] window = new byte[readable];
            in.getBytes(readerIndex, window);
            int frameLen = findFrameLength(window);

            if (frameLen > 0) {
                out.add(in.readRetainedSlice(frameLen));
                continue;
            }
            // 没找到完整帧时：缓冲区头部可能是"尚未到齐"的合法帧（TCP 分段），
            // 此时必须等待更多数据，而不能按垃圾丢弃——否则整帧丢失（MAC 上报帧丢失
            // 会导致设备连接正常但永远无法注册为活跃客户端）。
            // 仅当头部不可能是合法帧前缀时才丢弃一字节重新同步；
            // 缓冲区达到 MAX_FRAME_LENGTH 仍不匹配时强制丢弃，防止坏数据流无限积压。
            if (in.readableBytes() < MAX_FRAME_LENGTH && looksLikeFramePrefix(window)) {
                return;
            }
            in.readByte();
        }
    }

    /**
     * 判断缓冲区头部是否可能是合法帧的前缀（帧未到齐）：
     * - 心跳帧：与 38 46 55 64 73 82 的前若干字节逐字节匹配；
     * - 上报/控制帧：首字节为帧序号/主机地址（不固定），第 2 字节必须是合法控制命令。
     */
    private boolean looksLikeFramePrefix(byte[] window) {
        if (window.length <= HEARTBEAT_FRAME_LENGTH) {
            boolean heartbeatPrefix = true;
            for (int i = 0; i < window.length; i++) {
                if (window[i] != HEARTBEAT_FRAME[i]) {
                    heartbeatPrefix = false;
                    break;
                }
            }
            if (heartbeatPrefix) {
                return true;
            }
        }
        return window.length >= 2 && VALID_CONTROL_COMMANDS.contains(window[1] & 0xFF);
    }

    private int findFrameLength(byte[] window) {
        for (int frameLen = MIN_FRAME_LENGTH; frameLen <= window.length; frameLen++) {
            if (isHeartbeatFrame(window, frameLen)) {
                return HEARTBEAT_FRAME_LENGTH;
            }
            if (!isPotentialFrame(window, frameLen)) {
                continue;
            }
            int expected = CRC16.calculate(window, 0, frameLen - 2);
            int actual = ((window[frameLen - 2] & 0xFF) << 8) | (window[frameLen - 1] & 0xFF);
            if (expected == actual) {
                return frameLen;
            }
        }
        return -1;
    }

    private boolean isPotentialFrame(byte[] frame, int frameLen) {
        if (frameLen < MIN_FRAME_LENGTH) {
            return false;
        }
        int controlCommand = frame[1] & 0xFF;
        return VALID_CONTROL_COMMANDS.contains(controlCommand);
    }

    private boolean isHeartbeatFrame(byte[] window, int frameLen) {
        if (frameLen != HEARTBEAT_FRAME_LENGTH || window.length < HEARTBEAT_FRAME_LENGTH) {
            return false;
        }
        for (int i = 0; i < HEARTBEAT_FRAME_LENGTH; i++) {
            if (window[i] != HEARTBEAT_FRAME[i]) {
                return false;
            }
        }
        return true;
    }
}
