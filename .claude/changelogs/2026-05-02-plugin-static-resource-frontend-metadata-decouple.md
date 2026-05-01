# 插件静态资源前端元数据解耦变更日志

## 背景

访问 `/plugin/dongxinheping-plugin/static/pda/index.html` 时，日志提示：

```text
静态资源请求失败：插件 [dongxinheping-plugin] 没有前端扩展
```

排查确认插件 jar 已放入 `plugins` 目录并可被 PF4J rescan 到运行时，但直接静态资源访问被 `PluginStaticResourceController` 的前端扩展元数据校验拦截。

## 变更内容

- 调整 `PluginStaticResourceController`：
  - 静态资源访问只校验插件是否存在、请求路径是否合法、资源是否存在。
  - 不再要求插件必须注册 `FrontendExtension` 元数据。
- 更新 `PluginStaticResourceControllerTest`：
  - 将“插件没有前端扩展时返回 404”的旧预期改为“插件存在且资源存在时返回静态资源”。

## 验证

- `mvn -pl magic-plugin -Dtest=PluginStaticResourceControllerTest test`
- 结果：22 个测试通过，0 failures，0 errors。
