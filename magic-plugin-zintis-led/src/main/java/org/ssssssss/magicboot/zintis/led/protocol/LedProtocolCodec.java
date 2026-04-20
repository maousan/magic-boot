package org.ssssssss.magicboot.zintis.led.protocol;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LedProtocolCodec {

    private final Crc16Modbus crc16Modbus;

    public LedProtocolCodec(Crc16Modbus crc16Modbus) {
        this.crc16Modbus = crc16Modbus;
    }

    public byte[] buildControlFrame(int hostAddress, byte controlCommand, byte dataCommand) {
        return buildFrame(hostAddress, controlCommand, dataCommand, new byte[0]);
    }

    public byte[] buildFrame(int hostAddress, byte controlCommand, byte dataCommand, byte[] dataPayload) {
        if (hostAddress < 0 || hostAddress > 255) {
            throw new IllegalArgumentException("hostAddress 必须在 0~255 范围内");
        }
        byte[] payload = dataPayload == null ? new byte[0] : dataPayload;
        byte[] frameBody = new byte[3 + payload.length];
        frameBody[0] = (byte) (hostAddress & 0xFF);
        frameBody[1] = controlCommand;
        frameBody[2] = dataCommand;
        System.arraycopy(payload, 0, frameBody, 3, payload.length);
        return crc16Modbus.appendCrcHighLow(frameBody);
    }

    public ParsedFrame parseFrame(byte[] frame) {
        if (frame == null || frame.length < 5) {
            throw new IllegalArgumentException("协议帧长度不足，最小长度为 5 字节");
        }
        if (!crc16Modbus.isValidFrame(frame)) {
            throw new IllegalArgumentException("CRC校验失败");
        }

        int hostAddress = frame[0] & 0xFF;
        int controlCommand = frame[1] & 0xFF;
        int dataCommand = frame[2] & 0xFF;
        int payloadLength = frame.length - 5;
        byte[] payload = new byte[payloadLength];
        if (payloadLength > 0) {
            System.arraycopy(frame, 3, payload, 0, payloadLength);
        }

        return new ParsedFrame(hostAddress, controlCommand, dataCommand, payload, frame);
    }

    public static String toHex(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format(Locale.ROOT, "%02X", b));
        }
        return sb.toString();
    }

    public static String toPrefixedHex(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(data.length * 5);
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append("0x").append(String.format(Locale.ROOT, "%02X", data[i]));
        }
        return sb.toString();
    }

    @Getter
    public static class ParsedFrame {
        private final int hostAddress;
        private final int controlCommand;
        private final int dataCommand;
        private final byte[] payload;
        private final byte[] rawFrame;

        public ParsedFrame(int hostAddress, int controlCommand, int dataCommand, byte[] payload, byte[] rawFrame) {
            this.hostAddress = hostAddress;
            this.controlCommand = controlCommand;
            this.dataCommand = dataCommand;
            this.payload = payload;
            this.rawFrame = rawFrame;
        }

        public String payloadHex() {
            return LedProtocolCodec.toHex(payload);
        }
    }
}
