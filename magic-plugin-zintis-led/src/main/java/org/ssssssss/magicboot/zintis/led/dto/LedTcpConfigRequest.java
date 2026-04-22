package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
@Schema(description = "TCP 配置请求（用于开启/关闭 TCP Server 或 TCP Client）")
public class LedTcpConfigRequest {

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

    @Min(value = 100, message = "timeoutMs must be >= 100")
    @Max(value = 60000, message = "timeoutMs must be <= 60000")
    @Schema(description = "请求超时时间（毫秒）", example = "1000", defaultValue = "1000")
    private Integer timeoutMs = LedCommandConstants.DEFAULT_TIMEOUT_MS;

    /**
     * Required only for open TCP client command.
     */
    @Schema(description = "TCP 客户端模式目标上位机 IP，仅在开启 TCP 客户端时必填", example = "192.168.2.180")
    private String targetIp;

    /**
     * Required only for open TCP client command.
     */
    @Min(value = 1, message = "targetPort must be >= 1")
    @Max(value = 65535, message = "targetPort must be <= 65535")
    @Schema(description = "TCP 客户端模式目标上位机端口，仅在开启 TCP 客户端时必填", example = "9834")
    private Integer targetPort;
}
