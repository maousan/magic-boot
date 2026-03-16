package org.ssssssss.magicboot.plugin.api.scheduler;

import org.pf4j.ExtensionPoint;

/**
 * 定时任务扩展点
 * 允许插件注册 Java 类型的定时任务
 */
public interface JobExtension extends ExtensionPoint {

    /**
     * 获取任务名称
     */
    String getJobName();

    /**
     * 获取任务分组
     */
    default String getJobGroup() {
        return "plugin";
    }

    /**
     * 获取任务描述
     */
    default String getDescription() {
        return "";
    }

    /**
     * 执行任务
     * @param context 任务执行上下文
     */
    void execute(JobContext context);

    /**
     * 任务执行前的回调
     */
    default void beforeExecute(JobContext context) {
    }

    /**
     * 任务执行后的回调
     */
    default void afterExecute(JobContext context, Exception ex) {
    }
}
