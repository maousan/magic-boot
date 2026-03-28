# Tiga Platform - 应用层迁移指南

## 📋 迁移步骤

### Step 1: 备份原项目

```bash
# 备份当前项目
git add .
git commit -m "Backup before migration"
git checkout -b migration-backup
```

### Step 2: 更新pom.xml

将`tiga-platform/pom.xml`修改为使用新的模块化依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ocean.tiga</groupId>
        <artifactId>tiga-platform-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>tiga-platform</artifactId>
    <packaging>jar</packaging>

    <name>Tiga Platform Application</name>

    <dependencies>
        <!-- 使用新的Solon集成模块 -->
        <dependency>
            <groupId>com.ocean.tiga</groupId>
            <artifactId>tiga-engine-solon</artifactId>
        </dependency>

        <!-- Solon Web框架 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-web</artifactId>
        </dependency>

        <!-- 数据库相关 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>wood-solon-plugin</artifactId>
        </dependency>

        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>

        <!-- JSON处理 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>snack3</artifactId>
        </dependency>

        <!-- 其他工具 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- 测试 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.noear</groupId>
                <artifactId>solon-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: 修改代码注入

#### 3.1 LoadComponent.java

**修改前：**

```java
@Inject
private TigaEngineManager tigaEngineManager;
```

**修改后：**

```java
import com.ocean.tiga.engine.api.EngineManager;

@Inject
private EngineManager engineManager;
```

#### 3.2 注册全局模块

**修改前：**

```java
tigaEngineManager.registerGlobalModule("db", dsManager.getAll());
```

**修改后：**

```java
engineManager.registerGlobalModule("db", dsManager.getAll());
```

### Step 4: 保持DbModule不变

DbModule继续保留在应用层，因为它使用了Solon的`TranUtils`进行事务管理：

```java
// DbModule.java - 保持不变
package com.ocean.tigaapi.db;

import org.noear.wood.DbContext;
import org.noear.wood.annotation.Tran;

public class DbModule {
    // 使用TranUtils进行事务管理
    // 保持不变
}
```

### Step 5: 调试功能适配

如果您的代码中使用了调试功能，需要进行以下修改：

#### 5.1 DebugController

**修改前：**

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

**修改后：**

```java
@Controller
public class DebugController {
    @Inject
    private EngineManager engineManager;

    // 或者注入特定的引擎
    @Inject
    @Named("magicScriptEngine")
    private ScriptEngine magicEngine;

    public void debug(String scriptId, ...) {
        // DebugListener会通过Solon集成模块自动注入
        magicEngine.executeDebug(sid, scriptText, params, timeout, breakpoints, null);
    }
}
```

### Step 6: 监控指标适配

#### 6.1 MetricsController

**修改前：**

```java
@Controller
public class MetricsController {
    @Mapping("/metrics/prometheus")
    public String prometheus() {
        return MagicMonitor.getPrometheusMetrics();
    }
}
```

**修改后：**

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

### Step 7: 删除旧的engine包

迁移完成后，删除`src/main/java/com/ocean/tigaapi/engine`目录：

```bash
# 确保所有功能正常后再删除
rm -rf src/main/java/com/ocean/tigaapi/engine
```

## ✅ 验证清单

### 编译检查

```bash
cd tiga-platform
mvn clean compile
```

### 功能测试

- [ ] 应用启动成功
- [ ] 脚本执行正常（Magic和Groovy）
- [ ] 数据库操作正常（DbModule）
- [ ] 调试功能正常（WebSocket）
- [ ] 监控指标正常（/metrics/prometheus）
- [ ] 动态路由正常
- [ ] 所有API接口正常

### 性能对比

```bash
# 对比迁移前后的性能
# 1. 缓存命中率
curl http://localhost:8080/metrics/prometheus | grep cache_hits

# 2. 执行耗时
curl http://localhost:8080/metrics/prometheus | grep execution
```

## 🚨 常见问题

### Q1: 编译错误 - 找不到TigaEngineManager

**问题：** `cannot find symbol: class TigaEngineManager`

**解决：** 替换为`EngineManager`接口

```java
// 错误
import com.ocean.tigaapi.engine.TigaEngineManager;

// 正确
import com.ocean.tiga.engine.api.EngineManager;
```

### Q2: 运行错误 - EngineManager Bean不存在

**问题：** `No qualifying bean of type 'EngineManager'`

**解决：** 确保依赖了`tiga-engine-solon`模块

```xml
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-solon</artifactId>
</dependency>
```

### Q3: 调试WebSocket连接失败

**问题：** WebSocket连接失败

**解决：** WebSocket端点已自动注册到`/debug-ws`，无需手动配置

### Q4: 监控指标404

**问题：** `/metrics/prometheus`返回404

**解决：** 创建MetricsController并注入PrometheusMetricsCollector

## 📝 完整示例

### 修改后的LoadComponent.java

```java
package com.ocean.tigaapi.init;

import java.util.List;
import java.util.Map;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tigaapi.api.entity.ApiEntity;
import com.ocean.tigaapi.api.router.ApiRouter;
import com.ocean.tigaapi.api.service.ApiService;
import com.ocean.tigaapi.component.service.ComponentService;
import com.ocean.tigaapi.db.entity.DataSourceEntity;
import com.ocean.tigaapi.db.service.DynamicDatasourceService;

@Component
public class LoadComponent implements LifecycleBean {

    @Inject
    private DynamicDatasourceService dynamicDatasourceService;

    @Inject
    private ApiService apiRepository;

    @Inject
    private DynamicDatasourceService dsManager;

    @Inject
    private ApiRouter routerService;

    @Inject
    private EngineManager engineManager;  // 改为注入接口

    @Override
    public void start() throws Throwable {
        System.out.println(">>> [Tiga Platform] 正在初始化低代码引擎...");

        // 1. 注册RDBMS插件到脚本引擎
        engineManager.registerGlobalModule("db", dsManager.getAll());

        // 2. 从主库加载所有【数据源】配置
        initDynamicDataSources();

        // 3. 从主库加载所有【启用的 API】配置
        // initDynamicRoutes();

        // 4. 初始化所有业务组件
        initBusinessComponent();

        System.out.println(">>> [Tiga Platform] 引擎启动完成，动态接口已就绪。");
    }

    private void initDynamicDataSources() throws Exception {
        List<DataSourceEntity> dsList = dynamicDatasourceService.getAllExternalDs();
        System.out.println("找到外部数据源配置数: " + dsList.size());

        for (DataSourceEntity entity : dsList) {
            try {
                dsManager.registerDs(entity);
            } catch (Exception e) {
                System.err.println("数据源 [" + entity.getName() + "] 加载失败: " + e.getMessage());
            }
        }
    }

    private void initDynamicRoutes() throws Exception {
        List<ApiEntity> apiList = apiRepository.getAllActiveApis();
        System.out.println("找到启用的动态接口数: " + apiList.size());

        for (ApiEntity api : apiList) {
            routerService.register(api);
        }
    }

    @Inject
    private ComponentService componentService;

    private void initBusinessComponent() throws Exception {
        List<Map<String, Object>> componentList = componentService.getBusinessComponentList();
        System.out.println("初始化所有业务组件接口数: " + componentList.size());

        for (Map<String, Object> component : componentList) {
            Object businessCode = component.get("script");
            if (businessCode != null) {
                componentService.register(component.get("id").toString(), businessCode.toString());
            }
        }
    }
}
```

## 🎉 迁移完成

恭喜！您已成功完成应用层的迁移。

现在您可以：
- 使用新的模块化架构
- 轻松切换到Spring Boot（如需）
- 享受更清晰的代码结构
- 更好的可测试性

如有问题，请参考：
- [README.md](README.md) - 使用指南
- [MIGRATION.md](MIGRATION.md) - 完整迁移文档
- [BUILD.md](BUILD.md) - 构建说明
