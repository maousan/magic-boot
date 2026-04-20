# 2026-04-09 ScriptExecutor SPI + Tiga 接管变更日志

## 变更摘要

- 在 `magic-api-parent` 新增脚本执行 SPI：`ScriptExecutor`，并提供默认实现 `DefaultScriptExecutor`。
- `RequestHandler` / `RequestMagicDynamicRegistry` / `FunctionMagicDynamicRegistry` / `DefaultMagicAPIService` 改为通过 `MagicConfiguration.getScriptExecutor()` 执行脚本。
- `magic-api-spring-boot-starter` 新增 `ScriptExecutor` 默认自动装配。
- `magic-boot-master` 新增 `TigaScriptExecutor` 并以 `@Primary` 注入，作为 SPI 覆盖实现。
- 移除旧的旁路接管拦截器与自定义 `TigaMagicAPIService`，统一收敛到 SPI 架构。

## 影响范围

- 框架层：`magic-api-parent`（执行抽象）
- 应用层：`magic-boot-master`（Tiga 实现注入）
- 对外 API 路由与响应协议不变

