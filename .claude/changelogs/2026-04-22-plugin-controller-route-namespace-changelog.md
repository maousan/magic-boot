# 2026-04-22 plugin-controller-route-namespace-changelog

## 变更内容
- `PluginProperties` 新增 `apiPrefixTemplate`，默认值 `/plugin/{pluginId}/api`。
- `PluginControllerRegistrar` 动态注册插件 Controller 时默认加插件命名空间前缀，避免路径冲突。
- `PluginControllerRegistrar` 注册映射时保留请求方法与请求条件（params/headers/consumes/produces）。
- 更新 `test-magic-plugin-zintis-led.http`，接口访问路径改为 `{{baseUrl}}{{pluginApiPrefix}}/led/...`。

## 影响
- 插件业务接口默认不再裸露在根路径（例如 `/led/...`），改为插件隔离路径（例如 `/plugin/zintis-led-plugin/api/led/...`）。
- 插件间接口路径可并存，降低冲突风险。
