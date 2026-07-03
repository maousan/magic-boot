package org.ssssssss.magicboot.zintis.led.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedControlResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedQueryResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSignalStrengthResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemNetworkResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedOtaUpdateRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedTcpConfigRequest;
import org.ssssssss.magicboot.zintis.led.service.LedControlService;
import org.ssssssss.magicboot.zintis.led.service.LedLanScannerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "LED 设备控制", description = "提供 LED 设备控制、系统查询、网络扫描接口")
public class LedDeviceController {

    private final LedControlService ledControlService;
    private final LedLanScannerService ledLanScannerService;

    @Operation(summary = "开启设备", description = "根据设备 IP 与端口下发开灯指令")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/control/on")
    public LedControlResponse turnOn(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.turnOn(request);
    }

    @Operation(summary = "关闭设备", description = "根据设备 IP 与端口下发关灯指令")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/control/off")
    public LedControlResponse turnOff(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.turnOff(request);
    }

    @Operation(summary = "脉冲控制", description = "根据设备 IP 与端口下发脉冲控制指令")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/control/pulse")
    public LedControlResponse pulse(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.pulse(request);
    }

    @Operation(summary = "查询设备状态", description = "查询 LED 设备当前控制状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedQueryResponse.class)))
    })
    @PostMapping("/control/query")
    public LedQueryResponse query(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.query(request);
    }

    @Operation(summary = "查询系统信息", description = "查询设备系统信息，如版本、运行状态等")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedSystemInfoResponse.class)))
    })
    @PostMapping("/system/info")
    public LedSystemInfoResponse querySystemInfo(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySystemInfo(request);
    }

    @Operation(summary = "查询信号强度", description = "查询设备无线信号强度")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedSignalStrengthResponse.class)))
    })
    @PostMapping("/system/signal-strength")
    public LedSignalStrengthResponse querySignalStrength(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySignalStrength(request);
    }

    @Operation(summary = "查询网络配置", description = "查询设备网络配置参数")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedSystemNetworkResponse.class)))
    })
    @PostMapping("/system/network")
    public LedSystemNetworkResponse querySystemNetwork(@Valid @RequestBody LedControlRequest request) {
        return ledControlService.querySystemNetwork(request);
    }

    @Operation(summary = "开启 TCP 服务端", description = "配置并开启设备 TCP 服务端")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/tcp/server/open")
    public LedControlResponse openTcpServer(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.openTcpServer(request);
    }

    @Operation(summary = "关闭 TCP 服务端", description = "关闭设备 TCP 服务端")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/tcp/server/close")
    public LedControlResponse closeTcpServer(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.closeTcpServer(request);
    }

    @Operation(summary = "开启 TCP 客户端", description = "配置并开启设备 TCP 客户端")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/tcp/client/open")
    public LedControlResponse openTcpClient(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.openTcpClient(request);
    }

    @Operation(summary = "关闭 TCP 客户端", description = "关闭设备 TCP 客户端")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/tcp/client/close")
    public LedControlResponse closeTcpClient(@Valid @RequestBody LedTcpConfigRequest request) {
        return ledControlService.closeTcpClient(request);
    }

    @Operation(summary = "OTA 升级", description = "根据设备 IP 与端口下发 OTA 升级指令，版本号按 ASCII 字节发送")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedControlResponse.class)))
    })
    @PostMapping("/ota/update")
    public LedControlResponse otaUpdate(@Valid @RequestBody LedOtaUpdateRequest request) {
        return ledControlService.otaUpdate(request);
    }

    @Operation(summary = "局域网扫描设备", description = "扫描指定网段内可发现的 LED 设备")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "扫描完成", content = @Content(schema = @Schema(implementation = LedLanScanResponse.class)))
    })
    @PostMapping("/lan/scan")
    public LedLanScanResponse scanLan(@Valid @RequestBody LedLanScanRequest request) {
        return ledLanScannerService.scan(request);
    }

    @Operation(summary = "健康检查", description = "检查 LED 控制模块服务可用性")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "服务可用")
    })
    @GetMapping("/health")
    public Map<String, Object> health() {
        return ledControlService.health();
    }
}
