package org.ssssssss.magicapi.file.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "magic-api.file")
public class FilePluginProperties {
    private String tableName = "magic_file";
    private String dialect = "mysql";
}
