package org.ssssssss.magicboot.zintis.led.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Netty 广播发送请求")
public class LedNettyBroadcastRequest {

    @Schema(description = "广播负载文本，payloadFormat=ascii/hex 时生效", example = "0x66 0x35 0xBA 0x3C 0x07")
    private String payload;

    /**
     * Optional numeric array payload, each value should be in range 0..255.
     * If provided and not empty, this field has higher priority than payload/payloadFormat.
     */
    @Schema(description = "广播数值数组负载（0-255），优先级高于 payload/payloadFormat", example = "[102,53,186,60,7]")
    private List<Integer> payloadArray;

    /**
     * ascii | hex
     */
    @Schema(description = "负载格式，可选 ascii 或 hex", example = "hex", defaultValue = "ascii")
    private String payloadFormat = "ascii";
}
