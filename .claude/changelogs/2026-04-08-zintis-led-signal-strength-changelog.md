# 变更日志 - 2026-04-08 - 查询信号强度

## 变更内容
- 新增 DTO：`LedSignalStrengthResponse`。
- 新增服务能力：`LedControlService#querySignalStrength`。
- 新增控制器接口：`POST /led/system/signal-strength`。
- 新增 RSSI 解析逻辑：支持文本和单字节两种解析策略。
- 新增 HTTP 测试样例：`http/test-magic-plugin-zintis-led.http`。
- 增加服务测试用例：
  - 正常解析 RSSI。
  - payload 不含 RSSI 时返回 `SIGNAL_NOT_FOUND`。

## 兼容性说明
- 不影响现有开/关/点动/查询/TCP 控制接口。
- 查询信号强度接口为新增能力，调用方可按需接入。
