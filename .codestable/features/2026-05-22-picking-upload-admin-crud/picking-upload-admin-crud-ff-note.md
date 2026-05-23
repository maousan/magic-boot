---
doc_type: feature-ff-note
feature: picking-upload-admin-crud
date: 2026-05-22
requirement:
tags: [dongxinheping, picking-upload, admin, crud, master-detail]
---

## 做了什么
为 dongxinheping-admin 前端新增"拣货单管理"页面，支持 t_picking_upload 主表 CRUD 和 t_picking_upload_detail 明细表增删改。后端基于 magic-api 新建"拣货管理"分组，提供 8 个 .ms 接口。

## 改了哪些
- `data/dongxinheping/api/东信和平/拣货管理/group.json` — 新建分组，path: /picking-upload
- `data/dongxinheping/api/东信和平/拣货管理/列表查询.ms` — GET 分页查询主表
- `data/dongxinheping/api/东信和平/拣货管理/查看详情.ms` — GET 查主表+明细
- `data/dongxinheping/api/东信和平/拣货管理/新增单据.ms` — POST 新增主表
- `data/dongxinheping/api/东信和平/拣货管理/修改单据.ms` — POST 修改主表
- `data/dongxinheping/api/东信和平/拣货管理/删除单据.ms` — POST 级联删除主表+明细
- `data/dongxinheping/api/东信和平/拣货管理/新增明细.ms` — POST 新增明细并更新 detail_count
- `data/dongxinheping/api/东信和平/拣货管理/修改明细.ms` — POST 修改明细
- `data/dongxinheping/api/东信和平/拣货管理/删除明细.ms` — POST 删除明细并更新 detail_count
- `dongxinheping-admin/src/types/index.ts` — 新增 PickingUpload、PickingUploadDetail 接口
- `dongxinheping-admin/src/api/picking-upload.ts` — 新增 API 层（9 个函数）
- `dongxinheping-admin/src/views/PickingUpload.vue` — 新增主从管理页面
- `dongxinheping-admin/src/router/index.ts` — 注册 /picking-upload 路由
- `dongxinheping-admin/src/layouts/AdminLayout.vue` — 侧边栏新增"拣货管理"菜单

## 怎么验证的
TypeScript 类型检查通过（vue-tsc --noEmit，无新增错误）。需启动后端服务和前端 dev server 在浏览器中验证完整 CRUD 流程。

## 顺手发现（可选，不阻塞）
- `dongxinheping-admin/src/views/LedDevice.vue:310` — 预存 TS 类型错误，不在本次范围
