package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
public class LedLanScanRequest {

    /**
     * Optional subnet prefix, e.g. "192.168.2".
     * If empty, service will try to detect local subnet.
     */
    private String subnetPrefix;

    @Min(value = 1, message = "devicePort must be >= 1")
    @Max(value = 65535, message = "devicePort must be <= 65535")
    private Integer devicePort = LedCommandConstants.DEFAULT_DEVICE_PORT;

    @Min(value = 1, message = "startHost must be >= 1")
    @Max(value = 254, message = "startHost must be <= 254")
    private Integer startHost = 1;

    @Min(value = 1, message = "endHost must be >= 1")
    @Max(value = 254, message = "endHost must be <= 254")
    private Integer endHost = 254;

    @Min(value = 1, message = "threadPoolSize must be >= 1")
    @Max(value = 128, message = "threadPoolSize must be <= 128")
    private Integer threadPoolSize = 32;

    @Min(value = 50, message = "timeoutMs must be >= 50")
    @Max(value = 10000, message = "timeoutMs must be <= 10000")
    private Integer timeoutMs = 300;

    @Min(value = 1, message = "queryDataCommand must be >= 1")
    @Max(value = 255, message = "queryDataCommand must be <= 255")
    private Integer queryDataCommand = 207;
}
