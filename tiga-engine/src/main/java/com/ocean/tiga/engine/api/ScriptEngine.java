package com.ocean.tiga.engine.api;

import java.util.Map;
import java.util.Set;

/**
 * 脚本引擎接口
 * 定义统一的脚本执行、调试和模块管理能力
 *
 * @author Tiga Platform Team
 */
public interface ScriptEngine {

    /**
     * 执行脚本
     *
     * @param scriptId   脚本唯一标识（用于缓存管理）
     * @param scriptText 脚本源码
     * @param params     执行参数
     * @param timeoutMs  超时时间（毫秒）
     * @return 执行结果
     * @throws Exception 执行异常
     */
    Object execute(String scriptId, String scriptText, Map<String, Object> params, long timeoutMs) throws Exception;

    /**
     * 调试执行脚本
     *
     * @param scriptId    脚本唯一标识
     * @param scriptText  脚本源码
     * @param params      执行参数
     * @param timeout     超时时间（毫秒），可为null
     * @param breakpoints 断点行号集合
     * @param listener    调试事件监听器
     */
    void executeDebug(String scriptId, String scriptText, Map<String, Object> params,
                     Long timeout, Set<Integer> breakpoints, DebugListener listener);

    /**
     * 注册模块
     *
     * @param name   模块名称
     * @param module 模块实例
     */
    void registerModule(String name, Object module);

    /**
     * 获取所有模块名称
     *
     * @return 模块名称集合
     */
    Set<String> getModuleNames();

    /**
     * 获取模块的键集合
     *
     * @param modelNames 模块名称（逗号分隔）
     * @return 模块名称 -> 键集合的映射
     */
    Map<String, Set<String>> getModuleKeys(String modelNames);

    /**
     * 移除脚本缓存
     *
     * @param scriptId 脚本ID
     */
    void removeCacheById(String scriptId);

    /**
     * 获取引擎指标数据
     *
     * @return 指标Map
     */
    Map<String, Object> getMetrics();

    /**
     * 获取引擎类型
     *
     * @return 引擎类型标识
     */
    String getEngineType();
}
