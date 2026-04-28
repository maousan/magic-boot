# 2026-04-27 东信和平库存查询函数测试接口计划

## 目标
- 新增一个测试接口，用于调用函数 `/utils/queryInventoryByLocations`，快速验证按仓库和库位查询库存的 WMS HTTP 链路。

## 变更范围
- 新增 API 脚本：`data/dongxinheping/api/东信和平/库位/测试按库位查询库存返回函数.ms`
- 新增 HTTP 测试样例：`http/test-dongxinheping-inventory-query.http`

## 约束
- 复用现有 `东信和平/库位` API 分组，最终路径为 `/api/location/inventory/query-by-location-function-test`。
- 测试接口只做请求体必填校验，并将参数透传给函数，不改变原函数签名与业务逻辑。

## 验证方式
- 静态检查 `.ms` 元数据、接口路径、函数 import 路径和参数透传逻辑。
- 通过 HTTP 样例联调验证成功、缺少 `warehouseId`、缺少 `locationId` 三类场景。
