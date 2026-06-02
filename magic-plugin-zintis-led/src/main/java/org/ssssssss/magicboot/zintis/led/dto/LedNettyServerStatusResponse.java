package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Netty 服务状态响应")
public class LedNettyServerStatusResponse {

    private boolean running;
    private int port;
    private int activeConnections;
    private boolean heartbeatEnabled;
    private boolean heartbeatRunning;
    private boolean clientReportRegistrationEnabled;
    private Long lastHeartbeatAt;
    private int lastHeartbeatTargets;
    private int lastHeartbeatSuccessCount;
    private int lastHeartbeatFailedCount;
    private String message;
}
