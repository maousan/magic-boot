package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LedNettyServerStartRequest {

    @Min(value = 1, message = "port must be >= 1")
    @Max(value = 65535, message = "port must be <= 65535")
    private Integer port;
}
