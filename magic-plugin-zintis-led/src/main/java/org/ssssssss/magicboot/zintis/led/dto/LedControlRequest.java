package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
public class LedControlRequest {

    @NotBlank(message = "deviceIp must not be blank")
    private String deviceIp;

    @Min(value = 1, message = "devicePort must be >= 1")
    @Max(value = 65535, message = "devicePort must be <= 65535")
    private Integer devicePort = LedCommandConstants.DEFAULT_DEVICE_PORT;

    @Min(value = 0, message = "hostAddress must be >= 0")
    @Max(value = 255, message = "hostAddress must be <= 255")
    private Integer hostAddress;

    @Min(value = 1, message = "dataCommand must be >= 1")
    @Max(value = 255, message = "dataCommand must be <= 255")
    private Integer dataCommand;

    @Min(value = 100, message = "timeoutMs must be >= 100")
    @Max(value = 60000, message = "timeoutMs must be <= 60000")
    private Integer timeoutMs = LedCommandConstants.DEFAULT_TIMEOUT_MS;
}