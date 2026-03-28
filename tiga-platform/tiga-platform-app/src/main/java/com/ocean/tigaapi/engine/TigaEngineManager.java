package com.ocean.tigaapi.engine;

import java.util.Map;
import java.util.Set;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocean.tigaapi.engine.groovy.GroovyEngine;
import com.ocean.tigaapi.engine.magic.MagicEngine;

/**
 * Tiga 引擎枢纽管理器
 * 统一调度 MagicScript 与 Groovy 引擎，并管理全局模块与监控。
 */
@Component
public class TigaEngineManager {
    private static final Logger log = LoggerFactory.getLogger(TigaEngineManager.class);

    @Inject
    private static MagicEngine magicScriptEngine;
    @Inject
    private GroovyEngine groovyScriptEngine;

    /**
     * 执行脚本（统一入口）
     * @param engineType 引擎类型: "magic" 或 "groovy"
     * @param scriptId   唯一标识（接口ID）
     * @param scriptText 源码
     * @param params     入参
     * @return 执行结果
     */
    public Object execute(String engineType, String scriptId, String scriptText, Map<String, Object> params, long timeout) throws Exception {
        if (!Utils.isEmpty(engineType) && "magic".equalsIgnoreCase(engineType)) {
            return magicScriptEngine.execute(scriptId, scriptText, params, timeout);
        } else if (!Utils.isEmpty(engineType) && "groovy".equalsIgnoreCase(engineType)) {
            return groovyScriptEngine.execute(scriptId, scriptText, params, timeout);
        } else {
            throw new IllegalArgumentException("不支持的引擎类型: " + engineType);
        }
    }

    /**
     * 注册全局模块（一次注册，双引擎生效）
     * 比如注册 db, redis, http 等工具类
     */
    public void registerGlobalModule(String name, Object module) {
        magicScriptEngine.registerModule(name, module);
        groovyScriptEngine.registerModule(name, module);
        log.info("Tiga引擎注册全局模块: {}", name);
    }
    
    
    /**
     * 获取插件的对象名称
     * 比如注册 db._、db.yjy 中的 db
     */
    public static Set<String> getModuleNames() {
    	return magicScriptEngine.getModuleNames();
    }
    
	/**
	 * 获取插件名称及插件实例的名称
	 * @param modelNames
	 * 		插件名称集合，数据格式：db,redis,kafka,mqtt,hbase,es,tcp,...
	 * @return
	 * 		{
	 * 			dh : [_,yjy,...],
	 * 			redis : [_,yjy,...],
	 * 			...
	 *		}
	 */
    public Map<String, Set<String>> getModuleKeys(String modelNames) {
    	log.info("提取插件 {} 代理名称", modelNames);
    	return magicScriptEngine.getModuleKeys(modelNames);
    }
    

    /**
     * 刷新/清理脚本缓存
     * 当接口源码发生变动时调用，确保下次执行时重新编译
     */
    public void refreshCache(String scriptId) {
        // 清理 MagicScript 缓存
        magicScriptEngine.removeCacheById(scriptId);
        groovyScriptEngine.removeCacheById(scriptId);
        
        log.info("Tiga引擎已清空脚本缓存资源 [ID: {}]", scriptId);
    }
}