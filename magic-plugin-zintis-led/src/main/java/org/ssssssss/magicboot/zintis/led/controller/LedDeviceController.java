package org.ssssssss.magicboot.zintis.led.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedControlResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedQueryResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSignalStrengthResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemNetworkResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStartRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedTcpConfigRequest;
import org.ssssssss.magicboot.zintis.led.service.LedControlService;
import org.ssssssss.magicboot.zintis.led.service.LedLanScannerService;
import org.ssssssss.magicboot.zintis.led.service.LedNettyServerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/led")
@RequiredArgsConstructor
public class LedDeviceController {

    private final LedControlService ledControlService;
    private final LedLanScannerService ledLanScannerService;
    private final LedNettyServerService ledNettyServerService;

    @PostMapping("/control/on")
    public LedControlResponse turnOn(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.turnOn(request);
    }

    @PostMapping("/control/off")
    public LedControlResponse turnOff(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.turnOff(request);
    }

    @PostMapping("/control/pulse")
    public LedControlResponse pulse(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.pulse(request);
    }

    @PostMapping("/control/query")
    public LedQueryResponse query(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.query(request);
    }

    @PostMapping("/system/info")
    public LedSystemInfoResponse querySystemInfo(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySystemInfo(request);
    }

    @PostMapping("/system/signal-strength")
    public LedSignalStrengthResponse querySignalStrength(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySignalStrength(request);
    }

    @PostMapping("/system/network")
    public LedSystemNetworkResponse querySystemNetwork(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySystemNetwork(request);
    }

    @PostMapping("/tcp/server/open")
    public LedControlResponse openTcpServer(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.openTcpServer(request);
    }

    @PostMapping("/tcp/server/close")
    public LedControlResponse closeTcpServer(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.closeTcpServer(request);
    }

    @PostMapping("/tcp/client/open")
    public LedControlResponse openTcpClient(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.openTcpClient(request);
    }

    @PostMapping("/tcp/client/close")
    public LedControlResponse closeTcpClient(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.closeTcpClient(request);
    }

    @PostMapping("/lan/scan")
    public LedLanScanResponse scanLan(@Valid @RequestBody LedLanScanRequest request) {
        return ledLanScannerService.scan(request);
    }

    @PostMapping("/netty/server/start")
    public LedNettyServerStatusResponse startNettyServer(@RequestBody(required = false) LedNettyServerStartRequest request) {
        Integer port = request == null ? null : request.getPort();
        return ledNettyServerService.start(port);
    }

    @PostMapping("/netty/server/stop")
    public LedNettyServerStatusResponse stopNettyServer() {
        return ledNettyServerService.stop();
    }

    @GetMapping("/netty/server/status")
    public LedNettyServerStatusResponse nettyServerStatus() {
        return ledNettyServerService.status();
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return ledControlService.health();
    }
}
