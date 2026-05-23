# 2026-05-06 WMS 库存接口定时同步计划

## 目标
- 完善 `data/dongxinheping/job/wms/定时同步库存接口数据.ms`。
- 按 `warehouse_code` 分组分页读取 `t_location_warehouse`。
- 每页调用 `@/utils/queryInventoryByLocations` 获取库存明细。
- 将返回明细写入 `t_inventory`。

## 实现要点
1. 分组分页
- 使用 `group by warehouse_code` 查询 `t_location_warehouse` 中非空仓库编码列表。
- 对每个仓库按 `location_id, id` 排序分页读取库位。
- 每页最多 50 个库位，匹配 `queryInventoryByLocations` 的限制。

2. 远端调用
- 调用 `queryInventoryByLocations(warehouseCode, locationIds)`。
- 兼容返回 `items` 为数组或单对象。
- 远端失败时记录错误并继续处理下一个分页。

3. 写入库存
- 不创建 `t_inventory`，使用用户已建好的表。
- 校验 `warehouseId/locationId/sku` 必填。
- `qty/qtyAllocated/qtyPa` 为空时按 0 写入。
- `editTime` 使用 `str_to_date(..., '%Y-%m-%d %H:%i:%s')` 转换。
- 使用 `insert ... on duplicate key update` 同步库存字段。

## 验证
- 校验 `.ms` 头部 JSON 可解析且分隔线存在。
- 校验脚本包含分组分页、函数调用、批量 upsert、字段映射和错误统计逻辑。
- 不主动编译，由用户按项目约定自行编译测试。
