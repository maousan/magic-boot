package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyBroadcastRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyClientListResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

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

    @Test
    void heartbeat_shouldSendFixedFrameToConnectedClient() throws Exception {
        service.setHeartbeatEnabled(true);
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(5000);
            waitUntilClientConnected();

            byte[] heartbeat = socket.getInputStream().readNBytes(6);

            assertArrayEquals(new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82}, heartbeat);
        }
    }

    @Test
    void heartbeat_shouldNotSendFrameWhenDisabled() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());
        assertFalse(start.isHeartbeatEnabled());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3500);
            waitUntilClientConnected();

            assertThrows(SocketTimeoutException.class, () -> socket.getInputStream().read());
        }
    }

    @Test
    void updateHeartbeat_shouldStartHeartbeatWhenServerIsRunning() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(5000);
            waitUntilClientConnected();

            LedNettyServerStatusResponse heartbeatStatus = service.updateHeartbeat(true);
            byte[] heartbeat = socket.getInputStream().readNBytes(6);

            assertTrue(heartbeatStatus.isHeartbeatEnabled());
            assertTrue(heartbeatStatus.isHeartbeatRunning());
            assertArrayEquals(new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82}, heartbeat);

            LedNettyServerStatusResponse status = waitUntilHeartbeatRecorded();
            assertNotNull(status.getLastHeartbeatAt());
            assertEquals(1, status.getLastHeartbeatTargets());
            assertEquals(1, status.getLastHeartbeatSuccessCount());
            assertEquals(0, status.getLastHeartbeatFailedCount());
        }
    }

    @Test
    void updateClientReportRegistration_shouldUpdateStatus() {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isClientReportRegistrationEnabled());

        LedNettyServerStatusResponse disabled = service.updateClientReportRegistration(false);
        LedNettyServerStatusResponse enabled = service.updateClientReportRegistration(true);

        assertFalse(disabled.isClientReportRegistrationEnabled());
        assertTrue(enabled.isClientReportRegistrationEnabled());
    }

    @Test
    void broadcast_shouldRespectMinimumOutboundSendInterval() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();

            LedNettyBroadcastRequest first = broadcastBytes(1);
            LedNettyBroadcastRequest second = broadcastBytes(2);

            LedNettySendResponse firstResponse = service.broadcast(first);
            int firstByte = socket.getInputStream().read();
            long startMillis = System.currentTimeMillis();
            LedNettySendResponse secondResponse = service.broadcast(second);
            long elapsed = System.currentTimeMillis() - startMillis;
            int secondByte = socket.getInputStream().read();

            assertEquals(1, firstResponse.getSuccessCount());
            assertEquals(1, secondResponse.getSuccessCount());
            assertEquals(1, firstByte);
            assertEquals(2, secondByte);
            assertTrue(elapsed >= 450, "second outbound send should wait near the 500ms interval");
        }
    }

    @Test
    void clientHeartbeat_shouldReceiveSameHeartbeatReply() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();
            byte[] heartbeat = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};

            socket.getOutputStream().write(heartbeat);
            socket.getOutputStream().flush();
            byte[] reply = socket.getInputStream().readNBytes(6);

            assertArrayEquals(heartbeat, reply);
        }
    }

    @Test
    void clientReportFrame_shouldReceiveHeartbeatReply() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();
            byte[] heartbeat = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};

            socket.getOutputStream().write(clientReportFrame());
            socket.getOutputStream().flush();
            byte[] reply = socket.getInputStream().readNBytes(6);

            assertArrayEquals(heartbeat, reply);
        }
    }

    @Test
    void sendToClient_shouldCacheFailedCommandAndRetryAfterClientReport() throws Exception {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());
        LedNettySendRequest request = new LedNettySendRequest();
        request.setRemoteAddress("127.0.0.1:1");
        request.setMacAddress("3A:69:7A:08:D0:A5");
        request.setPayloadArray(List.of(0x55));

        LedNettySendResponse failed = service.sendToClient(request);
        assertFalse(failed.isSuccess());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(5000);
            waitUntilClientConnected();
            socket.getOutputStream().write(clientReportFrame());
            socket.getOutputStream().flush();

            byte[] heartbeat = socket.getInputStream().readNBytes(6);
            int retriedCommand = socket.getInputStream().read();

            assertArrayEquals(new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82}, heartbeat);
            assertEquals(0x55, retriedCommand);
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

    private LedNettyServerStatusResponse waitUntilHeartbeatRecorded() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() < deadline) {
            LedNettyServerStatusResponse status = service.status();
            if (status.getLastHeartbeatAt() != null) {
                return status;
            }
            Thread.sleep(20);
        }
        return service.status();
    }

    private LedNettyBroadcastRequest broadcastBytes(int value) {
        LedNettyBroadcastRequest request = new LedNettyBroadcastRequest();
        request.setPayloadArray(List.of(value));
        return request;
    }

    private byte[] clientReportFrame() {
        return new byte[]{
                0x66, (byte) 0xAB, (byte) 0x97,
                0x11, 0x0D,
                0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x44, 0x30, 0x3A, 0x41, 0x35,
                0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x32, 0x2E, 0x31, 0x30, 0x32,
                (byte) 0x90, 0x78
        };
    }
}
