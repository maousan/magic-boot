# 应用层迁移执行指南

**重要提示：** 本指南将帮助您将现有应用迁移到新的模块化架构。请在执行前仔细阅读完整内容。

---

## ⚠️ 迁移前准备

### 1. 备份项目

```bash
# 创建Git备份分支
git checkout -b backup-before-migration
git add .
git commit -m "Backup before migration to modular architecture"
git push origin backup-before-migration

# 或创建完整备份
cp -r . ../tiga-platform-backup-$(date +%Y%m%d)
```

### 2. 确认环境

- [ ] Java 17+
- [ ] Maven 3.6+
- [ ] Git已安装
- [ ] 有足够的磁盘空间

---

## 📋 迁移步骤

### 方案A：原地迁移（推荐）

适用于：当前项目已稳定，希望平滑迁移

#### Step 1: 更新父POM

**文件：** `pom.xml`

**修改前：**
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>3.8.0</version>
</parent>
```

**修改后：**
```xml
<parent>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-platform-parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

**同时添加：**
```xml
<dependencies>
    <!-- 替换原有的engine依赖 -->
    <dependency>
        <groupId>com.ocean.tiga</groupId>
        <artifactId>tiga-engine-solon</artifactId>
    </dependency>

    <!-- 保留其他依赖... -->
</dependencies>
```

#### Step 2: 更新LoadComponent.java

**文件：** `src/main/java/com/ocean/tigaapi/init/LoadComponent.java`

**修改前：**
```java
@Inject
private TigaEngineManager tigaEngineManager;

// 使用
tigaEngineManager.registerGlobalModule("db", dsManager.getAll());
```

**修改后：**
```java
import com.ocean.tiga.engine.api.EngineManager;

@Inject
private EngineManager engineManager;  // 改为接口类型

// 使用
engineManager.registerGlobalModule("db", dsManager.getAll());
```

#### Step 3: 更新其他引用

查找所有使用`TigaEngineManager`的地方：

```bash
# 查找需要修改的文件
grep -r "TigaEngineManager" src/ --include="*.java"
```

**批量替换：**
```bash
# Linux/Mac
sed -i 's/TigaEngineManager/EngineManager/g' $(grep -r "TigaEngineManager" src/ --include="*.java" -l)

# 或手动逐个修改
```

#### Step 4: 更新DebugWebSocket引用

**修改前：**
```java
import com.ocean.tigaapi.engine.controller.debug.DebugWebSocket;

DebugWebSocket.push(sid, "FINISHED", result);
```

**修改后：**
```java
import com.ocean.tiga.engine.api.DebugListener;

@Inject
private DebugListener debugListener;  // 由Solon集成模块自动注入

debugListener.onFinished(sid, result);
```

#### Step 5: 更新监控指标

**修改前：**
```java
import com.ocean.tigaapi.engine.magic.MagicMonitor;

return MagicMonitor.getPrometheusMetrics();
```

**修改后：**
```java
import com.ocean.tiga.engine.monitor.PrometheusMetricsCollector;

@Inject
private PrometheusMetricsCollector metricsCollector;

return metricsCollector.getMetricsReport();
```

---

### 方案B：子模块迁移（适合大型项目）

适用于：希望清晰分层，长期维护

#### Step 1: 创建子模块目录结构

```bash
# 创建应用子模块
mkdir -p tiga-platform-app/src

# 移动现有代码
mv src/* tiga-platform-app/src/
mv pom.xml tiga-platform-app/pom.xml

# 创建新的根POM
# (参考pom-parent.xml)
```

#### Step 2: 更新子模块POM

**文件：** `tiga-platform-app/pom.xml`

添加父POM引用：
```xml
<parent>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-platform-parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

#### Step 3: 更新父POM

在根目录的`pom.xml`中添加：
```xml
<modules>
    <module>tiga-engine</module>
    <module>tiga-engine-solon</module>
    <module>tiga-engine-spring</module>
    <module>tiga-platform-app</module>
</modules>
```

---

## 🔍 验证迁移

### 1. 编译检查

```bash
# 清理并编译
mvn clean compile

# 检查是否有编译错误
echo $?  # 应该输出0
```

### 2. 依赖检查

```bash
# 检查依赖树
mvn dependency:tree | grep tiga

# 应该看到：
# [INFO] +- com.ocean.tiga:tiga-engine-solon:jar:1.0.0
# [INFO] |  \- com.ocean.tiga:tiga-engine:jar:1.0.0
```

### 3. 功能测试

**创建测试脚本：** `test-migration.sh`

```bash
#!/bin/bash

echo "=== 测试脚本执行 ==="

# 1. 测试Magic脚本
curl -X POST http://localhost:8080/api/test-magic \
  -H "Content-Type: application/json" \
  -d '{"script": "return 1 + 1"}'

echo ""
echo "✅ Magic脚本测试完成"

# 2. 测试Groovy脚本
curl -X POST http://localhost:8080/api/test-groovy \
  -H "Content-Type: application/json" \
  -d '{"script": "return 1 + 1"}'

echo ""
echo "✅ Groovy脚本测试完成"

# 3. 测试数据库操作
curl -X POST http://localhost:8080/api/test-db \
  -H "Content-Type: application/json" \
  -d '{"sql": "SELECT 1"}'

echo ""
echo "✅ 数据库测试完成"

# 4. 测试监控指标
curl http://localhost:8080/metrics/prometheus | grep tiga

echo ""
echo "✅ 监控指标测试完成"

echo ""
echo "=== 所有测试完成 ==="
```

### 4. 性能对比

```bash
# 对比迁移前后的性能
# 1. 缓存命中率
curl http://localhost:8080/metrics/prometheus | grep cache_hits

# 2. 执行耗时
curl http://localhost:8080/metrics/prometheus | grep execution_duration
```

---

## 🚨 常见问题

### Q1: 编译错误 - 找不到EngineManager

**错误：**
```
cannot find symbol: class EngineManager
```

**解决：**
```xml
<!-- 确保添加了tiga-engine-solon依赖 -->
<dependency>
    <groupId>com.ocean.tiga</groupId>
    <artifactId>tiga-engine-solon</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Q2: 运行错误 - Bean注入失败

**错误：**
```
No qualifying bean of type 'EngineManager'
```

**解决：**
1. 检查`tiga-engine-solon`依赖是否正确
2. 确保Solon能扫描到配置类
3. 检查`@Inject`注解是否正确

### Q3: DbModule无法使用

**错误：**
```
脚本中无法使用db变量
```

**解决：**
```java
// 确保在启动时注册了DbModule
@Inject
public void init(EngineManager engineManager, DynamicDatasourceService dsManager) {
    engineManager.registerGlobalModule("db", dsManager.getAll());
}
```

### Q4: WebSocket调试失败

**错误：**
```
WebSocket连接失败
```

**解决：**
WebSocket端点已自动注册到`/debug-ws`，检查：
1. 前端连接地址是否正确
2. Solon WebSocket依赖是否完整
3. 防火墙设置

---

## ✅ 迁移检查清单

### 编译阶段

- [ ] 父POM已更新
- [ ] 依赖已替换为tiga-engine-solon
- [ ] TigaEngineManager → EngineManager替换完成
- [ ] DebugWebSocket.push → debugListener.onXxx替换完成
- [ ] MagicMonitor → metricsCollector替换完成
- [ ] 所有import语句已更新
- [ ] 编译无错误

### 运行阶段

- [ ] 应用启动成功
- [ ] EngineManager Bean注入成功
- [ ] DbModule注册成功
- [ ] 脚本执行正常（Magic和Groovy）
- [ ] 数据库操作正常
- [ ] 调试功能正常（WebSocket）
- [ ] 监控指标正常（/metrics/prometheus）

### 性能阶段

- [ ] 缓存命中率 > 80%
- [ ] 执行耗时无明显下降
- [ ] 内存使用正常
- [ ] 无内存泄漏

---

## 📝 回滚方案

如果迁移出现问题，可以快速回滚：

```bash
# 方案1：Git回滚
git checkout backup-before-migration
git checkout -b rollback-migration
git push origin rollback-migration

# 方案2：文件恢复
rm -rf src pom.xml
cp -r ../tiga-platform-backup-*/ .

# 重新编译
mvn clean install
```

---

## 🎯 下一步

迁移完成后：

1. **测试** - 运行完整的测试套件
2. **监控** - 观察生产环境性能
3. **文档** - 更新项目文档
4. **清理** - 删除旧的engine包（确认无问题后）

```bash
# 确认无问题后，删除旧engine代码
rm -rf src/main/java/com/ocean/tigaapi/engine

# 提交迁移
git add .
git commit -m "Migrate to modular architecture"
git push origin master
```

---

## 📞 获取帮助

如果遇到问题：

1. 查看详细文档：
   - `MIGRATION.md` - 完整迁移指南
   - `APPLICATION_MIGRATION.md` - 应用层迁移详解
   - `PROJECT_STATUS.md` - 项目状态

2. 检查常见问题
3. 联系技术支持

---

**祝您迁移顺利！** 🎉
