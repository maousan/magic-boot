# Tiga Engine 集成里程碑

**日期**: 2026-03-28
**状态**: 已完成
**类型**: 模块集成

## 概述

将 tiga-platform 项目中的 `tiga-engine`（核心引擎）和 `tiga-engine-spring`（Spring Boot 集成）两个模块集成到 magic-boot 项目中，作为一级 Maven 模块管理。

## 集成内容

### 新增能力

| 能力 | 说明 |
|------|------|
| Groovy 脚本执行 | 与现有 MagicScript 并行的双引擎支持 |
| Calcite 内存 SQL 引擎 | tiga-engine 内部使用的 SQL 查询引擎 |
| 统一引擎管理器 | EngineManager API |
| WebSocket 调试支持 | Spring Boot 环境下的调试监听 |
| Prometheus 监控指标 | 引擎执行指标采集 |

### 新增依赖

| 依赖 | 版本 | 说明 |
|------|------|------|
| org.apache.groovy:groovy | 4.0.21 | 覆盖 Spring Boot BOM 默认值 |
| org.apache.groovy:groovy-json | 4.0.21 | Groovy JSON 处理 |
| org.bouncycastle:bcprov-jdk18on | 1.77 | 加密库 |
| com.alibaba.fastjson2:fastjson2 | 2.0.43 | JSON 处理 |

## 变更文件

| 文件 | 操作 |
|------|------|
| `tiga-engine/` | 从 tiga-platform/ 复制到根目录 |
| `tiga-engine-spring/` | 从 tiga-platform/ 复制到根目录 |
| `pom.xml` (根) | 添加 2 个 module 声明 |
| `magic-dependencies/pom.xml` | 添加版本属性 + 依赖管理 |
| `tiga-engine/pom.xml` | 替换 parent，添加 groupId/version |
| `tiga-engine-spring/pom.xml` | 替换 parent，移除硬编码版本 |
| `tiga-engine-spring/.../SpringEngineAutoConfiguration.java` | 添加 ConditionalOnProperty |
| `magic-boot-master/pom.xml` | 添加 tiga-engine-spring 依赖 |
| `magic-boot-master/src/main/resources/application.yml` | 添加 tiga.engine 配置 |

## 版本兼容性

| 依赖 | magic-boot | tiga-platform | 处理方式 |
|------|-----------|---------------|---------|
| Java | 17 | 17 | 一致 |
| Spring Boot | 3.1.2 | 3.2.0 | 降级使用 3.1.2，API 兼容 |
| magic-script | 1.9.0 (fork) | 1.9.0 | 使用 fork 版 |
| Calcite | 1.36.0 | 1.36.0 | 一致 |
| Hutool | 5.8.35 | 5.8.25 | 使用 5.8.35 |
| Groovy | (无) | 4.0.21 | 新增 |
| FastJSON2 | (无) | 2.0.43 | 新增 |
| BouncyCastle | (无) | 1.77 | 新增 |

## 验证结果

- **编译验证**: `mvn clean compile -DskipTests` 通过
- **全量构建**: 所有模块编译成功

## 配置项

```yaml
tiga:
  engine:
    enabled: true           # 引擎开关（默认 true）
    core-pool-size: 4       # 核心线程数
    max-pool-size: 8        # 最大线程数
    queue-capacity: 2000    # 队列容量
    max-cache-size: 500     # 最大缓存数
```

## 后续 TODO

- [ ] 启动验证：检查日志中 tiga engine Bean 注册
- [ ] 功能验证：检查 `engineManager` Bean 是否存在
- [ ] 配置关闭验证：设置 `tiga.engine.enabled=false` 确认不加载
- [ ] 决定是否清理 `tiga-platform/` 剩余内容
