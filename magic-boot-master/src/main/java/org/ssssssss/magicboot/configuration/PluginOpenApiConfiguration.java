package org.ssssssss.magicboot.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;

@Configuration
@ConditionalOnClass(GroupedOpenApi.class)
@ConditionalOnExpression("${springdoc.api-docs.enabled:true} && ${plugin.enabled:true}")
public class PluginOpenApiConfiguration {

    static final String DEFAULT_PLUGIN_API_PREFIX_TEMPLATE = "/plugin/{pluginId}/api";

    @Bean
    public GroupedOpenApi pf4jPluginApiGroup(PluginProperties pluginProperties) {
        String pluginPathPrefix = resolvePluginApiPrefix(pluginProperties, "*");
        String pathPattern = normalizePath(pluginPathPrefix + "/**");
        return GroupedOpenApi.builder()
                .group("pf4j-plugin-api")
                .pathsToMatch(pathPattern)
                .build();
    }

    static String resolvePluginApiPrefix(PluginProperties pluginProperties, String pluginId) {
        String prefixTemplate = pluginProperties.getApiPrefixTemplate();
        if (prefixTemplate == null || prefixTemplate.isBlank()) {
            prefixTemplate = DEFAULT_PLUGIN_API_PREFIX_TEMPLATE;
        }
        return normalizePath(prefixTemplate.replace("{pluginId}", pluginId));
    }

    static String normalizePath(String path) {
        String value = path == null ? "" : path.trim();
        if (value.isBlank()) {
            return "/";
        }
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        while (value.contains("//")) {
            value = value.replace("//", "/");
        }
        return value;
    }
}
