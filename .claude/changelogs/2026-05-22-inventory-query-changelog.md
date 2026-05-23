# 库存查询功能 - 变更日志

**日期**: 2026-05-22
**类型**: 新增功能

## 变更内容

### 新增文件

| 文件 | 说明 |
|------|------|
| `data/dongxinheping/api/东信和平/库位/库存列表查询.ms` | 后端 GET 接口 `/api/location/inventory/list`，分页查询 `t_inventory` 表 |
| `dongxinheping-admin/src/api/inventory.ts` | 前端 API 调用层 |
| `dongxinheping-admin/src/views/InventoryQuery.vue` | Admin 后台库存查询页面 |

### 修改文件

| 文件 | 变更 |
|------|------|
| `dongxinheping-admin/src/types/index.ts` | 新增 `InventoryItem` 接口 |
| `dongxinheping-admin/src/router/index.ts` | 新增 `/inventory-query` 路由 |
| `dongxinheping-admin/src/layouts/AdminLayout.vue` | 新增「库存查询」菜单项 |

### 构建产物（自动生成）

`dongxinheping-admin` `npm run build` 输出到 `magic-plugin-dongxinheping/src/main/resources/static/admin/`

## 接口说明

**GET** `/api/location/inventory/list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页条数，默认 20，最大 100 |
| warehouseId | string | 否 | 仓库ID（精确匹配） |
| locationId | string | 否 | 库位码（模糊匹配） |
| sku | string | 否 | SKU编码（模糊匹配） |
| lotAtt09 | string | 否 | 批次属性（精确匹配） |

返回值：`{code: 200, message: "success", data: {list: [...], total: N}}`
