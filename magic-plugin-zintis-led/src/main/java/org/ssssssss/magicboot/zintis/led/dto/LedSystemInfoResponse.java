package org.ssssssss.magicboot.zintis.led.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedSystemInfoResponse {

    private boolean success;
    private String message;
    private String errorCode;

    private Integer hostAddress;
    private String requestDataCommand;
    private String responseControlCommand;
    private String responseDataCommand;

    private String payloadHex;
    private String payloadAscii;
    private String rawRequestHex;
    private String rawResponseHex;
}
