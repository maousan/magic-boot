package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
@Schema(description = "LED 基础控制请求")
public class LedControlRequest {

    @NotBlank(message = "deviceIp must not be blank")
    @Schema(description = "设备 IP 地址", example = "192.168.2.102")
    private String deviceIp;

    @Min(value = 1, message = "devicePort must be >= 1")
    @Max(value = 65535, message = "devicePort must be <= 65535")
    @Schema(description = "设备端口", example = "6000", defaultValue = "6000")
    private Integer devicePort = LedCommandConstants.DEFAULT_DEVICE_PORT;

    @Min(value = 0, message = "hostAddress must be >= 0")
    @Max(value = 255, message = "hostAddress must be <= 255")
    @Schema(description = "设备主机地址（0-255）", example = "102")
    private Integer hostAddress;

    @Min(value = 1, message = "dataCommand must be >= 1")
    @Max(value = 255, message = "dataCommand must be <= 255")
    @Schema(description = "数据命令字（1-255）", example = "51")
    private Integer dataCommand;

    @Min(value = 100, message = "timeoutMs must be >= 100")
    @Max(value = 60000, message = "timeoutMs must be <= 60000")
    @Schema(description = "请求超时时间（毫秒）", example = "1000", defaultValue = "1000")
    private Integer timeoutMs = LedCommandConstants.DEFAULT_TIMEOUT_MS;

}
