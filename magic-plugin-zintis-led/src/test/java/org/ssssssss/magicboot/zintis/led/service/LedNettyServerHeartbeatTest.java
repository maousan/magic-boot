package org.ssssssss.magicboot.zintis.led.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;
import org.ssssssss.magicboot.zintis.led.transport.netty.LedNettyServerHandler;

import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 厂家规范：服务器须在 6 秒内向已连接客户端发送心跳帧 38 46 55 64 73 82（建议每 3 秒一次）。
 * 设备若 6 秒内收不到服务器心跳会主动断开重连（抓包实测：连接后整 6 秒 FIN）。
 */
class LedNettyServerHeartbeatTest {

    private static final byte[] HEARTBEAT_FRAME = new byte[]{0x38, 0x46, 0x55, 0x64, 0x73, (byte) 0x82};

    private final LedNettyServerService service = new LedNettyServerService();

    @AfterEach
    void tearDown() {
        service.stop();
    }

    @Test
    void heartbeat_shouldBroadcastPeriodically_whenEnabled() throws Exception {
        service.setHeartbeatIntervalSeconds(1);
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();

            LedNettyServerStatusResponse enabled = service.updateHeartbeat(true);
            assertTrue(enabled.isHeartbeatRunning(), "开启心跳后 heartbeatRunning 应为 true");

            // 周期性收到心跳帧（至少连续两帧，证明是周期任务而非一次性）
            assertArrayEquals(HEARTBEAT_FRAME, socket.getInputStream().readNBytes(6));
            assertArrayEquals(HEARTBEAT_FRAME, socket.getInputStream().readNBytes(6));
        }
    }

    @Test
    void heartbeat_shouldStopBroadcasting_whenDisabled() throws Exception {
        service.setHeartbeatIntervalSeconds(1);
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();

            service.updateHeartbeat(true);
            assertArrayEquals(HEARTBEAT_FRAME, socket.getInputStream().readNBytes(6));

            LedNettyServerStatusResponse disabled = service.updateHeartbeat(false);
            assertFalse(disabled.isHeartbeatRunning(), "关闭心跳后 heartbeatRunning 应为 false");

            Thread.sleep(300); // 让可能在途的最后一帧落地
            socket.setSoTimeout(2500);
            assertThrows(SocketTimeoutException.class, () -> socket.getInputStream().read(),
                    "关闭心跳后不应再收到心跳帧");
        }
    }

    @Test
    void heartbeat_shouldStop_whenServerStopped() throws Exception {
        service.setHeartbeatIntervalSeconds(1);
        LedNettyServerStatusResponse start = service.start(0);
        try (Socket socket = new Socket("127.0.0.1", start.getPort())) {
            socket.setSoTimeout(3000);
            waitUntilClientConnected();
            service.updateHeartbeat(true);
            assertArrayEquals(HEARTBEAT_FRAME, socket.getInputStream().readNBytes(6));

            service.stop();

            assertFalse(service.status().isHeartbeatRunning(), "服务停止后 heartbeatRunning 应为 false");
        }
    }

    @Test
    void status_shouldReportHeartbeatNotRunning_byDefault() {
        LedNettyServerStatusResponse start = service.start(0);
        assertTrue(start.isRunning());

        assertFalse(service.status().isHeartbeatRunning());
    }

    @Test
    void broadcastHeartbeat_shouldWriteFrameToActiveChannel() {
        LedNettyServerHandler handler = new LedNettyServerHandler();
        EmbeddedChannel channel = new EmbeddedChannel(handler);

        int sent = handler.broadcastHeartbeat();

        assertEquals(1, sent);
        Object outbound = channel.readOutbound();
        assertEquals(Unpooled.wrappedBuffer(HEARTBEAT_FRAME), outbound);
        channel.finishAndReleaseAll();
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
