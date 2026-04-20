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
    private static final int MAX_FRAME_LENGTH = 512;
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
            in.readByte();
        }
    }

    private int findFrameLength(byte[] window) {
        for (int frameLen = MIN_FRAME_LENGTH; frameLen <= window.length; frameLen++) {
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
}
