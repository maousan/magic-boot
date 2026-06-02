package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Netty 心跳开关请求")
public class LedNettyHeartbeatRequest {

    @NotNull(message = "enabled must not be null")
    @Schema(description = "是否开启服务端心跳")
    private Boolean enabled;
}
