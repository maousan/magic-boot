package org.ssssssss.magicboot.zintis.led.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;

@Data
@Schema(description = "局域网扫描请求")
public class LedLanScanRequest {

    /**
     * Optional subnet prefix, e.g. "192.168.2".
     * If empty, service will try to detect local subnet.
     */
    @Schema(description = "可选网段前缀，如 192.168.2；为空时自动探测本机网段", example = "192.168.2")
    private String subnetPrefix;

    @Min(value = 1, message = "devicePort must be >= 1")
    @Max(value = 65535, message = "devicePort must be <= 65535")
    @Schema(description = "设备端口", example = "6000", defaultValue = "6000")
    private Integer devicePort = LedCommandConstants.DEFAULT_DEVICE_PORT;

    @Min(value = 1, message = "startHost must be >= 1")
    @Max(value = 254, message = "startHost must be <= 254")
    @Schema(description = "扫描起始主机号", example = "1", defaultValue = "1")
    private Integer startHost = 1;

    @Min(value = 1, message = "endHost must be >= 1")
    @Max(value = 254, message = "endHost must be <= 254")
    @Schema(description = "扫描结束主机号", example = "254", defaultValue = "254")
    private Integer endHost = 254;

    @Min(value = 1, message = "threadPoolSize must be >= 1")
    @Max(value = 128, message = "threadPoolSize must be <= 128")
    @Schema(description = "并发线程数", example = "32", defaultValue = "32")
    private Integer threadPoolSize = 32;

    @Min(value = 50, message = "timeoutMs must be >= 50")
    @Max(value = 10000, message = "timeoutMs must be <= 10000")
    @Schema(description = "扫描单地址超时（毫秒）", example = "300", defaultValue = "300")
    private Integer timeoutMs = 300;

    @Min(value = 1, message = "queryDataCommand must be >= 1")
    @Max(value = 255, message = "queryDataCommand must be <= 255")
    @Schema(description = "扫描时发送的数据命令字", example = "207", defaultValue = "207")
    private Integer queryDataCommand = 207;
}
