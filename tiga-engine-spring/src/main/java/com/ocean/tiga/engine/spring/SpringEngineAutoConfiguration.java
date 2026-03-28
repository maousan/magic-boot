package com.ocean.tiga.engine.spring;

import com.ocean.tiga.engine.api.DebugListener;
import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tiga.engine.core.EngineConfig;
import com.ocean.tiga.engine.core.EngineFactory;
import com.ocean.tiga.engine.monitor.PrometheusMetricsCollector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot自动配置
 *
 * @author Tiga Platform Team
 */
@AutoConfiguration
@ConditionalOnClass(EngineManager.class)
@ConditionalOnProperty(name = "tiga.engine.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(EngineProperties.class)
public class SpringEngineAutoConfiguration {

    /**
     * 配置引擎管理器Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public EngineManager engineManager(EngineProperties properties) {
        EngineConfig config = EngineConfig.builder()
                .corePoolSize(properties.getCorePoolSize())
                .maxPoolSize(properties.getMaxPoolSize())
                .queueCapacity(properties.getQueueCapacity())
                .maxCacheSize(properties.getMaxCacheSize())
                .metricsCollector(new PrometheusMetricsCollector())
                .debugListener(new SpringDebugListener())
                .build();

        return EngineFactory.createEngineManager(config);
    }

    /**
     * 配置调试监听器Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public DebugListener debugListener() {
        return new SpringDebugListener();
    }
}
