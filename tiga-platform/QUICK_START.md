# Tiga Platform - 快速开始指南

5分钟快速上手Tiga Platform模块化引擎。

## 🎯 选择您的使用方式

### 方式1：纯Java（无框架）

适用场景：独立应用、工具类、测试

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tiga.engine.core.EngineFactory;

// 创建引擎
EngineManager engine = EngineFactory.createDefaultEngineManager();

// 执行脚本
Object result = engine.execute(
    "magic",                              // 引擎类型
    "hello-world",                        // 脚本ID
    "return 'Hello, World!'",             // 脚本内容
    new HashMap<>(),                      // 参数
    5000                                  // 超时（毫秒）
);

System.out.println(result); // 输出: Hello, World!
```

### 方式2：Solon框架

适用场景：Solon Web应用

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-solon</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
import com.ocean.tiga.engine.api.EngineManager;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Controller;

@Controller
public class MyController {
    
    @Inject
    private EngineManager engineManager;
    
    @Mapping("/execute")
    public Object execute() throws Exception {
        return engineManager.execute(
            "magic",
            "test-script",
            "return 1 + 1",
            new HashMap<>(),
            5000
        );
    }
}
```

### 方式3：Spring Boot框架

适用场景：Spring Boot应用

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
import com.ocean.tiga.engine.api.EngineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyController {
    
    @Autowired
    private EngineManager engineManager;
    
    @GetMapping("/execute")
    public Object execute() throws Exception {
        return engineManager.execute(
            "magic",
            "test-script",
            "return 1 + 1",
            new HashMap<>(),
            5000
        );
    }
}
```

## 📚 更多示例

### 带参数的脚本

```java
Map<String, Object> params = new HashMap<>();
params.put("name", "Alice");
params.put("age", 30);

String script = """
    return 'Hello, ' + name + '! You are ' + age + ' years old.'
    """;

Object result = engineManager.execute("magic", "greeting", script, params, 5000);
System.out.println(result);
// 输出: Hello, Alice! You are 30 years old.
```

### 注册全局模块

```java
// 假设您有一个数据库模块
Object dbModule = createDbModule();

// 注册到引擎
engineManager.registerGlobalModule("db", dbModule);

// 现在可以在脚本中使用了
String script = """
    return db.query('SELECT * FROM users WHERE id = ?', userId)
    """;

Map<String, Object> params = new HashMap<>();
params.put("userId", 123);

Object result = engineManager.execute("magic", "query-user", script, params, 5000);
```

### 使用SQL引擎

```java
String script = """
    // 定义SQL查询
    var sql = '''
        SELECT u.name, o.total 
        FROM users u 
        JOIN orders o ON u.id = o.user_id
        WHERE o.total > ?
    ''';
    
    // 执行查询
    return db.query(sql, [1000]);
    """;

Object result = engineManager.execute("magic", "expensive-orders", script, params, 5000);
```

## 🔍 调试功能

### 连接调试WebSocket

```javascript
// 前端代码（JavaScript）
const ws = new WebSocket('ws://localhost:8080/debug-ws?sid=session-001');

ws.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('调试事件:', message.type, message.data);
};

ws.onopen = function() {
    console.log('调试连接已建立');
};
```

### 调试事件类型

- `BREAKPOINT_HIT` - 断点命中
- `FINISHED` - 执行完成
- `ERROR` - 执行错误
- `STOPPED` - 用户停止

## 📊 监控指标

### 访问Prometheus指标

```bash
# Solon
curl http://localhost:8080/metrics/prometheus

# Spring Boot
curl http://localhost:8080/actuator/prometheus
```

### 关键指标

```
# 缓存命中率
tiga_engine_cache_hits_total
tiga_engine_cache_misses_total

# 缓存大小
tiga_engine_cache_size

# 执行耗时
tiga_engine_execution_seconds
```

## 🚨 常见问题

### Q1: 如何清理脚本缓存？

```java
// 当脚本内容更新时
engineManager.refreshCache("script-001");
```

### Q2: 如何切换引擎类型？

```java
// Magic-Script引擎
engineManager.execute("magic", ...);

// Groovy引擎
engineManager.execute("groovy", ...);
```

### Q3: 如何配置线程池？

```java
EngineConfig config = EngineConfig.builder()
    .corePoolSize(32)
    .maxPoolSize(64)
    .queueCapacity(5000)
    .build();

EngineManager engine = EngineFactory.createEngineManager(config);
```

## 📖 深入学习

- [README.md](README.md) - 完整文档
- [MIGRATION.md](MIGRATION.md) - 迁移指南
- [BUILD.md](BUILD.md) - 构建说明
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - 项目状态

## 💡 提示

1. **生产环境** - 建议使用Solon或Spring Boot集成
2. **测试环境** - 可以使用纯Java方式
3. **性能优化** - 调整线程池大小和缓存容量
4. **监控** - 定期检查Prometheus指标

## 🎉 开始使用吧！

选择适合您的方式，开始使用Tiga Platform吧！

---

**需要帮助？** 查看文档或提交Issue：https://github.com/tiga-platform/issues
