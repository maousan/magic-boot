package org.ssssssss.magicboot.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Redis 自动配置排除类
 * 当 extend.redis.enabled=false 时，排除 Spring Boot 的 Redis 自动配置
 * 这样可以避免在没有 Redis 的情况下启动失败
 */
@Configuration
@ConditionalOnProperty(name = "extend.redis.enabled", havingValue = "false", matchIfMissing = true)
@EnableAutoConfiguration(exclude = {
    RedisAutoConfiguration.class,
    RedisRepositoriesAutoConfiguration.class
})
public class RedisAutoConfigurationExcluder {
    // 这个配置类仅在 extend.redis.enabled=false 时生效
    // 它会排除 Spring Boot 的 Redis 自动配置
}
