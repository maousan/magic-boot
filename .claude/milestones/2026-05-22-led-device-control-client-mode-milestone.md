# 巷道灯设备控制Client Mode里程碑

## 完成内容

- `控制巷道灯设备开关.ms` 支持 `server/client` 两种控制模式。
- client mode 支持按 MAC 匹配 Netty 活跃客户端。
- client mode 支持构造带 CRC 的完整 LED 协议帧，并通过 `/led/sendToClient` 下发。

## 行为说明

- 默认 `mode=server`，保持原业务逻辑。
- `mode=client` 找不到对应活跃客户端时返回 404。
