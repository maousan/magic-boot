package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.protocol.Crc16Modbus;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedNettyFrameDecoderTest {

    private final Crc16Modbus crc16Modbus = new Crc16Modbus();

    @Test
    void decode_shouldExtractFrame_afterNoiseBytes() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());
        byte[] frame = buildFrame((byte) 0xC6, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS);
        byte[] noiseAndFrame = concat(new byte[]{0x00, 0x7F}, frame);

        assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(noiseAndFrame)));
        ByteBuf decoded = channel.readInbound();

        byte[] actual = new byte[decoded.readableBytes()];
        decoded.readBytes(actual);
        decoded.release();
        assertArrayEquals(frame, actual);
        assertNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void decode_shouldWaitUntilFrameComplete_whenPartialBytesArrive() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());
        byte[] frame = buildFrame((byte) 0xC6, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS);

        channel.writeInbound(Unpooled.wrappedBuffer(frame, 0, 3));
        assertNull(channel.readInbound());

        assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(frame, 3, frame.length - 3)));
        ByteBuf decoded = channel.readInbound();
        byte[] actual = new byte[decoded.readableBytes()];
        decoded.readBytes(actual);
        decoded.release();
        assertArrayEquals(frame, actual);
        channel.finishAndReleaseAll();
    }

    @Test
    void decode_shouldDropFrame_whenControlCommandIsUnknown() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());
        byte[] invalidControlFrame = buildFrame((byte) 0xC6, (byte) 0x01, LedCommandConstants.RESP_SUCCESS);

        channel.writeInbound(Unpooled.wrappedBuffer(invalidControlFrame));
        assertNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    private byte[] buildFrame(byte hostAddress, byte controlCommand, byte dataCommand) {
        byte[] body = new byte[]{hostAddress, controlCommand, dataCommand};
        return crc16Modbus.appendCrcHighLow(body);
    }

    private byte[] concat(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }
}
