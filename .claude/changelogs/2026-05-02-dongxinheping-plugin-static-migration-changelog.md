# 东信和平静态页面插件迁移变更日志

## 变更内容

- 新增 PF4J 插件模块 `magic-plugin-dongxinheping`。
- 将 PDA 静态页面迁移到插件资源目录：
  - `magic-plugin-dongxinheping/src/main/resources/static/pda`
  - `magic-plugin-dongxinheping/src/main/resources/static/vendor`
- 移除主应用中的原静态页面目录：
  - `magic-boot-master/src/main/resources/static/pda`
  - `magic-boot-master/src/main/resources/static/vendor`
- 新增插件前端扩展声明，提供菜单、路由和 iframe 外链地址。
- 新增插件默认入口 `console.js`，访问 `/plugin/dongxinheping-plugin` 时跳转到 PDA 首页。
- 根 `pom.xml` 注册新插件模块。

## 访问路径

- 插件默认入口：`/plugin/dongxinheping-plugin`
- PDA 首页：`/plugin/dongxinheping-plugin/static/pda/index.html`

## 注意事项

- 插件 jar 需要被 PF4J 加载并启动后，插件静态资源路径才可访问。
- 原 `/pda/*.html` 静态访问路径不再由主应用直接提供。
- 页面调用的业务 API 仍沿用现有 `/api/location/...` 路径。
