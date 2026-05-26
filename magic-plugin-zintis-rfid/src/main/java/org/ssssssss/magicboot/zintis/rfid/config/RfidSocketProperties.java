package org.ssssssss.magicboot.zintis.rfid.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rfid.socket")
public class RfidSocketProperties {
    private int port = 9090;
    private int maxConnections = 20;
    private int maxFrameSize = 65536;
    private int idleTimeoutSeconds = 60;
    private int ackTimeoutMs = 5000;
    private int ackMaxRetries = 3;
    private int batchSize = 100;
    private int batchIntervalMs = 5000;
    private int dedupCacheSize = 100;
}
