---
doc_type: feature-ff-note
feature: user-light-color-admin-crud
date: 2026-05-22
requirement:
tags: [dongxinheping, user-light-color, admin, crud]
---

## 做了什么
为 dongxinheping-admin 新增"用户灯色映射"管理页面，支持 t_user_light_color 表的 CRUD 操作。管理员可配置用户ID与灯光颜色（RED/GREEN/BLUE/YELLOW/CYAN）的映射关系，控制启用/停用状态。

## 改了哪些
- `data/dongxinheping/api/东信和平/用户灯色映射/group.json` — 新建分组，path: /user-light-color
- `data/dongxinheping/api/东信和平/用户灯色映射/列表查询.ms` — GET 分页查询
- `data/dongxinheping/api/东信和平/用户灯色映射/新增.ms` — POST 新增（含颜色校验和唯一性检查）
- `data/dongxinheping/api/东信和平/用户灯色映射/修改.ms` — POST 动态字段更新（含 user_id 唯一性校验）
- `data/dongxinheping/api/东信和平/用户灯色映射/删除.ms` — POST 删除
- `dongxinheping-admin/src/types/index.ts` — 新增 UserLightColor 接口
- `dongxinheping-admin/src/api/user-light-color.ts` — 新增 API 层（4 个函数）
- `dongxinheping-admin/src/views/UserLightColor.vue` — 新增管理页面
- `dongxinheping-admin/src/router/index.ts` — 注册 /user-light-color 路由
- `dongxinheping-admin/src/layouts/AdminLayout.vue` — 侧边栏新增"用户灯色映射"菜单
