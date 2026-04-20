package org.ssssssss.magicboot.zintis.led.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Crc16ModbusTest {

    private final Crc16Modbus crc16Modbus = new Crc16Modbus();

    @Test
    void calculate_shouldMatchKnownModbusVector() {
        byte[] payload = new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x0A};

        int crc = crc16Modbus.calculate(payload);

        assertEquals(0xCDC5, crc);
    }

    @Test
    void appendAndValidate_shouldWork() {
        byte[] payload = new byte[]{(byte) 0xEA, (byte) 0x35, (byte) 0xF5};

        byte[] frame = crc16Modbus.appendCrcHighLow(payload);

        assertTrue(crc16Modbus.isValidFrame(frame));
        frame[0] = (byte) 0xEB;
        assertFalse(crc16Modbus.isValidFrame(frame));
    }
}