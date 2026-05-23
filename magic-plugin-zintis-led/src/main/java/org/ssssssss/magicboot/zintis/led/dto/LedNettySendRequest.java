package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Netty 指定客户端发送请求")
public class LedNettySendRequest {

    @NotBlank(message = "remoteAddress must not be blank")
    @Schema(description = "客户端远端地址（IP:Port）", example = "192.168.2.102:51853")
    private String remoteAddress;

    @Schema(description = "负载文本，payloadFormat=ascii/hex 时生效；hex 支持 `66AB` 或 `0x66 0xAB`", example = "0x66 0x35 0xBA 0x3C 0x07")
    private String payload;

    /**
     * Optional numeric array payload, each value should be in range 0..255.
     * If provided and not empty, this field has higher priority than payload/payloadFormat.
     */
    @Schema(description = "数值数组负载（0-255），优先级高于 payload/payloadFormat", example = "[102,53,186,60,7]")
    private List<Integer> payloadArray;

    /**
     * ascii | hex
     */
    @Schema(description = "负载格式，可选 ascii 或 hex", example = "hex", defaultValue = "ascii")
    private String payloadFormat = "ascii";

    @Schema(description = "是否等待客户端回包；默认false，不等待", example = "false", defaultValue = "false")
    private Boolean waitResponse = false;
}
