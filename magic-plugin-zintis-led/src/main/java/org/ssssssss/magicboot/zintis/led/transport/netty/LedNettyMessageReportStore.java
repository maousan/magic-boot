package org.ssssssss.magicboot.zintis.led.transport.netty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class LedNettyMessageReportStore {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<NettyReceiveReport>> REPORT_LIST_TYPE = new TypeReference<>() {
    };
    private static final int DEFAULT_MAX_REPORTS = 1000;
    private static final Path DEFAULT_REPORT_FILE = Paths.get(
            System.getProperty("user.dir"),
            "data",
            "zintis-led",
            "netty-recv-report.json"
    );

    private final ReentrantLock fileLock = new ReentrantLock();
    private final Path reportFile;
    private final int maxReports;

    LedNettyMessageReportStore() {
        this(DEFAULT_REPORT_FILE, DEFAULT_MAX_REPORTS);
    }

    LedNettyMessageReportStore(Path reportFile, int maxReports) {
        this.reportFile = reportFile;
        this.maxReports = Math.max(1, maxReports);
    }

    void save(NettyReceiveReport report) throws IOException {
        fileLock.lock();
        try {
            Files.createDirectories(reportFile.getParent());
            List<NettyReceiveReport> reports = readAllInternal();
            reports.add(report);
            if (reports.size() > maxReports) {
                int removeCount = reports.size() - maxReports;
                reports = new ArrayList<>(reports.subList(removeCount, reports.size()));
            }
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(reportFile.toFile(), reports);
        } finally {
            fileLock.unlock();
        }
    }

    List<NettyReceiveReport> readAll() throws IOException {
        fileLock.lock();
        try {
            return readAllInternal();
        } finally {
            fileLock.unlock();
        }
    }

    private List<NettyReceiveReport> readAllInternal() throws IOException {
        if (Files.notExists(reportFile) || Files.size(reportFile) == 0) {
            return new ArrayList<>();
        }
        List<NettyReceiveReport> reports = OBJECT_MAPPER.readValue(reportFile.toFile(), REPORT_LIST_TYPE);
        return reports == null ? new ArrayList<>() : new ArrayList<>(reports);
    }

    record NettyReceiveReport(
            String timestamp,
            String remoteAddress,
            String hex,
            String payloadAscii,
            String mac,
            String ip,
            String crc
    ) {
    }
}

