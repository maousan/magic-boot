package org.ssssssss.magicboot.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置类
 * 根据 extend.redis.enabled 决定使用 Redis 存储还是内存存储
 */
@Configuration
public class SaTokenConfig {

    /**
     * 当 Redis 禁用时，使用内存存储
     */
    @Bean
    @ConditionalOnProperty(name = "extend.redis.enabled", havingValue = "false", matchIfMissing = true)
    public SaTokenDao saTokenDaoMemory() {
        // 使用 Sa-Token 默认的内存存储实现
        return new SaTokenDaoDefaultImpl();
    }
    
    // 注意：当 extend.redis.enabled=true 时，Sa-Token 的 Redis 自动配置会生效
    // 会自动创建 SaTokenDaoForRedisTemplate Bean
}
