# Tiga Engine 集成变更日志

**日期**: 2026-03-28
**类型**: 模块集成

## 变更说明

将 `tiga-platform` 中的 `tiga-engine` 和 `tiga-engine-spring` 模块集成到 magic-boot 项目作为一级 Maven 模块。

## 新增

- `tiga-engine/` 模块 — 核心脚本引擎（MagicScript + Groovy 双引擎 + Calcite SQL）
- `tiga-engine-spring/` 模块 — Spring Boot 自动配置集成
- `magic-dependencies` BOM 中新增 Groovy 4.0.21、FastJSON2 2.0.43、BouncyCastle 1.77 版本管理
- `magic-boot-master` 添加 tiga-engine-spring 运行时依赖
- `application.yml` 新增 `tiga.engine.*` 配置项
- `SpringEngineAutoConfiguration` 添加 `@ConditionalOnProperty` 条件注解（支持配置开关）

## 修改

- `pom.xml`（根） — modules 列表新增 `tiga-engine`、`tiga-engine-spring`
- `tiga-engine/pom.xml` — parent 切换为 `magic-boot`，添加 groupId `com.ocean.tiga`，版本跟随 `${revision}`
- `tiga-engine-spring/pom.xml` — parent 切换为 `magic-boot`，移除 Spring Boot 依赖硬编码版本

## 修复

- 移除 `PluginAdminController.java` 的 UTF-8 BOM 标记（遗留问题）
