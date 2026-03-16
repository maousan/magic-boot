package org.ssssssss.magicboot.pf4j.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ssssssss.magicboot.pf4j.model.PluginStatus;

import java.time.LocalDateTime;

/**
 * 插件信息实体
 */
@Data
@TableName("magic_plugin")
public class PluginInfo {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 插件 ID
     */
    private String pluginId;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件版本
     */
    private String version;

    /**
     * 插件描述
     */
    private String description;

    /**
     * 插件作者
     */
    private String author;

    /**
     * 插件类
     */
    private String pluginClass;

    /**
     * 插件状态
     */
    private String status;

    /**
     * JAR 文件路径
     */
    private String jarPath;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 插件依赖
     */
    private String dependencies;

    /**
     * 插件提供者
     */
    private String provider;
}
