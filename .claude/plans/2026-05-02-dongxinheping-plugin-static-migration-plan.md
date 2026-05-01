# 东信和平静态页面插件迁移计划

## 目标

新增 `magic-plugin-dongxinheping` PF4J 插件，将当前 `magic-boot-master` 中的 PDA 静态页面及依赖资源迁移到插件包内，使页面随插件 jar 发布。

## 边界

- 新增独立插件模块：`magic-plugin-dongxinheping`。
- 迁移资源范围：`static/pda` 页面、模板文件，以及这些页面依赖的 `static/vendor` React / ReactDOM / antd-mobile 文件。
- 保持页面内部相对路径不变，插件访问路径使用 `/plugin/dongxinheping-plugin/static/pda/index.html`。
- 不调整 Magic-API 接口、不修改页面业务逻辑、不引入新的前端构建链。

## 实施步骤

1. 复用现有 PF4J 插件结构创建新模块、`pom.xml`、`plugin.properties`。
2. 新增插件入口类，创建插件 Spring ApplicationContext，并继承主应用环境。
3. 新增 `FrontendExtension`，声明插件名称、菜单、路由和 iframe 外链地址。
4. 将 `pda` 与 `vendor` 静态资源复制到插件 `src/main/resources/static`。
5. 移除主应用中的原 `static/pda` 与 `static/vendor` 目录。
6. 新增插件默认入口 `console.js`，访问 `/plugin/dongxinheping-plugin` 时跳转到 PDA 首页。
7. 将根 `pom.xml` 注册新模块。
8. 补充变更日志与里程碑文档。
9. 做定向验证：文件存在性、资源引用、POM 结构与编译检查。

## 风险与验证

- 风险：插件未启动时新路径不可访问；需要部署插件 jar 后启用。
- 风险：旧 `/pda/*.html` 路径迁移后不再由主应用静态目录提供。
- 验证：检查插件资源完整性、相对资源路径是否仍能解析、运行 `mvn -pl magic-plugin-dongxinheping -am -DskipTests compile`。
