package org.ssssssss.magicboot.zintis.led.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LedProtocolCodecTest {

    private final LedProtocolCodec codec = new LedProtocolCodec(new Crc16Modbus());

    @Test
    void buildControlFrame_shouldAppendCrcAndParseBack() {
        byte[] frame = codec.buildControlFrame(0xEA, LedCommandConstants.CMD_CIRCUIT_OFF, (byte) 0xF5);

        LedProtocolCodec.ParsedFrame parsed = codec.parseFrame(frame);

        assertEquals(0xEA, parsed.getHostAddress());
        assertEquals(LedCommandConstants.CMD_CIRCUIT_OFF & 0xFF, parsed.getControlCommand());
        assertEquals(0xF5, parsed.getDataCommand());
    }

    @Test
    void buildFrame_shouldKeepPayload() {
        byte[] frame = codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xF5, new byte[]{0x01, 0x02});

        LedProtocolCodec.ParsedFrame parsed = codec.parseFrame(frame);

        assertArrayEquals(new byte[]{0x01, 0x02}, parsed.getPayload());
    }

    @Test
    void parseFrame_shouldThrowWhenCrcInvalid() {
        byte[] frame = codec.buildControlFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xF5);
        frame[4] = (byte) (frame[4] ^ 0x01);

        assertThrows(IllegalArgumentException.class, () -> codec.parseFrame(frame));
    }
}