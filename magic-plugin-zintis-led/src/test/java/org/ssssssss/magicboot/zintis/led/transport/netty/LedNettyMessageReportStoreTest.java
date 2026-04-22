package org.ssssssss.magicboot.zintis.led.transport.netty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedNettyMessageReportStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void save_shouldPersistReportToJsonFile() throws Exception {
        Path reportFile = tempDir.resolve("netty-recv-report.json");
        LedNettyMessageReportStore store = new LedNettyMessageReportStore(reportFile, 100);

        store.save(new LedNettyMessageReportStore.NettyReceiveReport(
                "2026-04-22T12:00:00Z",
                "/192.168.2.180:9834",
                "66AB97",
                "192.168.2.180:9834",
                "3A:69:7A:08:D0:A5",
                "192.168.2.180",
                "9078"
        ));

        List<LedNettyMessageReportStore.NettyReceiveReport> reports = store.readAll();
        assertEquals(1, reports.size());
        assertEquals("66AB97", reports.get(0).hex());
        assertTrue(java.nio.file.Files.exists(reportFile));
    }

    @Test
    void save_shouldKeepLatestReports_whenExceedMaxSize() throws Exception {
        Path reportFile = tempDir.resolve("netty-recv-report.json");
        LedNettyMessageReportStore store = new LedNettyMessageReportStore(reportFile, 2);

        store.save(new LedNettyMessageReportStore.NettyReceiveReport(
                "t1", "r1", "h1", "p1", "m1", "i1", "c1"
        ));
        store.save(new LedNettyMessageReportStore.NettyReceiveReport(
                "t2", "r2", "h2", "p2", "m2", "i2", "c2"
        ));
        store.save(new LedNettyMessageReportStore.NettyReceiveReport(
                "t3", "r3", "h3", "p3", "m3", "i3", "c3"
        ));

        List<LedNettyMessageReportStore.NettyReceiveReport> reports = store.readAll();
        assertEquals(2, reports.size());
        assertEquals("h2", reports.get(0).hex());
        assertEquals("h3", reports.get(1).hex());
    }
}

