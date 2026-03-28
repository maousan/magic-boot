# Tiga Platform - 迁移指南

本文档详细说明如何从旧版本的单体应用迁移到新的模块化架构。

## 🎯 迁移目标

- 解耦核心engine与Web框架
- 支持多种框架（Solon、Spring Boot）
- 保持现有功能完全兼容
- 零停机迁移

## 📋 迁移前准备

### 1. 备份现有代码

```bash
git checkout -b backup-before-migration
git commit -am "Backup before migration"
```

### 2. 确认依赖版本

确保您的项目使用以下版本：
- Java 17+
- Maven 3.6+
- Solon 3.8.0+ (如果使用Solon)
- Spring Boot 3.2.0+ (如果使用Spring Boot)

## 🔄 迁移步骤

### Step 1: 更新父POM

将原来的`pom.xml`重命名为`pom-old.xml`，创建新的父POM：

```bash
mv pom.xml pom-old.xml
mv pom-parent.xml pom.xml
```

新的父POM结构：

```xml
<project>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-platform-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>tiga-engine</module>
        <module>tiga-engine-solon</module>
        <module>tiga-engine-spring</module>
        <module>tiga-platform</module>
    </modules>

    <!-- ... dependencyManagement ... -->
</project>
```

### Step 2: 修改应用模块POM

编辑`tiga-platform/pom.xml`，替换engine依赖：

```xml
<dependencies>
    <!-- 移除旧的engine代码（已在父POM中移除）-->

    <!-- 添加新的Solon集成依赖 -->
    <dependency>
        <groupId>com.ocean.tiga</groupId>
        <artifactId>tiga-engine-solon</artifactId>
        <version>${project.version}</version>
    </dependency>

    <!-- 保留其他依赖 -->
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>

    <!-- ... -->
</dependencies>
```

### Step 3: 修改代码注入

#### 3.1 修改LoadComponent.java

**旧代码：**

```java
@Inject
private TigaEngineManager engineManager;
```

**新代码：**

```java
@Inject
private EngineManager engineManager; // 注入接口而非实现类
```

#### 3.2 修改DbModule注册

**旧代码：**

```java
@Component
public class DbModule {
    // DbModule内部使用TranUtils
}
```

**新代码：**

DbModule保持不变，但在注册时需要显式调用：

```java
@Configuration
public class AppConfig {
    @Bean
    public void initEngine(@Inject EngineManager engineManager,
                          @Inject DbModule dbModule) {
        // 注册全局模块
        engineManager.registerGlobalModule("db", dbModule);
    }
}
```

### Step 4: 处理调试功能

#### 4.1 WebSocket端点

**旧代码：**

```java
@ServerEndpoint("/debug-ws")
public class DebugWebSocket extends SimpleWebSocketListener {
    // 原实现在engine包中
}
```

**新代码：**

WebSocket端点已移到`tiga-engine-solon`模块中，自动注册，无需手动配置。

如果需要自定义，可以：

```java
@Configuration
public class WebSocketConfig {
    @Bean
    public SolonDebugWebSocket customDebugWebSocket() {
        return new SolonDebugWebSocket();
    }
}
```

#### 4.2 调试控制器

**旧代码：**

```java
@Controller
public class DebugController {
    @Inject
    private MagicEngine magicEngine;

    public void debug(String scriptId, ...) {
        magicEngine.executeDebug(sid, scriptText, params, timeout, breakpoints);
    }
}
```

**新代码：**

```java
@Controller
public class DebugController {
    @Inject
    private EngineManager engineManager;

    // 或者直接注入特定引擎
    @Inject
    @Named("magicScriptEngine")
    private ScriptEngine magicEngine;

    public void debug(String scriptId, ...) {
        // 需要传入DebugListener
        magicEngine.executeDebug(sid, scriptText, params, timeout, breakpoints, debugListener);
    }
}
```

### Step 5: 处理监控指标

#### 5.1 指标端点

**旧代码：**

```java
@Controller
public class MetricsController {
    @Mapping("/metrics/prometheus")
    public String prometheus() {
        return MagicMonitor.getPrometheusMetrics();
    }
}
```

**新代码：**

```java
@Controller
public class MetricsController {
    @Inject
    private PrometheusMetricsCollector metricsCollector;

    @Mapping("/metrics/prometheus")
    public String prometheus() {
        return metricsCollector.getMetricsReport();
    }
}
```

### Step 6: 迁移自定义扩展

如果您有自定义的Magic函数扩展：

#### 6.1 SqlFunctionExtension

**位置：** 原在`engine/magic/function/context/`

**迁移：**
1. 将文件复制到`tiga-engine/src/main/java/com/ocean/tiga/engine/magic/function/context/`
2. 更新包名：`com.ocean.tigaapi.engine` → `com.ocean.tiga.engine`
3. 更新导入：`Utils.md5` → `StringUtils.md5`

#### 6.2 Groovy扩展

类似处理，更新包名和工具类引用。

## ✅ 验证迁移

### 1. 编译检查

```bash
mvn clean compile
```

确保所有模块编译通过。

### 2. 运行测试

```bash
mvn test
```

### 3. 启动应用

```bash
cd tiga-platform
mvn solon:run
```

检查启动日志：

```
INFO  SolonEngineAutoConfiguration - Tiga Engine initialized successfully
INFO  MagicScriptEngine - MagicScript Engine initialized
```

### 4. 功能测试

测试以下功能：
- [ ] 脚本执行（Magic和Groovy）
- [ ] 数据库操作（通过DbModule）
- [ ] 调试功能（WebSocket连接）
- [ ] 监控指标（/metrics/prometheus）
- [ ] 缓存刷新（refreshCache）

### 5. 性能测试

对比新旧版本的性能：

```bash
# 缓存命中率
curl http://localhost:8080/metrics/prometheus | grep cache_hits

# 执行耗时
curl http://localhost:8080/metrics/prometheus | grep execution
```

## 🐛 常见问题

### Q1: 找不到EngineManager Bean

**问题：** 启动时报错`No qualifying bean of type 'EngineManager'`

**解决：** 确保添加了`tiga-engine-solon`依赖，并且配置类被扫描到。

```java
@Configuration
public class AppConfig {
    // 确保在Solon扫描路径下
}
```

### Q2: DbModule无法注入

**问题：** 脚本中无法使用`db`变量

**解决：** 确保在应用启动时注册了DbModule：

```java
@Bean
public void initModules(EngineManager engineManager, DbModule dbModule) {
    engineManager.registerGlobalModule("db", dbModule);
}
```

### Q3: 调试WebSocket连接失败

**问题：** 前端无法连接到`ws://localhost:8080/debug-ws`

**解决：**
1. 检查Solon版本是否支持WebSocket
2. 确保依赖了`solon-web-websocket`
3. 检查防火墙设置

### Q4: 性能下降

**问题：** 迁移后脚本执行变慢

**解决：**
1. 检查线程池配置是否正确
2. 检查缓存大小配置
3. 查看Prometheus指标，分析瓶颈

```java
EngineConfig config = EngineConfig.builder()
    .corePoolSize(100)    // 增加核心线程数
    .maxPoolSize(200)     // 增加最大线程数
    .maxCacheSize(1000)   // 增加缓存大小
    .build();
```

## 📊 迁移检查清单

- [ ] 更新父POM
- [ ] 更新应用模块POM
- [ ] 修改代码注入（TigaEngineManager → EngineManager）
- [ ] 注册全局模块（db, redis等）
- [ ] 迁移自定义函数扩展
- [ ] 更新监控端点
- [ ] 测试脚本执行
- [ ] 测试调试功能
- [ ] 测试监控指标
- [ ] 性能对比测试
- [ ] 文档更新

## 🆘 获取帮助

如果遇到问题：

1. 查看[GitHub Issues](https://github.com/tiga-platform/issues)
2. 阅读源码注释
3. 联系技术支持

## 📝 迁移日志

建议记录您的迁移过程：

```
日期：2026-03-27
迁移人：张三
迁移耗时：2小时
遇到的问题：
  1. Bean注入问题 - 已解决
  2. DbModule注册 - 已解决
备注：功能测试全部通过
```

祝您迁移顺利！🎉
