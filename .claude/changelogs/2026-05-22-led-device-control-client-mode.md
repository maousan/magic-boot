# 巷道灯设备控制新增Client Mode

## 变更内容

- `控制巷道灯设备开关.ms` 新增 `mode` 请求参数。
- `mode=server` 时保持原有逻辑，继续按设备 IP 调用 LED 插件 `/control/on|off`。
- `mode=client` 时，通过 LED 插件 `/netty/server/clients` 获取活跃 TCP 客户端列表，按 `ledId`/`macAddress` 匹配客户端。
- client mode 会按当前颜色命令构造完整协议帧 `[hostAddress, controlCommand, dataCommand, crcHi, crcLo]`，再调用 `@/led/sendToClient` 下发。

## 参数

- `mode`：`server` 或 `client`，默认 `server`。
- `waitResponse`：client mode 下透传给 `sendToClient`，默认不等待。

## 兼容性

- 未传 `mode` 的原调用保持 server mode 行为。
