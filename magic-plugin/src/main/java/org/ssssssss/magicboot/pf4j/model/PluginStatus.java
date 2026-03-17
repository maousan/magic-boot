package org.ssssssss.magicboot.pf4j.model;

/**
 * 插件状态枚举
 */
public enum PluginStatus {
    /**
     * 已创建
     */
    CREATED,
    /**
     * 已启动
     */
    STARTED,
    /**
     * 已停止
     */
    STOPPED,
    /**
     * 已禁用
     */
    DISABLED,
    /**
     * 错误
     */
    ERROR;

    public String toZh() {
        switch (this) {
            case CREATED:
                return "已创建";
            case STARTED:
                return "运行中";
            case STOPPED:
                return "已停止";
            case DISABLED:
                return "已禁用";
            case ERROR:
                return "错误";
            default:
                return "未知";
        }
    }
}
