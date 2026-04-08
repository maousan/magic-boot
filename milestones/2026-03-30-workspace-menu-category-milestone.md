# 2026-03-30 Workspace Menu Category 里程碑

## 里程碑目标
完成 workspace 菜单归属后端化（`sys_menu.category`），为后续“按权限动态显示 workspace”提供稳定数据基础。

## 已完成项
1. 数据层
- `sys_menu` 新增 `category` 字段。
- 历史菜单完成分类回填策略（父级继承 + 路径兜底）。

2. 接口层
- `POST /system/menu/current/menus` 已返回 `category`。
- `GET /system/menu/tree` 已返回 `category`。
- `POST /system/menu/save` 已支持持久化 `category`，并含兜底策略。

3. 交付物
- Flyway 迁移脚本：`V20260330.001__add_sys_menu_category.sql`
- Magic-API 脚本更新：菜单管理 3 个接口
- 测试用例：`http/test-menu-category-workspace.http`
- 变更日志：`milestones/2026-03-30-workspace-menu-category-changelog.md`

## 验收标准
- 新老菜单数据均可读取到 `category`。
- 菜单新增/编辑流程不会因 `category` 缺失导致保存失败。
- 菜单相关接口返回结构向后兼容（新增字段，不破坏原字段）。

## 下一阶段建议
- 前端 workspace store 改造为基于 `category` 分组与可见性计算。
- 菜单管理 UI 增加 `category` 下拉字段，支持人工修正自动推断结果。
