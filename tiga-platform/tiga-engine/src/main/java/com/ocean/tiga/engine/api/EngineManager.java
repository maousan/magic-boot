package com.ocean.tiga.engine.api;

import java.util.Map;
import java.util.Set;

/**
 * 引擎管理器接口
 * 统一调度多种脚本引擎，提供全局模块管理
 *
 * @author Tiga Platform Team
 */
public interface EngineManager {

    /**
     * 执行脚本（统一入口）
     *
     * @param engineType 引擎类型: "magic" 或 "groovy"
     * @param scriptId   唯一标识（接口ID）
     * @param scriptText 源码
     * @param params     入参
     * @param timeout    超时时间（毫秒）
     * @return 执行结果
     * @throws Exception 执行异常
     */
    Object execute(String engineType, String scriptId, String scriptText,
                  Map<String, Object> params, long timeout) throws Exception;

    /**
     * 注册全局模块（一次注册，所有引擎生效）
     *
     * @param name   模块名称（如 db, redis, http）
     * @param module 模块实例
     */
    void registerGlobalModule(String name, Object module);

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
     * 刷新/清理脚本缓存
     * 当接口源码发生变动时调用，确保下次执行时重新编译
     *
     * @param scriptId 脚本ID
     */
    void refreshCache(String scriptId);
}
