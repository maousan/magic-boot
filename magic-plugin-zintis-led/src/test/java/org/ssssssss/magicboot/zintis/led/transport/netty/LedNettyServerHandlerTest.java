package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.timeout.IdleStateEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedNettyServerHandlerTest {

    @Test
    void toAsciiText_shouldKeepPrintableAndReplaceNonPrintable() {
        byte[] data = new byte[]{
                0x66, (byte) 0xAB, (byte) 0x97, 0x11, 0x0D,
                0x33, 0x41, 0x3A, 0x36, 0x39
        };

        String text = LedNettyServerHandler.toAsciiText(data);

        assertEquals("f....3A:69", text);
    }

    @Test
    void toAsciiText_shouldReturnEmpty_whenInputIsNullOrEmpty() {
        assertEquals("", LedNettyServerHandler.toAsciiText(null));
        assertEquals("", LedNettyServerHandler.toAsciiText(new byte[0]));
    }

    @Test
    void toPayloadAsciiText_shouldIgnoreHeaderAndCrc() {
        byte[] frame = new byte[]{
                0x66, (byte) 0xAB, (byte) 0x97,
                0x31, 0x39, 0x32, 0x2E, 0x31,
                (byte) 0x90, 0x78
        };

        assertEquals("192.1", LedNettyServerHandler.toPayloadAsciiText(frame));
        assertEquals("9078", LedNettyServerHandler.extractCrcHex(frame));
    }

    @Test
    void extractMacAndIp_shouldSplitLengthPrefixedPayload() {
        byte[] frame = new byte[]{
                0x66, (byte) 0xAB, (byte) 0x97,
                0x11, 0x0D,
                0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x44, 0x30, 0x3A, 0x41, 0x35,
                0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x32, 0x2E, 0x31, 0x30, 0x32,
                (byte) 0x90, 0x78
        };

        byte[] payload = LedNettyServerHandler.extractPayload(frame);
        assertEquals("3A:69:7A:08:D0:A5", LedNettyServerHandler.extractMac(payload));
        assertEquals("192.168.2.102", LedNettyServerHandler.extractIp(payload));
        assertEquals("9078", LedNettyServerHandler.extractCrcHex(frame));
    }

    @Test
    void extractMacAndIp_shouldSupportAsciiFallback() {
        byte[] payload = "IP:192.168.2.198,MAC:AA-BB-CC-DD-EE-FF".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        assertEquals("AA:BB:CC:DD:EE:FF", LedNettyServerHandler.extractMac(payload));
        assertEquals("192.168.2.198", LedNettyServerHandler.extractIp(payload));
    }

    @Test
    void userEventTriggered_shouldKeepChannelOpen_whenReaderIdleTimeout() {
        LedNettyServerHandler handler = new LedNettyServerHandler();
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        channel.pipeline().fireUserEventTriggered(IdleStateEvent.READER_IDLE_STATE_EVENT);

        assertTrue(channel.isActive());
        channel.finishAndReleaseAll();
    }

    @Test
    void channelRead_shouldReportDevice_whenPayloadContainsMac() {
        AtomicReference<String> reportedMac = new AtomicReference<>();
        AtomicReference<String> reportedIp = new AtomicReference<>();
        LedNettyServerHandler handler = new LedNettyServerHandler((macAddress, ipAddress) -> {
            reportedMac.set(macAddress);
            reportedIp.set(ipAddress);
        });
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        byte[] frame = new byte[]{
                0x66, (byte) 0xAB, (byte) 0x97,
                0x11, 0x0D,
                0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x44, 0x30, 0x3A, 0x41, 0x35,
                0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x32, 0x2E, 0x31, 0x30, 0x32,
                (byte) 0x90, 0x78
        };

        channel.writeInbound(Unpooled.wrappedBuffer(frame));

        assertEquals("3A:69:7A:08:D0:A5", reportedMac.get());
        assertEquals("192.168.2.102", reportedIp.get());
        channel.finishAndReleaseAll();
    }
}
