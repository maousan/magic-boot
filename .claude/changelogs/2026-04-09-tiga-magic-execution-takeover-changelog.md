# 2026-04-09 Tiga Magic 执行链路替换变更日志

## 变更摘要

- 在 `magic-boot-master` 新增 `TigaMagicAPIService`，将脚本执行由 `ScriptManager.executeScript(...)` 切换为 `EngineManager.execute("magic", ...)`。
- 新增 `TigaMagicApiConfiguration`，在 `tiga.engine.enabled=true` 且 `EngineManager` 存在时以 `@Primary` 覆盖默认 `MagicAPIService`。
- 新增模块桥接初始化器，启动后将 `MagicResourceLoader` 模块同步注册到 Tiga 全局模块池。
- 新增配置 `tiga.magic.timeout-ms`，默认 `30000` 毫秒。

## 影响范围

- 仅 `magic-boot-master` 模块。
- 不修改 `magic-api-parent`、`magic-script`、`tiga-engine`。
- 对外 API 路由与响应结构保持不变。

