# magic-plugin-zintis-led 查询信号强度功能实施计划

## 目标
- 新增查询 LED 设备信号强度能力。
- 提供独立 API，返回结构化 `signalStrengthDbm`。

## 实施范围
- 新增 DTO：`LedSignalStrengthResponse`。
- 新增服务方法：`LedControlService#querySignalStrength`。
- 新增接口：`POST /led/system/signal-strength`。
- 新增/更新测试与 HTTP 示例。

## 协议策略
- 复用查询控制指令：`CMD_QUERY(0xAB)`。
- 使用请求中的 `dataCommand` 查询目标信息。
- 从响应 payload 中解析 RSSI：
  - 优先解析文本模式（如 `RSSI:-67dBm`、`-67 dBm`）。
  - 回退解析单字节有符号数值。

## 验证
- `mvn -pl magic-plugin-zintis-led package -DskipTests`
- 复制 jar 至 `plugins` 目录并验证文件时间戳。
