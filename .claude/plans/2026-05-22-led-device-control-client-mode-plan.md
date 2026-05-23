# 巷道灯设备控制新增Client Mode计划

## 目标

为 `data/dongxinheping/api/东信和平/库位/控制巷道灯设备开关.ms` 增加 `mode` 参数：

- `server`：保持当前通过设备 IP 调用 `/control/on|off` 的逻辑。
- `client`：根据 `ledId`（MAC 地址）匹配 LED Netty 活跃客户端，再通过 `/led/sendToClient` 向该客户端发送完整协议帧。

## 实施步骤

1. 增加 `mode` 参数解析，默认 `server`。
   - 验证：未传 `mode` 时执行原业务逻辑。
2. 增加 client mode 的活跃客户端匹配。
   - 验证：调用 LED 插件 `/netty/server/clients`，按 `clientDetails.macAddress` 匹配标准化后的 `ledId`。
3. 增加 client mode 的协议帧构造与发送。
   - 验证：按 `[hostAddress, controlCommand, dataCommand]` 计算 CRC16 Modbus high-low，调用 `@/led/sendToClient`。
4. 保持原状态更新逻辑。
   - 验证：发送全部成功后仍更新 `t_location_led.status`。

## 成功标准

- `mode=server` 与原逻辑一致。
- `mode=client` 能通过 MAC 找到活跃客户端并下发 ON/OFF 指令。
- 找不到客户端、客户端列表接口异常、发送失败时返回明确错误。
