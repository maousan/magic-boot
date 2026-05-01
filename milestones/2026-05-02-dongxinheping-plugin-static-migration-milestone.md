# 东信和平静态页面插件迁移里程碑

## 里程碑

完成 `magic-plugin-dongxinheping` 插件骨架与 PDA 静态页面迁移，使东信和平相关 PDA 页面可随 PF4J 插件独立打包发布。

## 交付物

- 插件模块：`magic-plugin-dongxinheping`
- 插件入口：`DongxinhepingPlugin`
- 前端扩展：`DongxinhepingFrontendExtension`
- 静态页面：`magic-plugin-dongxinheping/src/main/resources/static/pda`
- 依赖资源：`magic-plugin-dongxinheping/src/main/resources/static/vendor`
- 已迁出主应用目录：`magic-boot-master/src/main/resources/static/pda`、`magic-boot-master/src/main/resources/static/vendor`
- 计划文档：`.claude/plans/2026-05-02-dongxinheping-plugin-static-migration-plan.md`
- 变更日志：`.claude/changelogs/2026-05-02-dongxinheping-plugin-static-migration-changelog.md`

## 验收方式

1. 构建插件模块无编译错误。
2. 插件资源目录包含原 PDA 页面、模板文件和 vendor 依赖。
3. 插件启动后可通过 `/plugin/dongxinheping-plugin/static/pda/index.html` 访问 PDA 首页。
