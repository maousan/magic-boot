# 2026-04-29 东信和平库存查询函数 GET 调用变更日志

## 变更内容
- 简化函数：`data/dongxinheping/function/工具函数/按库位查询库存返回.ms`
- 去除 WMS 签名逻辑，不再调用 `/utils/buildWmsInterfaceSign`。
- 去除 `POST application/x-www-form-urlencoded` 请求体，改为 `GET` query 调用三方接口。
- query 参数保留：
  - `method`
  - `warehouseId`
  - `locationId`
- `locationId` 支持批量传入，多个库位用逗号分隔，最多 50 组；函数内部会拆分、去重后重新以逗号拼接传给三方接口。
- 函数入参去除 `EDIGROUPID`，测试接口调用方式调整为位置参数：`queryInventoryByLocations(warehouseId, locationId)`。
- 测试接口由 `POST` 请求体改为 `GET` query 参数，参数为 `warehouseId`、`locationId`。

## 保留逻辑
- 保留 `warehouseId` 必填校验。
- 保留 `locationId` 必填校验。
- 保留库位去重、中文逗号兼容与最多 50 个库位限制。

## 兼容性
- 未修改测试接口路径。
- 未修改其他 WMS 或库存相关函数。
