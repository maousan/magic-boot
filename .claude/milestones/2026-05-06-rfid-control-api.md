# 2026-05-06 里程碑：RFID 控制接口

## 已完成
- 新增 `POST /api/rfid`。
- 实现 `mode`、`data.locationId`、`data.sku` 校验。
- `deviceId` 默认取客户端 IP，本机回环调用时支持配置或本机 LAN IP 兜底，并转发给 RFID 插件。
- 接入 RFID 插件定向指令控制。
- 补充 HTTP 测试用例和静态验证脚本。

## 验证方式
- `.claude/tmp/verify-rfid-control-api.ps1` 静态校验 `.ms` 元数据、核心脚本片段和 HTTP 用例。

## 待联调
- 启动后端和 RFID 插件后，执行 `http/test-dongxinheping-rfid-control.http` 验证实际设备或插件响应。
