# 2026-04-24 东信和平 DZJH004 HTTP 调用函数开发计划

## 目标
- 依据接口文档 5.1.4，新增一个 magic-api 函数调用 HTTP 接口。
- 对外业务入参仅包含：`EDIGROUPID`、`warehouseId`、`locationId`。
- 将签名算法抽象成独立工具函数，供当前及后续接口复用。

## 范围
- 新增函数：`data/dongxinheping/function/工具函数/按库位查询库存返回.ms`
- 新增工具函数：`data/dongxinheping/function/工具函数/生成WMS接口签名.ms`
- 不新增本地 HTTP API 路由。
- 不查询本地库存表。

## 实现
- 主函数只声明三个业务参数。
- 三方地址与公共调用参数从 `forest.variables` 读取。
- 签名工具函数负责 `MD5 + Base64 + 大写 + URL编码`。
- 主函数使用 `Forest.post` 发送 `application/x-www-form-urlencoded` 请求。

## 验证
- 校验 `.ms` 头部 JSON 可解析。
- 校验脚本分隔线 `================================` 存在。
- 静态确认签名逻辑已从主函数抽离。
