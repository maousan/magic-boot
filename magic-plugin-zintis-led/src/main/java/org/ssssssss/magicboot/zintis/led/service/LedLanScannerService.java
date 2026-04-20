package org.ssssssss.magicboot.zintis.led.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanDeviceInfo;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedLanScannerService {

    private final LedControlService ledControlService;

    public LedLanScanResponse scan(LedLanScanRequest request) {
        String subnetPrefix = resolveSubnetPrefix(request.getSubnetPrefix());
        if (subnetPrefix == null) {
            return LedLanScanResponse.builder()
                    .success(false)
                    .message("Failed to detect local subnet, please provide subnetPrefix")
                    .devices(List.of())
                    .build();
        }

        int startHost = request.getStartHost();
        int endHost = request.getEndHost();
        if (startHost > endHost) {
            return LedLanScanResponse.builder()
                    .success(false)
                    .message("startHost must be <= endHost")
                    .subnetPrefix(subnetPrefix)
                    .devices(List.of())
                    .build();
        }

        ExecutorService executor = Executors.newFixedThreadPool(request.getThreadPoolSize());
        try {
            List<Future<Optional<LedLanDeviceInfo>>> futures = new ArrayList<>();
            for (int host = startHost; host <= endHost; host++) {
                final int finalHost = host;
                futures.add(executor.submit(scanOne(subnetPrefix, finalHost, request)));
            }

            List<LedLanDeviceInfo> found = new ArrayList<>();
            for (Future<Optional<LedLanDeviceInfo>> future : futures) {
                try {
                    future.get().ifPresent(found::add);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("scan interrupted: {}", e.getMessage());
                } catch (ExecutionException e) {
                    log.debug("scan task failed: {}", e.getMessage());
                }
            }

            found.sort(Comparator.comparing(LedLanDeviceInfo::getIpAddress));

            int scannedCount = endHost - startHost + 1;
            return LedLanScanResponse.builder()
                    .success(true)
                    .message("Scan completed")
                    .subnetPrefix(subnetPrefix)
                    .scannedCount(scannedCount)
                    .matchedCount(found.size())
                    .devices(found)
                    .build();
        } finally {
            executor.shutdownNow();
        }
    }

    private Callable<Optional<LedLanDeviceInfo>> scanOne(
            String subnetPrefix,
            int host,
            LedLanScanRequest request
    ) {
        return () -> {
            String ip = subnetPrefix + "." + host;
            LedControlRequest controlRequest = new LedControlRequest();
            controlRequest.setDeviceIp(ip);
            controlRequest.setDevicePort(request.getDevicePort());
            controlRequest.setHostAddress(host);
            controlRequest.setDataCommand(request.getQueryDataCommand());
            controlRequest.setTimeoutMs(request.getTimeoutMs());

            LedSystemInfoResponse response = ledControlService.querySystemInfo(controlRequest);
            if (!response.isSuccess() || response.getPayloadAscii() == null) {
                return Optional.empty();
            }

            String deviceName = response.getPayloadAscii().trim();
            if (!startsWithEsp32(deviceName)) {
                return Optional.empty();
            }

            return Optional.of(LedLanDeviceInfo.builder()
                    .ipAddress(ip)
                    .hostAddress(host)
                    .deviceName(deviceName)
                    .payloadHex(response.getPayloadHex())
                    .build());
        };
    }

    private boolean startsWithEsp32(String deviceName) {
        return deviceName.toUpperCase(Locale.ROOT).startsWith("ESP32");
    }

    private String resolveSubnetPrefix(String requestPrefix) {
        if (requestPrefix != null && !requestPrefix.isBlank()) {
            String trimmed = requestPrefix.trim();
            if (trimmed.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
                return trimmed;
            }
            return null;
        }

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            if (!(localHost instanceof Inet4Address)) {
                return null;
            }
            String[] parts = localHost.getHostAddress().split("\\.");
            if (parts.length != 4) {
                return null;
            }
            return parts[0] + "." + parts[1] + "." + parts[2];
        } catch (Exception exception) {
            log.warn("detect subnet failed: {}", exception.getMessage());
            return null;
        }
    }
}
