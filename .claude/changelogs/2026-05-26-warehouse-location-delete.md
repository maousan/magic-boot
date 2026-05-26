# 库位列表删除操作

## 背景

库位列表操作列需要支持删除单条仓库库位记录。

## 变更

- 新增 magic-api 接口 `DELETE /location/warehouse-location/delete`，按 `id` 删除 `t_location_warehouse` 记录。
- 前端库位 API 增加 `deleteWarehouseLocation(id)`。
- 库位列表操作列新增“删除”按钮，点击后弹出确认框，确认后删除并刷新列表。

## 影响范围

- 仅影响库位列表单条记录删除能力。
- 同步库存、更新标签、导入、手动添加逻辑未调整。
