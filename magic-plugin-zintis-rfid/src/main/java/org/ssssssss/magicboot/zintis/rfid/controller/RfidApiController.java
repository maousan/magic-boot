package org.ssssssss.magicboot.zintis.rfid.controller;

import lombok.RequiredArgsConstructor;
import org.ssssssss.magicboot.zintis.rfid.config.RfidSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.model.DeviceInfo;
import org.ssssssss.magicboot.zintis.rfid.server.NettyTcpServer;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rfid")
@RequiredArgsConstructor
public class RfidApiController {

    private final NettyTcpServer server;
    private final RfidSocketProperties properties;
    private final DeviceManager deviceManager;
    private final MessageService messageService;

    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody(required = false) Map<String, Object> request) {
        int port = server.getBoundPort() > 0 ? server.getBoundPort() : properties.getPort();
        if (request != null && request.get("port") instanceof Number number) {
            port = number.intValue();
        }
        server.start(port);
        return status();
    }

    @PostMapping("/stop")
    public Map<String, Object> stop() {
        server.stop();
        return status();
    }

    @PostMapping("/command")
    public Map<String, Object> sendCommand(@RequestBody Map<String, Object> request) {
        String deviceId = (String) request.get("deviceId");
        String command = (String) request.get("command");
        Object params = request.get("params");

        DeviceInfo device = deviceManager.getDevice(deviceId);
        boolean online = device != null && device.getChannel().isActive();

        String json = messageService.buildCommandMessage(command, params);
        if (json == null) {
            return Map.of("success", false, "status", "error", "message", "Failed to build command");
        }

        if (online) {
            deviceManager.sendTo(deviceId, json);
            return Map.of(
                    "success", true,
                    "msgId", extractMsgId(json),
                    "status", "delivered",
                    "message", "指令已送达设备"
            );
        } else {
            deviceManager.sendTo(deviceId, json);
            return Map.of(
                    "success", true,
                    "msgId", extractMsgId(json),
                    "status", "pending",
                    "message", "设备离线，指令已缓存"
            );
        }
    }

    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(@RequestBody Map<String, Object> request) {
        String command = (String) request.get("command");
        Object params = request.get("params");

        String json = messageService.buildCommandMessage(command, params);
        if (json == null) {
            return Map.of("success", false, "message", "Failed to build command");
        }

        List<DeviceInfo> devices = deviceManager.getAllDevices();
        int total = devices.size();
        int success = 0;
        for (DeviceInfo device : devices) {
            if (device.getChannel().isActive()) {
                deviceManager.sendTo(device.getDeviceId(), json);
                success++;
            }
        }

        return Map.of(
                "success", success > 0,
                "totalTargets", total,
                "successCount", success,
                "failedCount", total - success,
                "failedTargets", List.of()
        );
    }

    @GetMapping("/devices")
    public Map<String, Object> listDevices() {
        List<Map<String, Object>> deviceList = deviceManager.getAllDevices().stream()
                .map(d -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("deviceId", d.getDeviceId());
                    m.put("remoteAddress", d.getRemoteAddress());
                    m.put("connectedAt", d.getConnectedAt());
                    m.put("lastActiveAt", d.getLastActiveAt());
                    return m;
                })
                .collect(Collectors.toList());

        return Map.of(
                "running", server.isRunning(),
                "devices", deviceList
        );
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "running", server.isRunning(),
                "port", server.isRunning() ? server.getBoundPort() : -1,
                "activeConnections", deviceManager.getActiveCount()
        );
    }

    private String extractMsgId(String json) {
        try {
            int start = json.indexOf("\"msgId\":\"") + 9;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}
