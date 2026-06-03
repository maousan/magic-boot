package org.ssssssss.magicboot.pf4j.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 插件信息实体
 */
@Data
public class PluginInfo {

    private String id;
    private String pluginId;
    private String pluginName;
    private String version;
    private String description;
    private String author;
    private String pluginClass;
    private String status;
    private String jarPath;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String dependencies;
    private String provider;
    private String packageType;
    private String packageChecksum;
    private String manifestVersion;
    private String manifestJson;
    private String requiresMagicBoot;
    private String permissions;
    private String installSource;
    private LocalDateTime installTime;
}
