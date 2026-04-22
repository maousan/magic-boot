package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Netty 服务启动请求")
public class LedNettyServerStartRequest {

    @Min(value = 1, message = "port must be >= 1")
    @Max(value = 65535, message = "port must be <= 65535")
    @Schema(description = "监听端口，未传时使用服务默认端口", example = "9834")
    private Integer port;
}
