# 2026-03-30 Workspace Menu Category 变更日志

## 变更背景
- 执行方案文档：`docs/superpowers/specs/2026-03-30-workspace-menu-architecture-design.md`
- 目标：将 workspace 归属从前端路径前缀判断，迁移为后端菜单数据字段 `category`。

## 变更内容
1. 数据库迁移
- 新增 Flyway 脚本：`magic-boot-master/src/main/resources/db/migration/V20260330.001__add_sys_menu_category.sql`
- 变更点：
  - 给 `sys_menu` 增加 `category` 字段（`VARCHAR(32)`）。
  - 先尝试继承父菜单 `category`。
  - 对无分类菜单按路径规则回填（`system/business/app-center/settings`）。

2. 菜单接口脚本
- 更新 `data/magic-api/api/系统管理/菜单管理/查询登录用户菜单.ms`
  - 查询字段增加 `sm.category`。
  - 增加路径推断兜底，确保返回菜单节点包含 `category`。
  - 树构建时子菜单缺失分类会继承父节点分类。
- 更新 `data/magic-api/api/系统管理/菜单管理/获取菜单树.ms`
  - 查询字段增加 `sm.category`。
  - 增加路径推断兜底，确保树节点包含 `category`。
- 更新 `data/magic-api/api/系统管理/菜单管理/保存菜单.ms`
  - 保存时支持接收 `category`。
  - 若未传 `category`，优先继承父菜单分类，再按路径规则兜底。

3. 接口测试用例
- 新增：`http/test-menu-category-workspace.http`
  - 覆盖 `current/menus`、`menu/tree`、`menu/save` 三个接口验证点。

## 兼容性说明
- 旧菜单数据未设置 `category` 时，迁移脚本会自动回填，兼容历史数据。
- 旧保存请求不传 `category` 时仍可成功，后端会自动推断。

## 风险与回滚
- 风险：路径与业务分类不一致时，自动回填可能与预期不同，需要管理员在菜单管理中修正。
- 回滚：
  1. 回滚应用版本；
  2. 若需结构回退，可手工执行 `ALTER TABLE sys_menu DROP COLUMN category;`（仅在确认无数据依赖时执行）。
