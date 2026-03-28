package org.ssssssss.magicboot.pf4j.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.ssssssss.magicboot.pf4j.service.PluginManagerService;

import java.util.Map;

/**
 * 启动后将 PF4J 运行态插件增量同步到数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = {"enabled", "init-sync-on-startup"}, havingValue = "true", matchIfMissing = true)
public class PluginStartupSyncRunner implements ApplicationRunner {

    private final PluginManagerService pluginManagerService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Map<String, Object> result = pluginManagerService.initMissingPluginsFromRuntime();
            log.info("启动插件元数据同步完成: {}", result);
        } catch (Exception e) {
            // 启动同步失败不应阻塞主流程
            log.error("启动插件元数据同步失败", e);
        }
    }
}

