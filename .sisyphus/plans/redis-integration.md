# Redis 集成实现计划

## TL;DR

> **目标**: 集成 Redisson 到 Spring Boot 项目，用于 WebSocket 日志缓存、会话管理等场景
>
> **集成方式**: 添加 Redisson 依赖（Spring Data Redis）
> **Redisson版本**: 3.23.4
> **配置方式**: application.yml 配置文件

---

## 上下文

### 当前技术栈
- **Spring Boot**: 3.1.2
- **Sa-Token**: 1.35.0.RC（用于认证）
- **Druid**: 1.2.20（数据库连接池）
- **Magic-API Redis插件**: 已存在，使用Redisson 3.23.4（与我们的选择不冲突）

### Magic-API Redis插件架构
- **配置存储**: `data/magic-api/redis/local.json`
- **配置管理**: 通过 Magic-API Web UI (`/magic/web`)动态添加/删除数据源
- **客户端**: Redisson（版本 3.23.4）
- **动态数据源**: 支持多个Redis配置，可根据`datasourceKey`切换

---

## 工作目标

### Phase 1: 基础设施
- [ ] 在主项目 pom.xml 添加 Redisson 依赖
- [ ] 在 application.yml 添加 Redisson 配置（单机模式）
- [ ] 测试 Redis 连接

### Phase 2: 功能实现（可选）
- [ ] 创建 Redisson 配置类
- [ ] 创建 Redis 工具类（封装常用操作）
- [ ] 实现会话管理（可选，用于WebSocket会话信息缓存）
- [ ] 实现日志缓冲（可选，用于优化高频日志写入）

---

## 实施计划

### Phase 1: 添加 Redisson 依赖

**1.1 添加 Maven 依赖**
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.23.4</version>
</dependency>
```

**1.2 验证依赖**
- 运行 `mvn dependency:tree | grep redisson`
- 确认版本正确加载

---

### Phase 2: 配置 Redisson

**2.1 更新 application.yml**

```yaml
spring:
  data:
    redis:
      # 单机配置
      host: 127.0.0.1
      port: 6379
      password: zintis
      database: 3
      timeout: 5000
      
      # 连接池配置（可选，根据需要调整）
      pool-size: 8
      min-idle: 5
      
      # 编码配置
      encoding: UTF-8
      
      # 指定数据库
      database: 0
```

**配置说明**:
- `host`: Redis 服务器地址（本地：127.0.0.1，生产：实际服务器IP）
- `port`: Redis 端口（默认：6379）
- `password`: Redis 密码
- `database`: Redis 数据库索引（默认：0）
- `timeout`: 连接超时（毫秒，默认：5000）
- `pool-size`: 连接池大小（默认：8）
- `min-idle`: 最小空闲连接（默认：5）

**数据库索引说明**：
- 0: 默认数据库
- 1- 数据库1
- 2: 数据库2
- ...
- n: 数据库n

---

### Phase 3: 创建 Redisson 配置类（可选）

**3.1 创建配置类**
```java
package org.ssssssss.magicboot.config;

import org.redisson.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:zintis}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    @Value("${spring.data.redis.timeout:5000}")
    private int timeout;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServerConfig()
                .setAddress(host + ":" + port)
                .setPassword(password)
                .setDatabase(database)
                .setTimeout(timeout);

        return Redisson.create(config);
    }
}
```

---

### Phase 4: 创建 Redis 工具类（可选）

**4.1 创建工具类**
```java
package org.ssssssss.magicboot.utils;

import org.redisson.RedissonClient;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 通用缓存操作
     */
    public void set(String key, Object value) {
        redissonClient.getBucket(key).set(value);
    }

    public Object get(String key) {
        return redissonClient.getBucket(key).get();
    }

    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 设置过期时间
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redissonClient.getBucket(key).expire(timeout, unit);
    }

    /**
     * 检查键是否存在
     */
    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 设置值并过期
     */
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        set(key, value);
        setExpire(key, timeout, unit);
    }
}
```

---

## 使用场景

### 场景 1: WebSocket 会话管理（可选）
```java
// 存储会话信息
String sessionKey = "session:" + sessionId;
redisUtil.set(sessionKey, sessionData);

// 5分钟过期
redisUtil.setExpire(sessionKey, 5, TimeUnit.MINUTES);

// 检查会话是否有效
if (!redisUtil.exists(sessionKey)) {
    // 会话已过期
}
```

### 场景 2: 日志缓冲（可选）
```java
// 日志写入前先写入Redis
String bufferKey = "log:buffer:" + userId;
List<String> logLines = new ArrayList<>();
logLines.add(logLine);

// 批量写入 Redis（减少I/O）
String bufferData = JSON.toJSONString(logLines);
redisUtil.setWithExpire(bufferKey, bufferData, 10, TimeUnit.SECONDS);

// 定期批量刷盘到文件系统
```

### 场景 3: 用户偏好设置（可选）
```java
// 缓存用户界面偏好
String preferencesKey = "user:preferences:" + userId;
redisUtil.set(preferencesKey, userPreferences);
```

---

## 测试验证

### 基础连接测试
```bash
# 1. 添加依赖后编译
mvn clean compile

# 2. 启动应用
mvn spring-boot:run

# 3. 查看日志确认 Redis 连接
# 应该看到类似这样的日志：
# Redisson: 127.0.0.1:6379 connected
# Successfully connected to Redis
```

### 功能测试（如果实现了）
```java
// 测试 Redisson 配置类
@Autowired
private RedissonConfig redissonConfig;

// 测试连接
RedissonClient client = redissonConfig.redissonClient();
client.getBucket("test").set("value");
String result = client.getBucket("test").get();
System.out.println("Result: " + result); // 应该输出 "value"
```

---

## 注意事项

### 1. Redis 连接配置
- 开发环境：使用本地 Redis（127.0.0.1:6379）
- 生产环境：修改为实际 Redis 服务器 IP 地址
- 密码：默认 "zintis"，生产环境必须修改
- 端口：默认 6379，确认 Redis 服务器端口
- 超时时间：默认 5000 毫秒

### 2. 数据库索引
- Redis 默认使用数据库 0
- 如果需要使用不同的数据库，修改 `database: 1`
- 每个数据库是独立的命名空间

### 3. 连接池
- `pool-size`: 连接池大小（默认 8）
- `min-idle`: 最小空闲连接（默认 5）
- 根据实际并发需求调整

### 4. 编码
- 确保所有 key 使用 UTF-8 编码
- Spring Boot Redisson 自动处理编码

### 5. 过期时间
- 根据业务场景设置不同的过期时间
- 常见过期时间：
  - 用户会话：30分钟
  - 日志缓冲：10 秒
  - 验证码：5 分钟
  - 用户偏好：1 小时

---

## 实施步骤

### Step 1: 添加依赖
```bash
# 编辑 pom.xml，添加 Redisson 依赖
# 验证依赖添加成功
mvn clean package
```

### Step 2: 配置 application.yml
```bash
# 编辑 application.yml，添加 Redis 配置
# 验证应用启动成功
mvn spring-boot:run
```

### Step 3: 测试连接（可选，但推荐）
```java
// 创建测试 Controller
@RestController
@RequestMapping("/test/redis")
public class TestRedisController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/set")
    public String testSet() {
        redissonClient.getBucket("test").set("hello");
        return "Set success";
    }

    @GetMapping("/get")
    public String testGet() {
        String value = (String) redissonClient.getBucket("test").get();
        return "Value: " + value;
    }
}
```

### Step 4: 实际应用（根据需求）
根据实际业务场景使用 Redis：
- WebSocket 会话管理
- 日志缓冲
- 用户偏好缓存
- 权限缓存
- 接口限流
- 分布式锁

---

## 决策点

### 必做：配置 Redis 连接
- ⚠️ 建议添加连接测试
- 建议生产环境使用独立 Redis 服务器
- 建议配置 Redis 密码（非 zintis）

### 可选：创建工具类和配置类
- 如果需要频繁使用 Redis，建议创建 RedisUtil 工具类
- 如果配置复杂，建议创建 RedissonConfig 配置类
- 如果有多个环境（dev/test/prod），可以使用 Spring Profiles

### 不建议
- ❌ 不要直接使用 RedissonClient API（建议封装到工具类）
- ❌ 不要在业务代码中硬编码连接配置（使用配置文件）
- ❌ 不要忽略过期时间管理（设置合理的过期时间）

---

## 成功标准

- [ ] Redisson 依赖成功添加
- [ ] application.yml 配置正确
- [ ] 应用启动成功，Redis 连接正常
- [ ] 基础功能测试通过

---

## 与 Magic-API Redis 插件的关系

### 不冲突
- **Magic-API Redis插件**：使用独立的 Redisson 客户
- **我们的集成**：使用 Spring Data Redis（不同的 Redisson 实例）
- **数据库选择**：
  - Magic-API 使用数据库索引选择
  - 我们的集成可以配置不同的数据库索引
- **共存方式**：两个 Redisson 实例可以共存，连接到同一个 Redis 服务器

### 配置方式
- **Magic-API 方式**：通过 Web UI 动态管理配置（需要重启）
- **我们的方式**：通过 application.yml 静态配置（无需重启）
- **优势**：我们的方式更简单直接

---

## 下一步

是否开始实施？
1. ✅ **执行 Step 1**：添加 Redisson 依赖
2. ✅ **执行 Step 2**：配置 application.yml
3. ✅ **执行 Step 3**：测试连接
4. ✅ **执行 Step 4**：根据实际需求实现功能

请确认是否继续？