package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Netty 客户端列表响应")
public class LedNettyClientListResponse {

    private boolean running;
    private int totalClients;
    private List<String> clients;
    private List<ClientInfo> clientDetails;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Netty 客户端信息")
    public static class ClientInfo {

        @Schema(description = "客户端远端地址（IP:Port）", example = "192.168.2.102:51853")
        private String remoteAddress;

        @Schema(description = "客户端MAC地址，未识别时为空", example = "3A:69:7A:08:D0:A5")
        private String macAddress;
    }
}
