# Magic Plugin - PF4J 插件系统

基于 PF4J 的动态插件系统，支持运行时热加载、扩展点机制和动态 Controller 注册。

## 模块结构

```
magic-plugin/
├── src/main/java/org/ssssssss/magicboot/pf4j/
│   ├── configuration/
│   │   └── Pf4jPluginConfiguration.java    # 插件配置类
│   ├── component/
│   │   └── PluginControllerRegistrar.java  # Controller 动态注册
│   ├── extension/
│   │   ├── ExtensionPointManager.java      # 扩展点管理器
│   │   ├── ExtensionPointRegistry.java     # 扩展点注册中心
│   │   └── ApiInterceptorExtensionProcessor.java  # API 拦截器处理器
│   ├── service/
│   │   └── PluginManagerService.java       # 插件管理服务
│   ├── controller/
│   │   └── PluginAdminController.java      # 插件管理 API
│   ├── entity/
│   │   └── PluginInfo.java                 # 插件信息实体
│   └── model/
│       └── PluginStatus.java               # 插件状态枚举
└── src/test/java/                          # 单元测试
```

## 插件加载流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Spring Boot 应用启动                                  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  1. Pf4jPluginConfiguration.pluginManager()                                  │
│     - 创建 SpringPluginManager(pluginsRoot)                                  │
│     - PF4J 自动扫描插件目录: D:/mb/plugins/                                   │
│     - 加载所有 .jar 插件 (loadPlugins)                                        │
│     - 启动所有插件 (startPlugins)                                             │
│                                                                              │
│  文件: configuration/Pf4jPluginConfiguration.java:34                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  2. ExtensionPointRegistry 构造                                              │
│     - 持有 PluginManager 引用                                                 │
│     - 扩展点缓存: Map<Class<?>, List<?>> extensionCache                      │
│     - getExtensions() 通过 pluginManager.getExtensions(type) 获取            │
│                                                                              │
│  文件: extension/ExtensionPointRegistry.java:25                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  3. ExtensionPointManager @PostConstruct init()                              │
│     - pluginManager.addPluginStateListener(this)                             │
│     - 监听插件状态变化                                                         │
│     - STARTED/STOPPED 时清除扩展点缓存                                        │
│                                                                              │
│  文件: extension/ExtensionPointManager.java:28-32                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  4. PluginControllerRegistrar Bean 创建                                      │
│     - pluginManager.addPluginStateListener(registrar)                        │
│     - 为已启动的插件注册 Controller                                            │
│     - 动态注册到 Spring MVC RequestMappingHandlerMapping                      │
│                                                                              │
│  文件: component/PluginControllerRegistrar.java:52-59                        │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  5. 扩展点使用 (如 PermissionInterceptor)                                    │
│     extensionPointManager.getSortedApiInterceptors()                         │
│     └─→ registry.getExtensions(ApiInterceptorExtension.class)                │
│         └─→ pluginManager.getExtensions(ApiInterceptorExtension.class)       │
│             └─→ 返回所有插件中实现了该接口的扩展点                               │
│                                                                              │
│  文件: magic-boot-master/.../interceptor/PermissionInterceptor.java          │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 插件状态流转

```
CREATED → (loadPlugin) → RESOLVED → (startPlugin) → STARTED → (stopPlugin) → STOPPED
                                                              ↓
                                                        (unloadPlugin) → DISABLED
```

## 扩展点发现机制

PF4J 扩展点发现原理：

1. **编译时**：`@Extension` 注解被 `ExtensionAnnotationProcessor` 处理
2. **生成文件**：生成 `META-INF/extensions.idx` 文件，记录扩展点实现类
3. **运行时**：`pluginManager.getExtensions()` 扫描所有插件的 `extensions.idx`，实例化并返回

```java
// ExtensionPointRegistry.java
public <T> List<T> getExtensions(Class<T> type) {
    return (List<T>) extensionCache.computeIfAbsent(type, t -> {
        List<T> extensions = pluginManager.getExtensions(type);
        return Collections.unmodifiableList(extensions);
    });
}
```

## 关键组件

### 1. Pf4jPluginConfiguration

插件配置类，创建 `SpringPluginManager` Bean。

```java
@Bean
public SpringPluginManager pluginManager() {
    Path pluginsRoot = Paths.get(pluginDir).toAbsolutePath();
    return new SpringPluginManager(pluginsRoot);
}
```

### 2. ExtensionPointRegistry

扩展点注册中心，负责缓存和提供扩展点实例。

### 3. ExtensionPointManager

扩展点管理器，监听插件状态变化，管理扩展点生命周期。

### 4. PluginControllerRegistrar

动态注册插件 Controller 到 Spring MVC。

### 5. PluginManagerService

插件管理核心服务，提供安装、卸载、启动、停止、重载等功能。

## 管理 API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/plugin/admin/list` | GET | 获取所有插件列表 |
| `/plugin/admin/install` | POST | 安装插件 |
| `/plugin/admin/start` | POST | 启动插件 |
| `/plugin/admin/stop` | POST | 停止插件 |
| `/plugin/admin/reload` | POST | 重载插件 |
| `/plugin/admin/uninstall` | POST | 卸载插件 |

## 开发插件

参考 `magic-plugin-demo` 模块：

1. 实现 `Plugin` 接口（继承 `SpringPlugin`）
2. 创建 `plugin.properties` 配置文件
3. 实现扩展点接口并标注 `@Extension`
4. 使用 `maven-assembly-plugin` 打包

### plugin.properties 示例

```properties
plugin.id=demo-plugin
plugin.class=org.ssssssss.magicboot.demo.DemoPlugin
plugin.version=1.0.0
plugin.provider=MagicBoot Team
plugin.dependencies=
```

### 扩展点实现示例

```java
@Extension
public class DemoApiInterceptor implements ApiInterceptorExtension {

    @Override
    public Object preHandle(ApiInterceptorContext context) {
        // 前置拦截逻辑
        return null; // 返回 null 继续处理
    }

    @Override
    public void postHandle(ApiInterceptorContext context, Object returnValue) {
        // 后置处理逻辑
    }

    @Override
    public void onError(ApiInterceptorContext context, Exception ex) {
        // 异常处理逻辑
    }

    @Override
    public int getOrder() {
        return 100; // 优先级，数值越小越先执行
    }
}
```

## 配置

在 `application.yml` 中配置：

```yaml
plugin:
  enabled: true              # 是否启用插件系统
  dir: D:/mb/plugins/        # 插件目录
  auto-load: true            # 启动时自动加载插件目录中的插件
  auto-start: true           # 加载后自动启动插件（仅当 auto-load 为 true 时生效）
```

| 配置项 | 默认值 | 描述 |
|--------|--------|------|
| `plugin.enabled` | `true` | 是否启用插件系统 |
| `plugin.dir` | `D:/mb/plugins/` | 插件目录 |
| `plugin.auto-load` | `true` | 应用启动时是否自动加载插件目录中的所有插件 |
| `plugin.auto-start` | `true` | 加载插件后是否自动启动（仅当 auto-load 为 true 时生效） |

## 单元测试

```bash
# 运行插件模块测试
mvn test -pl magic-plugin

# 测试覆盖
- ExtensionPointRegistryTest: 6 个测试
- ExtensionPointManagerTest: 4 个测试
- ApiInterceptorExtensionProcessorTest: 7 个测试
```
