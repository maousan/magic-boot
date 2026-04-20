package org.ssssssss.magicboot.zintis.led.protocol;

import org.springframework.stereotype.Component;

@Component
public class Crc16Modbus {

    public int calculate(byte[] data) {
        return calculate(data, 0, data.length);
    }

    public int calculate(byte[] data, int offset, int length) {
        int crc = 0xFFFF;
        for (int i = offset; i < offset + length; i++) {
            crc ^= data[i] & 0xFF;
            for (int bit = 0; bit < 8; bit++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc = crc >>> 1;
                }
            }
        }
        return crc & 0xFFFF;
    }

    public byte[] appendCrcHighLow(byte[] payload) {
        int crc = calculate(payload);
        byte[] frame = new byte[payload.length + 2];
        System.arraycopy(payload, 0, frame, 0, payload.length);
        frame[payload.length] = (byte) ((crc >> 8) & 0xFF);
        frame[payload.length + 1] = (byte) (crc & 0xFF);
        return frame;
    }

    public boolean isValidFrame(byte[] frame) {
        if (frame == null || frame.length < 3) {
            return false;
        }
        int expected = calculate(frame, 0, frame.length - 2);
        int actual = ((frame[frame.length - 2] & 0xFF) << 8) | (frame[frame.length - 1] & 0xFF);
        return expected == actual;
    }
}