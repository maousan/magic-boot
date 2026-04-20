package org.ssssssss.magicboot.configuration;

import com.ocean.tiga.engine.api.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.service.MagicAPIService;
import org.ssssssss.magicapi.core.service.ScriptExecutor;

/**
 * Tiga 接管链路诊断输出。
 * 用于在启动后确认实际生效的 MagicAPIService 实现。
 */
@Component
public class TigaMagicDiagnostics implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TigaMagicDiagnostics.class);

    private final MagicAPIService magicAPIService;
    private final ScriptExecutor scriptExecutor;
    private final ObjectProvider<EngineManager> engineManagerProvider;
    private final Environment environment;

    public TigaMagicDiagnostics(MagicAPIService magicAPIService,
                                ScriptExecutor scriptExecutor,
                                ObjectProvider<EngineManager> engineManagerProvider,
                                Environment environment) {
        this.magicAPIService = magicAPIService;
        this.scriptExecutor = scriptExecutor;
        this.engineManagerProvider = engineManagerProvider;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean tigaEnabled = Boolean.parseBoolean(environment.getProperty("tiga.engine.enabled", "true"));
        String timeoutMs = environment.getProperty("tiga.magic.timeout-ms", "30000");
        boolean engineManagerPresent = engineManagerProvider.getIfAvailable() != null;
        String apiServiceClass = magicAPIService.getClass().getName();
        String scriptExecutorClass = scriptExecutor.getClass().getName();
        String magicConfigurationScriptExecutor = MagicConfiguration.getScriptExecutor() == null
                ? "null"
                : MagicConfiguration.getScriptExecutor().getClass().getName();

        String message = String.format(
                "[TIGA-MAGIC-DIAG] tiga.engine.enabled=%s, engineManagerPresent=%s, tiga.magic.timeout-ms=%s, activeMagicAPIService=%s, activeScriptExecutor=%s, magicConfigurationScriptExecutor=%s",
                tigaEnabled, engineManagerPresent, timeoutMs, apiServiceClass, scriptExecutorClass, magicConfigurationScriptExecutor
        );

        logger.warn(message);
        System.out.println(message);
    }
}
