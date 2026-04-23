# 2026-04-23 库位码触发巷道灯亮灯函数变更日志

## 变更内容
- 新增函数：`/turnOnAisleLedByLotNo`（Magic Function 路径）。
- 入参：`data.lotNo`（必填）、`data.timeoutMs`（可选，默认3000）。
- 处理逻辑：
  - 解析库位码（兼容二维码 `Lot` 字段）。
  - 查询 `t_location_led` 获取 `ledId` 与 `color`。
  - 查询 `t_led_device` 获取设备 `ip`。
  - 查询 `t_led_color` 获取颜色命令 `code` 并转换为 `dataCommand`。
  - 调用插件接口 `POST /plugin/zintis-led-plugin/api/control/on`，端口固定 `9527`。

## 文件变更
- 新增：`data/dongxinheping/function/aims/控制灯/根据库位码亮巷道灯.ms`

## 兼容性说明
- 对现有 API 无破坏性改动。
- `t_led_color.code` 同时支持十进制、`0x`十六进制、纯十六进制字符串。
