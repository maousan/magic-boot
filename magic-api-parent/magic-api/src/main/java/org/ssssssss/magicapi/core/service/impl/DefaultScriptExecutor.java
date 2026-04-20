package org.ssssssss.magicapi.core.service.impl;

import org.ssssssss.magicapi.core.service.ScriptExecutor;
import org.ssssssss.magicapi.utils.ScriptManager;
import org.ssssssss.script.MagicScriptContext;

/**
 * 默认脚本执行器，保持原有 ScriptManager 行为。
 */
public class DefaultScriptExecutor implements ScriptExecutor {

    @Override
    public Object executeScript(String scriptId, String script, MagicScriptContext context) {
        return ScriptManager.executeScript(script, context);
    }
}

