# 库存查询功能计划

**日期**: 2026-05-22
**状态**: 实施中

## 需求

在 Admin 后台新增库存查询页面，查询本地 `t_inventory` 表，支持全字段筛选。

## 数据源

`t_inventory` 表（WMS 定时同步写入），字段：

| 字段 | 说明 |
|------|------|
| warehouse_id | 仓库ID |
| location_id | 库位码 |
| sku | SKU编码 |
| lot_att09 | 批次属性 |
| qty | 库存数量 |
| qty_allocated | 已分配数量 |
| qty_pa | 在途数量 |
| update_time | 最后更新时间 |

## 实施清单

### 1. 后端 .ms 接口

**文件**: `data/dongxinheping/api/东信和平/库位/库存列表查询.ms`
- GET `/inventory/list`（完整路径 `/api/location/inventory/list`）
- 参数: page, pageSize, warehouseId, locationId, sku, lotAtt09
- 模糊查询: locationId、sku 使用 LIKE
- 精确查询: warehouseId、lotAtt09
- 返回: `{code, message, data: {list, total}}`

### 2. 前端 API 层

**文件**: `dongxinheping-admin/src/api/inventory.ts`
- `getInventoryList(params)` → GET `/location/inventory/list`

### 3. 前端类型

**文件**: `dongxinheping-admin/src/types/index.ts`
- 新增 `InventoryItem` 接口

### 4. 前端页面

**文件**: `dongxinheping-admin/src/views/InventoryQuery.vue`
- Naive UI DataTable + 搜索表单
- 筛选字段: 仓库ID、库位码、SKU、批次属性
- 分页

### 5. 路由 & 菜单

- `dongxinheping-admin/src/router/index.ts` → 添加 `inventory-query` 路由
- `dongxinheping-admin/src/layouts/AdminLayout.vue` → 添加菜单项「库存查询」

### 6. 构建部署

- 在 `dongxinheping-admin/` 执行 `npm run build` 输出到插件 `static/admin/`
- 按 AGENTS.md 规则，用户自行编译测试
