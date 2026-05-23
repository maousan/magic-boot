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
@Schema(description = "Netty 发送结果响应")
public class LedNettySendResponse {

    private boolean success;
    private String message;
    private int totalTargets;
    private int successCount;
    private int failedCount;
    private List<String> failedTargets;
    private boolean waitResponse;
    private int responseCount;
    private List<ClientResponse> responses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Netty 客户端回包信息")
    public static class ClientResponse {

        @Schema(description = "客户端远端地址（IP:Port）", example = "192.168.2.102:51853")
        private String remoteAddress;

        @Schema(description = "是否收到回包")
        private boolean received;

        @Schema(description = "等待回包是否超时")
        private boolean timeout;

        @Schema(description = "原始回包HEX")
        private String rawResponseHex;

        @Schema(description = "回包负载ASCII视图")
        private String payloadAscii;

        @Schema(description = "回包解析出的MAC地址")
        private String macAddress;

        @Schema(description = "回包解析出的IP地址")
        private String ipAddress;

        @Schema(description = "回包CRC")
        private String crc;
    }
}
