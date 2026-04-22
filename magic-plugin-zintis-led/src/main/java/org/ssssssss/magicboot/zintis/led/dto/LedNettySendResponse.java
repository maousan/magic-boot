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
}
