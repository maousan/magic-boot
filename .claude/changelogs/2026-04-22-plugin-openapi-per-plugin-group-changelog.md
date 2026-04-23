# 变更日志：插件 OpenAPI 按插件分组（2026-04-22）

## 变更内容
- 调整 `PluginOpenApiConfiguration`：
  - 移除单一 `pf4j-plugin-api` 分组。
  - 新增按插件动态注册分组能力（`plugin-{pluginId}`）。
  - 启动时注册已启动插件分组，并监听插件启动事件增量注册。

## 影响
- Swagger UI 的接口定义下拉框会按插件展示分组。
- 每个分组仅包含对应插件路径 `/plugin/{pluginId}/api/**`。

## 兼容性
- 不改变插件实际接口路径。
- 仅变更文档分组行为。
