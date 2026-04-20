package org.ssssssss.magicboot.zintis.led.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedNettyServerStatusResponse {

    private boolean running;
    private int port;
    private int activeConnections;
    private String message;
}
