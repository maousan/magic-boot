# 2026-05-06 变更日志：WMS 库存接口定时同步

## 背景
- `定时同步库存接口数据.ms` 原先为占位脚本，尚未实现库存接口同步。
- 需要按 `t_location_warehouse.warehouse_code` 分组分页查询库位，调用 DZJH004 库存查询函数，并写入已存在的 `t_inventory`。

## 变更内容
1. `定时同步库存接口数据.ms`
- 新增仓库分组查询与库位分页查询，每页 50 个库位。
- 仓库编码查询使用 `group by warehouse_code`，不使用 `distinct`。
- 调用 `@/utils/queryInventoryByLocations` 获取库存明细。
- 将返回字段映射到 `t_inventory`：
  - `warehouseId -> warehouse_id`
  - `locationId -> location_id`
  - `lotAtt09 -> lot_att09`
  - `qtyAllocated -> qty_allocated`
  - `qtyPa -> qty_pa`
  - `userDefine1..5 -> user_define1..5`
- 使用 `insert ... on duplicate key update` 写入库存。
- 返回同步统计：仓库数、库位数、调用页数、写入数、跳过数、失败页明细。

2. 验证脚本
- 新增 `.claude/tmp/verify-wms-inventory-sync-job.ps1` 做 `.ms` 结构和关键逻辑静态校验。

## 影响范围
- 仅影响 WMS 库存同步定时任务。
- 不改变 `按库位查询库存返回.ms` 的远端调用协议。
- 不创建或修改 `t_inventory` 表结构。
- 不启用该定时任务，仍保持元数据中的 `enabled: false`。
