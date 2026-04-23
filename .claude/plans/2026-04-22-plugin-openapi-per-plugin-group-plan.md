# 插件 OpenAPI 按插件分组计划（2026-04-22）

## 目标
- 将 PF4J 插件接口文档从单一分组调整为“每个插件一个分组”。

## 范围
- 修改主工程 OpenAPI 分组注册逻辑。
- 保持现有插件接口路径不变（`/plugin/{pluginId}/api/**`）。

## 方案
1. 启动时扫描已启动插件，为每个插件注册 `GroupedOpenApi`。
2. 监听插件 `STARTED` 事件，运行时新增插件分组。
3. 分组命名采用 `plugin-{pluginId}`，路径匹配 `{apiPrefixTemplate}/**`。

## 验证
- 编译通过：`mvn -pl magic-boot-master -am -DskipTests package`
- Swagger UI 下拉可见多个插件分组。
