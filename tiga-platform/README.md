# Tiga Platform - 模块化重构版

[![Java Version](https://img.shields.io/badge/Java-17-green.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)

Tiga Platform 是一个高性能、模块化的脚本引擎框架，支持 Magic-Script 和 Groovy 两种脚本语言，并提供完善的调试、监控和缓存管理能力。

## 📦 项目结构

```
tiga-platform/
├── tiga-engine/                    # 核心引擎模块（框架无关）
│   ├── api/                        # 公共接口定义
│   ├── core/                       # 核心实现（EngineManager, EngineFactory）
│   ├── magic/                      # Magic-Script引擎实现
│   ├── groovy/                     # Groovy引擎实现
│   ├── sql/                        # SQL引擎（Apache Calcite）
│   ├── monitor/                    # 监控系统（Prometheus）
│   └── util/                       # 工具类
│
├── tiga-engine-solon/              # Solon框架集成
│   └── solon/                      # 自动配置、WebSocket调试
│
├── tiga-engine-spring/             # Spring Boot框架集成
│   └── spring/                     # 自动配置、调试支持
│
└── tiga-platform/                  # 应用层（原项目）
    ├── api/                        # 业务API接口
    ├── db/                         # 数据库模块（DbModule）
    └── plugin/                     # 插件系统
```

## ✨ 核心特性

### 1. **框架无关**
- 核心engine模块完全独立，不依赖任何Web框架
- 可轻松集成到Solon、Spring Boot或其他框架

### 2. **高性能执行**
- 分段锁机制，减少并发编译竞争
- 基于MD5的脚本缓存和热更新
- 独立线程池，资源隔离

### 3. **完善的调试支持**
- WebSocket实时调试
- 断点、单步执行、变量查看
- 自动行号偏移处理

### 4. **监控与度量**
- Prometheus指标收集
- 缓存命中率统计
- 执行耗时分析

### 5. **安全沙箱**
- 脚本安全检查
- 危险类与操作屏蔽
- AST级别限制

## 🚀 快速开始

### 方式1：Solon框架集成

#### 1. 添加依赖

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-solon</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 注入EngineManager

```java
@Inject
private EngineManager engineManager;

public Object executeScript() throws Exception {
    String scriptText = "return db.query('SELECT * FROM users')";

    return engineManager.execute(
        "magic",           // 引擎类型
        "script-001",      // 脚本ID
        scriptText,        // 脚本内容
        new HashMap<>(),   // 参数
        5000               // 超时（毫秒）
    );
}
```

#### 3. 注册全局模块

```java
@Configuration
public class AppConfig {
    @Bean
    public void initModules(EngineManager engineManager) {
        // 注册数据库模块
        engineManager.registerGlobalModule("db", dbModule);

        // 注册Redis模块
        engineManager.registerGlobalModule("redis", redisModule);
    }
}
```

### 方式2：Spring Boot框架集成

#### 1. 添加依赖

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 配置application.yml

```yaml
tiga:
  engine:
    core-pool-size: 16
    max-pool-size: 32
    queue-capacity: 2000
    max-cache-size: 800
```

#### 3. 使用方式与Solon相同

```java
@Autowired
private EngineManager engineManager;
```

### 方式3：纯Java使用（无框架）

```java
// 创建引擎配置
EngineConfig config = EngineConfig.builder()
    .corePoolSize(16)
    .maxPoolSize(32)
    .metricsCollector(new PrometheusMetricsCollector())
    .build();

// 创建引擎管理器
EngineManager engineManager = EngineFactory.createEngineManager(config);

// 注册模块
engineManager.registerGlobalModule("db", dbModule);

// 执行脚本
Object result = engineManager.execute("magic", "test-001", scriptText, params, 5000);
```

## 📊 调试功能

### 启用调试WebSocket

#### Solon配置

WebSocket端点会自动注册到 `/debug-ws`

#### Spring Boot配置

需要手动配置WebSocket（参考Spring文档）

### 前端连接示例

```javascript
const ws = new WebSocket('ws://localhost:8080/debug-ws?sid=session-001');

ws.onmessage = function(event) {
    const message = JSON.parse(event.data);

    switch(message.type) {
        case 'BREAKPOINT_HIT':
            console.log('断点命中:', message.data.line);
            console.log('当前变量:', message.data.variables);
            break;
        case 'FINISHED':
            console.log('执行完成:', message.data);
            break;
        case 'ERROR':
            console.error('执行错误:', message.data);
            break;
    }
};
```

## 📈 监控指标

访问Prometheus指标端点：

```
GET /actuator/prometheus  (Spring Boot)
GET /metrics/prometheus   (Solon)
```

可用指标：
- `tiga_engine_cache_hits` - 缓存命中次数
- `tiga_engine_cache_misses` - 缓存未命中次数
- `tiga_engine_cache_size` - 当前缓存大小
- `tiga_engine_execution` - 执行耗时统计

## 🔧 迁移指南

### 从旧版本迁移

如果您正在使用旧版本的`tiga-platform`（单体应用），请按以下步骤迁移：

#### 1. 替换依赖

```xml
<!-- 旧依赖 -->
<dependency>
    <groupId>com.ocean.tigaapi</groupId>
    <artifactId>tiga-platform</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 新依赖 -->
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-solon</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. 修改注入类型

```java
// 旧代码
@Inject
private TigaEngineManager engineManager;

// 新代码（注入接口）
@Inject
private EngineManager engineManager;
```

#### 3. DbModule保持不变

DbModule继续保留在应用层，通过`registerGlobalModule`注入：

```java
engineManager.registerGlobalModule("db", dbModule);
```

## 🧪 测试

```bash
# 编译所有模块
mvn clean install

# 运行测试
mvn test

# 运行Solon示例应用
cd tiga-platform
mvn solon:run
```

## 📝 开发进度

### ✅ 已完成
- [x] 核心接口定义（ScriptEngine, EngineManager, DebugListener, MetricsCollector）
- [x] tiga-engine核心模块（框架无关）
- [x] MagicScriptEngine完整实现
- [x] CalciteEngine SQL引擎
- [x] Prometheus监控集成
- [x] tiga-engine-solon集成模块
- [x] tiga-engine-spring集成模块

### 🚧 进行中
- [ ] GroovyScriptEngine完整实现（当前为简化版）
- [ ] Groovy调试功能
- [ ] 更多单元测试

### 📋 计划中
- [ ] 性能优化
- [ ] 更多脚本语言支持（Python, JavaScript等）
- [ ] 分布式缓存支持
- [ ] WebIDE调试界面

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 License

Apache License 2.0

## 📧 联系方式

- Email: tiga-platform@example.com
- GitHub: https://github.com/tiga-platform
