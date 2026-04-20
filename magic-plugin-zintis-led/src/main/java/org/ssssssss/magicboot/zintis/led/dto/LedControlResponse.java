package org.ssssssss.magicboot.zintis.led.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
