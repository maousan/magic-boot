package org.ssssssss.magicboot.zintis.led.protocol;

public final class LedCommandConstants {

    private LedCommandConstants() {
    }

    public static final int DEFAULT_DEVICE_PORT = 9527;
    public static final int DEFAULT_TIMEOUT_MS = 3000;

    public static final byte CMD_QUERY = (byte) 0xAB;
    public static final byte CMD_CIRCUIT_ON = (byte) 0x35;
    public static final byte CMD_CIRCUIT_OFF = (byte) 0xFA;
    public static final byte CMD_MOMENTARY = (byte) 0x36;
    public static final byte CMD_WIFI_CONFIG = (byte) 0xAF;
    public static final byte CMD_TCP_CONFIG = (byte) 0xBF;
    public static final byte CMD_AUTH_DOWNLOAD = (byte) 0xCB;
    public static final byte CMD_OTA = (byte) 0x9F;

    public static final byte SUB_CLOSE_TCP_SERVER = (byte) 0x2E;
    public static final byte SUB_OPEN_TCP_SERVER = (byte) 0x23;
    public static final byte SUB_OPEN_TCP_CLIENT = (byte) 0x33;
    public static final byte SUB_CLOSE_TCP_CLIENT = (byte) 0x3E;

    public static final byte RESP_SUCCESS = (byte) 0xCF;
    public static final byte RESP_ERROR = (byte) 0x0E;
}
