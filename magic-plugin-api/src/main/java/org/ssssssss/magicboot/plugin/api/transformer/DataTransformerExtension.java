package org.ssssssss.magicboot.plugin.api.transformer;

import org.pf4j.ExtensionPoint;
import java.util.Map;

/**
 * 数据转换扩展点
 * 允许插件注册自定义的数据转换逻辑
 */
public interface DataTransformerExtension extends ExtensionPoint {

    /**
     * 获取转换器名称
     */
    String getName();

    /**
     * 获取转换器类型标识
     */
    String getType();

    /**
     * 执行数据转换
     * @param data 原始数据
     * @param context 转换上下文
     * @return 转换后的数据
     */
    Object transform(Object data, Map<String, Object> context);

    /**
     * 是否支持该类型的数据转换
     * @param dataType 数据类型
     * @return 是否支持
     */
    default boolean supports(String dataType) {
        return getType().equals(dataType);
    }
}
