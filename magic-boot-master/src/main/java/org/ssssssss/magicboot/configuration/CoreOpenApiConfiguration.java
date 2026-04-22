package org.ssssssss.magicboot.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(GroupedOpenApi.class)
@ConditionalOnExpression("${springdoc.api-docs.enabled:true}")
public class CoreOpenApiConfiguration {

    @Bean
    public GroupedOpenApi defaultOpenApiGroup() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .pathsToExclude(
                        "/plugin/**",
                        "/v3/api-docs/plugin/**"
                )
                .build();
    }
}
