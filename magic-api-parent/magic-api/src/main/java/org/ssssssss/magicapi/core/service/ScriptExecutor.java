package org.ssssssss.magicapi.core.service;

import org.ssssssss.script.MagicScriptContext;

/**
 * 脚本执行器 SPI。
 * 用于统一替换脚本执行实现（默认 MagicScript，可扩展为其他执行引擎）。
 */
public interface ScriptExecutor {

    /**
     * 执行脚本。
     *
     * @param scriptId 脚本唯一标识（可用于缓存与诊断）
     * @param script   脚本源码
     * @param context  脚本上下文
     * @return 执行结果
     */
    Object executeScript(String scriptId, String script, MagicScriptContext context);
}

