# 2026-04-09 ScriptExecutor SPI + Tiga 接管计划

## 目标

在 `magic-api-parent` 引入可插拔脚本执行 SPI（`ScriptExecutor`），由 `magic-boot-master` 注入 `TigaScriptExecutor`，让 HTTP 请求主链路与函数链路统一走 `EngineManager.execute("magic", ...)`。

## 关键步骤

1. 在 `magic-api` 新增 `ScriptExecutor` 接口及默认实现 `DefaultScriptExecutor`。
2. 在 `MagicConfiguration` 增加全局 `scriptExecutor` 配置入口。
3. 将 `RequestHandler`、`RequestMagicDynamicRegistry`、`FunctionMagicDynamicRegistry`、`DefaultMagicAPIService` 的脚本执行改为 `MagicConfiguration.getScriptExecutor().executeScript(...)`。
4. 在 `magic-api-spring-boot-starter` 提供 `ScriptExecutor` 默认 Bean（可被上层覆盖）。
5. 在 `magic-boot-master` 注册 `@Primary ScriptExecutor`（`TigaScriptExecutor`），删除旧旁路接管拦截器。

## 验证

- `mvn -pl magic-boot-master -am -DskipTests compile`
- 启动日志检查 `activeScriptExecutor` 是否为 `org.ssssssss.magicboot.service.TigaScriptExecutor`
- 调用任意接口检查 `[TIGA-MAGIC] SPI执行` 日志

