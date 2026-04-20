package org.ssssssss.magicboot.service;

import com.ocean.tiga.engine.api.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.core.exception.MagicAPIException;
import org.ssssssss.magicapi.core.service.ScriptExecutor;
import org.ssssssss.magicboot.config.TigaMagicProperties;
import org.ssssssss.script.MagicScriptContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 基于 Tiga 引擎的 ScriptExecutor 实现。
 */
public class TigaScriptExecutor implements ScriptExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TigaScriptExecutor.class);

    private final EngineManager engineManager;
    private final TigaMagicProperties tigaMagicProperties;

    public TigaScriptExecutor(EngineManager engineManager, TigaMagicProperties tigaMagicProperties) {
        this.engineManager = engineManager;
        this.tigaMagicProperties = tigaMagicProperties;
    }

    @Override
    public Object executeScript(String scriptId, String script, MagicScriptContext context) {
        Map<String, Object> params = new HashMap<>();
        if (context != null) {
            params.putAll(context.getRootVariables());
            params.putIfAbsent("__scriptName__", context.getScriptName());
        }
        String finalScriptId = Objects.toString(scriptId, "unknown");
        try {
            logger.info("[TIGA-MAGIC] SPI执行 scriptId={}, timeoutMs={}", finalScriptId, tigaMagicProperties.getTimeoutMs());
            return engineManager.execute("magic", finalScriptId, script, params, tigaMagicProperties.getTimeoutMs());
        } catch (Exception e) {
            throw new MagicAPIException(e.getMessage(), e);
        }
    }
}

