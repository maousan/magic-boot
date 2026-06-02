package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
@Schema(description = "OTA 升级请求")
public class LedOtaUpdateRequest {

    @NotBlank(message = "deviceIp must not be blank")
    @Schema(description = "设备 IP 地址", example = "192.168.2.102")
    private String deviceIp;

    @Min(value = 1, message = "devicePort must be >= 1")
    @Max(value = 65535, message = "devicePort must be <= 65535")
    @Schema(description = "设备端口", example = "6000", defaultValue = "6000")
    private Integer devicePort = LedCommandConstants.DEFAULT_DEVICE_PORT;

    @Min(value = 0, message = "hostAddress must be >= 0")
    @Max(value = 255, message = "hostAddress must be <= 255")
    @Schema(description = "设备主机地址（0-255）", example = "234")
    private Integer hostAddress;

    @NotBlank(message = "version must not be blank")
    @Size(max = 128, message = "version length must be <= 128")
    @Pattern(regexp = "^[\\x20-\\x7E]+$", message = "version must be printable ASCII")
    @Schema(description = "升级版本文件名，将按 ASCII 字节下发", example = "1.0.7.bin")
    private String version;

    @Min(value = 100, message = "timeoutMs must be >= 100")
    @Max(value = 60000, message = "timeoutMs must be <= 60000")
    @Schema(description = "请求超时时间（毫秒）", example = "1000", defaultValue = "1000")
    private Integer timeoutMs = LedCommandConstants.DEFAULT_TIMEOUT_MS;
}
