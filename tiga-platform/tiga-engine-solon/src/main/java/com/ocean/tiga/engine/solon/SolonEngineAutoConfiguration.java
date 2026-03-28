package com.ocean.tiga.engine.solon;

import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tiga.engine.core.EngineConfig;
import com.ocean.tiga.engine.core.EngineFactory;
import com.ocean.tiga.engine.monitor.PrometheusMetricsCollector;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * Solon框架自动配置
 *
 * @author Tiga Platform Team
 */
@Configuration
public class SolonEngineAutoConfiguration {

    /**
     * 配置引擎管理器Bean
     */
    @Bean
    public EngineManager engineManager() {
        EngineConfig config = EngineConfig.builder()
                .metricsCollector(new PrometheusMetricsCollector())
                .debugListener(new SolonDebugListener())
                .corePoolSize(100)
                .maxPoolSize(200)
                .queueCapacity(1000)
                .maxCacheSize(800)
                .build();

        return EngineFactory.createEngineManager(config);
    }

    /**
     * 配置调试监听器Bean
     */
    @Bean
    public SolonDebugListener debugListener() {
        return new SolonDebugListener();
    }
}
