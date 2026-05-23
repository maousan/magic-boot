---
doc_type: feature-ff-note
feature: led-color-crud
date: 2026-05-22
requirement: req-dxhp-admin-panel
tags: [led-color, crud, dongxinheping-admin]
---

## 做了什么
为巷道灯管理模块新增 t_led_color 颜色与控制指令对应关系的 CRUD 管理页面，支持分页查询（按颜色代码模糊搜索、按颜色精确筛选）、新增、编辑、删除操作。

## 改了哪些
- `data/dongxinheping/api/东信和平/库位/LED颜色分页查询.ms` — 新增 GET 分页查询 API
- `data/dongxinheping/api/东信和平/库位/新增LED颜色.ms` — 新增 POST 创建 API
- `data/dongxinheping/api/东信和平/库位/更新LED颜色.ms` — 新增 PUT 更新 API
- `data/dongxinheping/api/东信和平/库位/删除LED颜色.ms` — 新增 DELETE 删除 API
- `dongxinheping-admin/src/types/index.ts` — 新增 LedColor 接口定义
- `dongxinheping-admin/src/api/led-color.ts` — 新增前端 API 层（4 个函数）
- `dongxinheping-admin/src/views/LedColor.vue` — 新增 CRUD 管理页面
- `dongxinheping-admin/src/router/index.ts` — 注册 /led-color 路由
- `dongxinheping-admin/src/layouts/AdminLayout.vue` — 巷道灯管理菜单下新增"颜色管理"项

## 怎么验证的
代码模式完全匹配项目现有 CRUD 页面（LedDevice.vue / LedMapping.vue）和后端 .ms 文件（LED设备系列 API），待用户编译后浏览器验证。

## 顺手发现（可选，不阻塞）
- 无
