package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Netty 客户端上报自动入库开关请求")
public class LedNettyClientReportRegistrationRequest {

    @NotNull(message = "enabled must not be null")
    @Schema(description = "是否开启客户端上报 MAC/IP 自动入库")
    private Boolean enabled;
}
