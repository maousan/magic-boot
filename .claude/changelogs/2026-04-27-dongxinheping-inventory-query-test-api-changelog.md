# 2026-04-27 东信和平库存查询函数测试接口变更日志

## 新增内容
- 新增测试接口：`POST /api/location/inventory/query-by-location-function-test`
- 文件：`data/dongxinheping/api/东信和平/库位/测试按库位查询库存返回函数.ms`
- 能力：接收 `warehouseId`、`locationId`，调用函数 `/utils/queryInventoryByLocations` 查询库存返回。

## 测试样例
- 新增 HTTP 文件：`http/test-dongxinheping-inventory-query.http`
- 覆盖：
  - 正常传入仓库和多个库位
  - 缺少 `warehouseId`
  - 缺少 `locationId`

## 兼容性
- 未修改原库存查询函数。
- 未修改现有库位、亮灯、拣货接口协议。
