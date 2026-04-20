package org.ssssssss.magicboot.configuration;

import com.ocean.tiga.engine.api.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.service.ScriptExecutor;
import org.ssssssss.magicapi.core.service.impl.DefaultScriptExecutor;
import org.ssssssss.magicboot.config.TigaMagicProperties;
import org.ssssssss.magicboot.service.TigaScriptExecutor;
import org.ssssssss.script.MagicResourceLoader;
import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.script.functions.DynamicModuleImport;

import java.util.ArrayList;
import java.util.Set;

/**
 * Tiga 接管 magic-api 执行链路配置。
 */
@Configuration
@EnableConfigurationProperties(TigaMagicProperties.class)
public class TigaMagicApiConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TigaMagicApiConfiguration.class);

    @Bean
    @Primary
    public ScriptExecutor scriptExecutor(ObjectProvider<EngineManager> engineManagerProvider,
                                         TigaMagicProperties tigaMagicProperties,
                                         Environment environment) {
        boolean tigaEnabled = Boolean.parseBoolean(environment.getProperty("tiga.engine.enabled", "true"));
        EngineManager engineManager = engineManagerProvider.getIfAvailable();
        if (tigaEnabled && engineManager != null) {
            logger.warn("[TIGA-MAGIC] ScriptExecutor SPI 切换为 TigaScriptExecutor，执行将走 EngineManager.execute(\"magic\", ...)，timeoutMs={}",
                    tigaMagicProperties.getTimeoutMs());
            return new TigaScriptExecutor(engineManager, tigaMagicProperties);
        }
        logger.warn("[TIGA-MAGIC] ScriptExecutor SPI 使用 DefaultScriptExecutor，原因: tiga.engine.enabled={}, engineManagerPresent={}",
                tigaEnabled, engineManager != null);
        return new DefaultScriptExecutor();
    }

    @Bean
    public SmartInitializingSingleton tigaMagicScriptExecutorInstaller(ScriptExecutor scriptExecutor) {
        return () -> {
            MagicConfiguration.setScriptExecutor(scriptExecutor);
            logger.warn("[TIGA-MAGIC] 已强制绑定 MagicConfiguration.scriptExecutor -> {}", scriptExecutor.getClass().getName());
        };
    }

    @Bean
    @ConditionalOnBean(EngineManager.class)
    @ConditionalOnProperty(name = "tiga.engine.enabled", havingValue = "true", matchIfMissing = true)
    public SmartInitializingSingleton tigaMagicModuleBridgeInitializer(EngineManager engineManager) {
        return () -> {
            Set<String> moduleNames = MagicResourceLoader.getModuleNames();
            int success = 0;
            for (String moduleName : new ArrayList<>(moduleNames)) {
                try {
                    Object module = MagicResourceLoader.loadModule(moduleName);
                    if (module instanceof DynamicModuleImport dynamicModuleImport) {
                        MagicScriptContext context = new MagicScriptContext();
                        context.setScriptName("tiga-bridge/" + moduleName);
                        module = dynamicModuleImport.getDynamicModule(context);
                    }
                    engineManager.registerGlobalModule(moduleName, module);
                    success++;
                } catch (Exception e) {
                    logger.warn("桥接模块到 Tiga 失败: {}", moduleName, e);
                }
            }
            logger.info("Tiga 模块桥接完成，成功注册 {}/{} 个模块", success, moduleNames.size());
        };
    }
}
