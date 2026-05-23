# 2026-05-06 RFID 控制接口开发计划

## 目标
- 新增 magic-api 接口 `POST /api/rfid`。
- 按 Apifox OpenAPI 定义实现 RFID 控制：
  - `mode`: 必填，`on` 表示寻卡，`off` 表示停止寻卡。
  - `deviceId`: 后端默认使用客户端 IP，不从请求体传入；本机回环调用时通过配置或本机 LAN IP 兜底。
  - `data.locationId`、`data.sku`: 当 `mode=on` 时必填。
- 返回结构保持 `{ code, message, data }`，`data` 固定为 `null`。

## OpenAPI 摘要
- 文档地址：https://api.apifox.com/temp-links/api/452916973?t=f030f46b-2865-43cb-b946-9fd97614027a
- Path：`/api/rfid`
- Method：`POST`
- Body：
```json
{
  "mode": "on",
  "data": {
    "locationId": "A001",
    "sku": "SKU-10001"
  }
}
```

## 实现方案
- 文件：`data/dongxinheping/api/东信和平/RFID控制.ms`
- 使用顶级分组 `东信和平`，接口 path 为 `/rfid`，最终路径为 `/api/rfid`。
- 通过 RFID 插件定向控制：
  - 默认地址：`http://127.0.0.1:8090/plugin/zintis-rfid-plugin/api/rfid/command`
  - 可配置：`forest.variables.zintisRfidCommandUrl`
  - 本机调试 deviceId 可配置：`forest.variables.zintisRfidDefaultDeviceId`
- 转发体：
```json
{
  "deviceId": "客户端IP",
  "command": "on",
  "params": {
    "locationId": "A001",
    "sku": "SKU-10001"
  }
}
```

## 验证
- 校验 `.ms` 头部 JSON 与分隔线。
- 静态校验 path/method、客户端 IP/回环兜底作为 deviceId、mode 校验、on 模式必填校验、插件指令 URL、Forest POST、ApiResponse 返回结构。
- 新增 HTTP 用例：`http/test-dongxinheping-rfid-control.http`。
