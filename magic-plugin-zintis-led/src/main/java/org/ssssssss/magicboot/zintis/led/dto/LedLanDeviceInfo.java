package org.ssssssss.magicboot.zintis.led.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedLanDeviceInfo {

    private String ipAddress;
    private Integer hostAddress;
    private String deviceName;
    private String payloadHex;
}
