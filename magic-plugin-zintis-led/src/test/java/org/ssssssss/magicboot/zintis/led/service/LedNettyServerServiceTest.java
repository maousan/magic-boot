package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
