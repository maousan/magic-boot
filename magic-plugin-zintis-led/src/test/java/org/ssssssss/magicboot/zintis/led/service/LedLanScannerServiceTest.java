package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedLanScanResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedLanScannerServiceTest {

    @Mock
    private LedControlService ledControlService;

    @InjectMocks
    private LedLanScannerService ledLanScannerService;

    @Test
    void scan_shouldReturnOnlyEsp32PrefixDevices() {
        when(ledControlService.querySystemInfo(any())).thenAnswer(invocation -> {
            LedControlRequest request = invocation.getArgument(0);
            int host = request.getHostAddress();
            if (host == 198) {
                return LedSystemInfoResponse.builder()
                        .success(true)
                        .payloadAscii("ESP32-LED-A")
                        .payloadHex("45535033322D4C45442D41")
                        .build();
            }
            if (host == 199) {
                return LedSystemInfoResponse.builder()
                        .success(true)
                        .payloadAscii("OTHER-DEVICE")
                        .payloadHex("4F544845522D444556494345")
                        .build();
            }
            return LedSystemInfoResponse.builder().success(false).build();
        });

        LedLanScanRequest request = new LedLanScanRequest();
        request.setSubnetPrefix("192.168.2");
        request.setStartHost(198);
        request.setEndHost(200);
        request.setThreadPoolSize(4);
        request.setTimeoutMs(200);

        LedLanScanResponse response = ledLanScannerService.scan(request);

        assertTrue(response.isSuccess());
        assertEquals(3, response.getScannedCount());
        assertEquals(1, response.getMatchedCount());
        assertEquals("192.168.2.198", response.getDevices().get(0).getIpAddress());
        assertEquals("ESP32-LED-A", response.getDevices().get(0).getDeviceName());
    }

    @Test
    void scan_shouldRejectInvalidHostRange() {
        LedLanScanRequest request = new LedLanScanRequest();
        request.setSubnetPrefix("192.168.2");
        request.setStartHost(210);
        request.setEndHost(200);

        LedLanScanResponse response = ledLanScannerService.scan(request);

        assertFalse(response.isSuccess());
        assertEquals("startHost must be <= endHost", response.getMessage());
    }

    @Test
    void scan_shouldUseConfiguredIpAndHostAddress() {
        when(ledControlService.querySystemInfo(any()))
                .thenReturn(LedSystemInfoResponse.builder().success(false).build());

        LedLanScanRequest request = new LedLanScanRequest();
        request.setSubnetPrefix("192.168.2");
        request.setStartHost(198);
        request.setEndHost(198);

        ledLanScannerService.scan(request);

        ArgumentCaptor<LedControlRequest> captor = ArgumentCaptor.forClass(LedControlRequest.class);
        verify(ledControlService, times(1)).querySystemInfo(captor.capture());
        assertEquals("192.168.2.198", captor.getValue().getDeviceIp());
        assertEquals(198, captor.getValue().getHostAddress());
    }
}
