# 2026-04-24 东信和平 DZJH004 HTTP 调用函数里程碑

## 里程碑目标
完成接口文档 5.1.4 的 magic-api 函数适配，并抽象通用 WMS 接口签名工具。

## 已交付
- `data/dongxinheping/function/工具函数/按库位查询库存返回.ms`
- `data/dongxinheping/function/工具函数/生成WMS接口签名.ms`

## 能力结果
- 主函数对外仅暴露 `EDIGROUPID`、`warehouseId`、`locationId`。
- 签名逻辑可被其他 WMS 三方接口复用。
- 返回 HTTP 状态码、请求业务报文与原始响应文本，方便联调排查。

## 后续建议
- 在 `application-dongxinheping.yml` 中补齐 `dzjh004*` 配置后进行真实联调。
