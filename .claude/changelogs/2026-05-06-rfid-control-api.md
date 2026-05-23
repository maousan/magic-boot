# 2026-05-06 变更日志：RFID 控制接口

## 背景
- 根据 Apifox OpenAPI 文档新增 RFID 控制接口。
- 接口定义：`POST /api/rfid`，`mode=on` 表示寻卡，`mode=off` 表示停止寻卡。
- 本地实现调整：`deviceId` 默认使用客户端 IP，不再由调用方传入；当本机调用拿到回环地址时，优先使用 `zintisRfidDefaultDeviceId` 配置，否则尝试解析本机 LAN IP。

## 变更内容
1. `RFID控制.ms`
- 新增 magic-api 接口 `/api/rfid`。
- 校验 `mode` 必填且仅支持 `on/off`。
- 使用 `request.getClientIP()` 作为转发给 RFID 插件的 `deviceId`，并处理 `127.0.0.1`、`::1`、`0:0:0:0:0:0:0:1` 等回环地址。
- `mode=on` 时校验 `data.locationId`、`data.sku` 必填。
- 转发到 RFID 插件定向指令接口，默认地址为 `http://127.0.0.1:8090/plugin/zintis-rfid-plugin/api/rfid/command`。
- 返回结构为 `{ code, message, data }`。

2. HTTP 用例
- 更新 `http/test-dongxinheping-rfid-control.http`，用例不再传入 `deviceId`。

## 影响范围
- 新增接口，不影响现有 RFID 插件 Controller。
- 插件地址可通过 `forest.variables.zintisRfidCommandUrl` 覆盖。
