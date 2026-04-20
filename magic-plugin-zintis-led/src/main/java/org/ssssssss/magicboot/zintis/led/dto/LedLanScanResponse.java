package org.ssssssss.magicboot.zintis.led.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedLanScanResponse {

    private boolean success;
    private String message;
    private String subnetPrefix;
    private int scannedCount;
    private int matchedCount;
    private List<LedLanDeviceInfo> devices;
}
