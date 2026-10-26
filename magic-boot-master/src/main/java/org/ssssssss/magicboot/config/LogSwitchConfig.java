package org.ssssssss.magicboot.config;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.redis.model.MagicDynamicRedisClient;

import java.util.concurrent.TimeUnit;

/**
 * 日志开关配置：读 sys_configure（与 .ms 侧 getSysConfig 函数共用 sys_config: 前缀的 redis 缓存，
 * 系统配置页保存后即时生效）；redis 未命中回源读库并回写 60s。
 * 取值语义：非 "false" 一律视为开启（配置缺失/异常时保持日志开启的默认行为）。
 */
@Component
public class LogSwitchConfig {

    private static final Logger log = LoggerFactory.getLogger(LogSwitchConfig.class);

    public static final String ACCESS_LOG_KEY = "access-log.enable";
    public static final String ERROR_LOG_KEY = "error-log.enable";

    private static final long CACHE_TTL_SECONDS = 60;

    @Autowired(required = false)
    private MagicDynamicRedisClient redisClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isAccessLogEnabled() {
        return isEnabled(ACCESS_LOG_KEY);
    }

    public boolean isErrorLogEnabled() {
        return isEnabled(ERROR_LOG_KEY);
    }

    public boolean isEnabled(String configureKey) {
        String value = readConfig(configureKey);
        return !"false".equalsIgnoreCase(value == null ? "" : value.trim());
    }

    private String readConfig(String configureKey) {
        String cacheKey = "sys_config:" + configureKey;
        try {
            RedissonClient redisson = redisClient == null ? null : redisClient.getDataSource("default");
            if (redisson != null) {
                Object cached = redisson.getBucket(cacheKey).get();
                if (cached != null) {
                    return cached.toString();
                }
                String dbValue = queryFromDb(configureKey);
                if (dbValue == null) {
                    dbValue = "";
                }
                redisson.getBucket(cacheKey).set(dbValue, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                return dbValue;
            }
        } catch (Exception e) {
            log.warn("读取日志开关 redis 缓存失败，回源读库: {}", e.getMessage());
        }
        return queryFromDb(configureKey);
    }

    private String queryFromDb(String configureKey) {
        try {
            return jdbcTemplate.queryForObject(
                    "select configure_value from sys_configure where configure_key = ? and is_del = 0",
                    String.class, configureKey);
        } catch (Exception e) {
            return null;
        }
    }
}
