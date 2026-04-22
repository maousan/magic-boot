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
@Schema(description = "LED 控制命令响应")
public class LedControlResponse {

    private boolean success;
    private String message;
    private String errorCode;

    private Integer hostAddress;
    private String controlCommand;
    private String dataCommand;
    private String responseControlCommand;
    private String responseDataCommand;

    private String rawRequestHex;
    private String rawResponseHex;
}
