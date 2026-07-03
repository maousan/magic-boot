package org.ssssssss.magicboot.zintis.led.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyBroadcastRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyClientListResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyClientReportRegistrationRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyHeartbeatRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettySendResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStartRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedNettyServerStatusResponse;
import org.ssssssss.magicboot.zintis.led.service.LedNettyServerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "LED Netty 服务", description = "提供 Netty TCP 服务的启动/停止、客户端管理与消息下发接口")
public class LedNettyServerController {

    private final LedNettyServerService ledNettyServerService;

    @Operation(summary = "启动 Netty 服务", description = "启动用于设备通信的 Netty 服务，端口可选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedNettyServerStatusResponse.class)))
    })
    @PostMapping("/netty/server/start")
    public LedNettyServerStatusResponse startNettyServer(@RequestBody(required = false) LedNettyServerStartRequest request) {
        Integer port = request == null ? null : request.getPort();
        return ledNettyServerService.start(port);
    }

    @Operation(summary = "停止 Netty 服务", description = "停止当前运行中的 Netty 服务")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "执行成功", content = @Content(schema = @Schema(implementation = LedNettyServerStatusResponse.class)))
    })
    @PostMapping("/netty/server/stop")
    public LedNettyServerStatusResponse stopNettyServer() {
        return ledNettyServerService.stop();
    }

    @Operation(summary = "查询 Netty 服务状态", description = "查看 Netty 服务当前运行状态与端口信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedNettyServerStatusResponse.class)))
    })
    @GetMapping("/netty/server/status")
    public LedNettyServerStatusResponse nettyServerStatus() {
        return ledNettyServerService.status();
    }

    @Operation(summary = "设置 Netty 心跳", description = "动态开启或关闭服务端心跳广播，开启后每 3 秒向已连接客户端发送心跳指令")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "设置成功", content = @Content(schema = @Schema(implementation = LedNettyServerStatusResponse.class)))
    })
    @PostMapping("/netty/server/heartbeat")
    public LedNettyServerStatusResponse updateNettyHeartbeat(@Valid @RequestBody LedNettyHeartbeatRequest request) {
        return ledNettyServerService.updateHeartbeat(Boolean.TRUE.equals(request.getEnabled()));
    }

    @Operation(summary = "设置客户端上报自动入库", description = "动态开启或关闭客户端上报 MAC/IP 后自动保存设备信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "设置成功", content = @Content(schema = @Schema(implementation = LedNettyServerStatusResponse.class)))
    })
    @PostMapping("/netty/server/client-report-registration")
    public LedNettyServerStatusResponse updateClientReportRegistration(
            @Valid @RequestBody LedNettyClientReportRegistrationRequest request) {
        return ledNettyServerService.updateClientReportRegistration(Boolean.TRUE.equals(request.getEnabled()));
    }

    @Operation(summary = "查询活跃客户端列表", description = "返回当前已连接 Netty 客户端远端地址列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = LedNettyClientListResponse.class)))
    })
    @GetMapping("/netty/server/clients")
    public LedNettyClientListResponse listNettyClients() {
        return ledNettyServerService.listClients();
    }

    @Operation(summary = "向指定客户端发送消息", description = "按远端地址向已连接的指定客户端主动下发消息，支持 ASCII/HEX")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "发送结果", content = @Content(schema = @Schema(implementation = LedNettySendResponse.class)))
    })
    @PostMapping("/netty/server/send")
    public LedNettySendResponse sendToClient(@Valid @RequestBody LedNettySendRequest request) {
        return ledNettyServerService.sendToClient(request);
    }

    @Operation(summary = "广播发送消息", description = "向所有当前已连接客户端主动广播消息，支持 ASCII/HEX")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "发送结果", content = @Content(schema = @Schema(implementation = LedNettySendResponse.class)))
    })
    @PostMapping("/netty/server/broadcast")
    public LedNettySendResponse broadcast(@Valid @RequestBody LedNettyBroadcastRequest request) {
        return ledNettyServerService.broadcast(request);
    }
}
