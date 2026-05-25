package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyClientListResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;

import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedNettyServerServiceTest {

    private final LedNettyServerService service = new LedNettyServerService();

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void startAndStop_shouldUpdateServerStatus() {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());
        assertTrue(start.getPort() > 0);

        LedNettyServerStatusResponse status = service.status();
        assertTrue(status.isRunning());
        assertTrue(status.getPort() > 0);

        LedNettyServerStatusResponse stop = service.stop();
        assertFalse(stop.isRunning());
    }

    @Test
    void parsePayload_shouldSupportAsciiAndHex() {
        assertArrayEquals("HELLO".getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                LedNettyServerService.parsePayload("HELLO", null, "ascii"));
        assertArrayEquals(new byte[]{0x66, (byte) 0xAB, (byte) 0x97},
                LedNettyServerService.parsePayload("66 AB 97", null, "hex"));
        assertArrayEquals(new byte[]{0x66, (byte) 0xAB, (byte) 0x97},
                LedNettyServerService.parsePayload("0x66,0xAB,0x97", null, "hex"));
        assertArrayEquals(new byte[]{0x66, 0x35, (byte) 0xBA, 0x3C, 0x07},
                LedNettyServerService.parsePayload("0x66 0x35 0xBA 0x3C 0x07", null, "hex"));
    }

    @Test
    void parsePayload_shouldSupportNumericArrayFormat() {
        assertArrayEquals(new byte[]{0x66, 0x35, (byte) 0xBA, 0x3C, 0x07},
                LedNettyServerService.parsePayload(null, java.util.List.of(102, 53, 186, 60, 7), "hex"));
    }

    @Test
    void parsePayload_shouldRejectInvalidHex() {
        assertThrows(IllegalArgumentException.class, () -> LedNettyServerService.parsePayload("6AB", null, "hex"));
        assertThrows(IllegalArgumentException.class, () -> LedNettyServerService.parsePayload("GG", null, "hex"));
        assertThrows(IllegalArgumentException.class, () -> LedNettyServerService.parsePayload(null, java.util.List.of(1, 256), "hex"));
        assertThrows(IllegalArgumentException.class, () -> LedNettyServerService.parsePayload(null, java.util.Arrays.asList(1, null), "hex"));
        assertThrows(IllegalArgumentException.class, () -> LedNettyServerService.parsePayload(null, java.util.List.of(), "hex"));
    }

    @Test
    void listClients_shouldReturnEmptyList_whenNoClientConnected() {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        LedNettyClientListResponse response = service.listClients();
        assertTrue(response.isRunning());
        assertEquals(0, response.getTotalClients());
        assertNotNull(response.getClients());
        assertTrue(response.getClients().isEmpty());
        assertNotNull(response.getClientDetails());
        assertTrue(response.getClientDetails().isEmpty());
    }

    @Test
    void stop_shouldCloseAcceptedClientConnection() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(1000);
            waitUntilClientConnected();

            service.stop();

            int read = socket.getInputStream().read();
            assertEquals(-1, read);
        } catch (SocketTimeoutException exception) {
            throw new AssertionError("client did not observe server-side close within timeout", exception);
        }
    }

    private void waitUntilClientConnected() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline) {
            if (service.listClients().getTotalClients() > 0) {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError("client was not registered as active");
    }
}
