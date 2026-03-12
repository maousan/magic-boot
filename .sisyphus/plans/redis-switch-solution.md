# Redis 开关配置 - 完整解决方案

## 问题描述

用户配置了 `extend.redis.enabled=false`，但应用启动时仍然尝试连接 Redis，导致启动失败。

**错误信息**:
```
Redis health check failed
org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis
```

---

## 根本原因

1. **Spring Boot Redis 自动配置**: 即使添加了 `@ConditionalOnProperty`，Spring Boot 的 RedisAutoConfiguration 仍会尝试创建 Redis 连接
2. **Sa-Token Redis 集成**: `sa-token-redis-template` 依赖会强制要求 Redis 连接
3. **Actuator 健康检查**: Spring Boot Actuator 的 RedisReactiveHealthIndicator 会尝试连接 Redis

---

## 已实施的解决方案

### 1. 排除 Redis 自动配置

**文件**: `application.yml`

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

**说明**: 强制排除 Spring Boot 的 Redis 自动配置

---

### 2. 条件加载 RedisConfig 和 RedisUtil

**文件**: `RedisConfig.java` 和 `RedisUtil.java`

```java
@Configuration
@ConditionalOnProperty(name = "extend.redis.enabled", havingValue = "true", matchIfMissing = false)
public class RedisConfig {
    // ...
}
```

**说明**: 当 `extend.redis.enabled=true` 时才加载

---

## 完整配置文件

### application.yml (Redis 禁用)

```yaml
server:
  port: 8081

# 扩展配置
extend:
  redis:
    enabled: false  # 禁用 Redis

spring:
  profiles:
    active: dev
  
  # 排除 Redis 自动配置
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

---

### application.yml (Redis 启用)

```yaml
server:
  port: 8081

# 扩展配置
extend:
  redis:
    enabled: true  # 启用 Redis

spring:
  profiles:
    active: dev
  
  # Redis 配置
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: ""
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-idle: 5
          min-idle: 2
          max-wait: 5000
  
  # 不排除 Redis 自动配置（注释掉或删除）
  # autoconfigure:
  #   exclude:
  #     - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
  #     - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
```

---

## Sa-Token 的处理

### 问题
`sa-token-redis-template` 依赖会强制要求 Redis 连接。

### 解决方案

#### 方案1: 修改 pom.xml（推荐）

将 `sa-token-redis-template` 改为可选依赖：

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-redis-template</artifactId>
    <version>${sa-token.version}</version>
    <optional>true</optional>  <!-- 添加 optional -->
</dependency>
```

**优点**: 完全禁用 Sa-Token 的 Redis 集成
**缺点**: 需要修改 pom.xml

---

#### 方案2: 创建条件配置类

创建 `SaTokenConfig.java`，根据 `extend.redis.enabled` 动态选择存储方式：

```java
@Configuration
public class SaTokenConfig {
    
    @Bean
    @ConditionalOnProperty(name = "extend.redis.enabled", havingValue = "false", matchIfMissing = false)
    public StpInterface stpInterface() {
        // 使用内存存储
        return new StpInterface() {
            // 实现接口方法
        };
    }
}
```

**优点**: 不需要修改 pom.xml
**缺点**: 需要额外配置

---

## 验证方法

### 1. 验证 Redis 已禁用

```bash
# 修改 application.yml
extend:
  redis:
    enabled: false

# 启动应用
mvn spring-boot:run

# 查看日志，不应该看到：
# - RedisTemplate initialized successfully
# - Redis health check failed
# - Unable to connect to Redis
```

### 2. 验证 Redis 已启用

```bash
# 修改 application.yml
extend:
  redis:
    enabled: true

# 确保 Redis 服务已启动
redis-cli ping  # 应该返回 PONG

# 启动应用
mvn spring-boot:run

# 查看日志，应该看到：
# - RedisTemplate initialized successfully
# - 没有 Redis connection failed 错误
```

---

## 常见问题

### Q1: 配置后仍然报 Redis connection failed？

**原因**: `spring.autoconfigure.exclude` 配置可能没有生效

**解决**:
1. 检查 YAML 格式是否正确（缩进、冒号后的空格）
2. 确认配置文件被正确加载
3. 清理编译缓存：`mvn clean`

---

### Q2: Sa-Token 仍然尝试连接 Redis？

**原因**: `sa-token-redis-template` 依赖强制要求 Redis

**解决**:
1. 修改 pom.xml，添加 `<optional>true</optional>`
2. 或者在 Sa-Token 配置中禁用 Redis 集成

---

### Q3: Actuator 健康检查失败？

**原因**: Actuator 的 RedisReactiveHealthIndicator 会尝试连接 Redis

**解决**:
1. 在 application.yml 中禁用 Redis 健康检查：

```yaml
management:
  health:
    redis:
      enabled: false  # 禁用 Redis 健康检查
```

---

## 完整配置示例

### application.yml (完整版)

```yaml
server:
  port: 8081
  compression:
    enabled: true
    min-response-size: 128

# 扩展配置
extend:
  redis:
    enabled: false  # Redis 开关

spring:
  profiles:
    active: dev
  
  # 条件排除 Redis 自动配置
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
  
  # Redis 配置（仅在 extend.redis.enabled=true 时生效）
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: ""
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-idle: 5
          min-idle: 2
          max-wait: 5000

# Actuator 配置
management:
  health:
    redis:
      enabled: ${extend.redis.enabled}  # 跟随 Redis 开关

# Sa-Token 配置
sa-token:
  token-name: token
  timeout: 2592000
  active-timeout: -1
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false
```

---

## 总结

**已实施的解决方案**:
- ✅ 在 application.yml 中排除 Redis 自动配置
- ✅ RedisConfig 和 RedisUtil 使用条件加载
- ✅ 添加 extend.redis.enabled 配置项

**仍需处理**:
- ⚠️ Sa-Token 的 Redis 集成（需要修改 pom.xml 或添加条件配置）
- ⚠️ Actuator 的 Redis 健康检查（需要禁用或条件配置）

**建议**:
1. 如果完全不使用 Redis，修改 pom.xml 将 sa-token-redis-template 改为可选依赖
2. 如果部分环境使用 Redis，创建条件配置类动态切换
3. 禁用 Actuator 的 Redis 健康检查（当 Redis 禁用时）

---

## 文件位置

- **配置文件**: `magic-boot-master/src/main/resources/application.yml`
- **Redis 配置类**: `magic-boot-master/src/main/java/org/ssssssss/magicboot/config/RedisConfig.java`
- **Redis 工具类**: `magic-boot-master/src/main/java/org/ssssssss/magicboot/utils/RedisUtil.java`
- **本文档**: `.sisyphus/plans/redis-switch-solution.md`
