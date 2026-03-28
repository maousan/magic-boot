package com.ocean.tiga.engine.core;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tiga.engine.api.ScriptEngine;
import com.ocean.tiga.engine.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 默认引擎管理器实现
 * 统一调度多种脚本引擎
 *
 * @author Tiga Platform Team
 */
public class DefaultEngineManager implements EngineManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultEngineManager.class);

    private final ScriptEngine magicScriptEngine;
    private final ScriptEngine groovyScriptEngine;

    /**
     * 构造函数
     *
     * @param magicScriptEngine  MagicScript引擎
     * @param groovyScriptEngine Groovy引擎
     */
    public DefaultEngineManager(ScriptEngine magicScriptEngine, ScriptEngine groovyScriptEngine) {
        this.magicScriptEngine = magicScriptEngine;
        this.groovyScriptEngine = groovyScriptEngine;
    }

    @Override
    public Object execute(String engineType, String scriptId, String scriptText,
                         Map<String, Object> params, long timeout) throws Exception {
        if (StringUtils.isEmpty(engineType)) {
            throw new IllegalArgumentException("引擎类型不能为空");
        }

        switch (engineType.toLowerCase()) {
            case "magic":
                return magicScriptEngine.execute(scriptId, scriptText, params, timeout);
            case "groovy":
                return groovyScriptEngine.execute(scriptId, scriptText, params, timeout);
            default:
                throw new IllegalArgumentException("不支持的引擎类型: " + engineType);
        }
    }

    @Override
    public void registerGlobalModule(String name, Object module) {
        magicScriptEngine.registerModule(name, module);
        groovyScriptEngine.registerModule(name, module);
        log.info("Tiga引擎注册全局模块: {}", name);
    }

    @Override
    public Set<String> getModuleNames() {
        return magicScriptEngine.getModuleNames();
    }

    @Override
    public Map<String, Set<String>> getModuleKeys(String modelNames) {
        log.info("提取插件 {} 代理名称", modelNames);
        return magicScriptEngine.getModuleKeys(modelNames);
    }

    @Override
    public void refreshCache(String scriptId) {
        magicScriptEngine.removeCacheById(scriptId);
        groovyScriptEngine.removeCacheById(scriptId);
        log.info("Tiga引擎已清空脚本缓存资源 [ID: {}]", scriptId);
    }
}
