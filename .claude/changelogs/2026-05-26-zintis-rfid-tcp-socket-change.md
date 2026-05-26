# Zintis RFID TCP Socket 通信改造变更日志

## 变更时间

2026-05-26

## 变更范围

`magic-plugin-zintis-rfid`

## 变更内容

1. 终端通信由 Netty WebSocket 服务改为 Netty TCP Socket 服务。
2. TCP 消息边界改为 `4字节长度头 + UTF-8 JSON内容`。
3. 开放接口字段 `deviceId` 保持不变，当前业务约定其值为终端 IP。
4. TCP 连接建立后，服务端使用客户端 remote IP 注册为 `deviceId -> Channel`。
5. 同一 IP 重连时关闭旧连接，并避免旧连接关闭事件误删新连接映射。
6. 配置前缀由 `rfid.websocket` 调整为 `rfid.socket`，批量落库定时配置兼容旧的 `rfid.websocket.batchIntervalMs`。

## 兼容性说明

- `/rfid/command`、`/rfid/broadcast`、`/rfid/devices`、`/rfid/status` 接口路径不变。
- `/rfid/command` 请求体仍使用 `deviceId` 字段。
- Android 终端 App 需要按 TCP 长连接协议发送带 4 字节长度头的 UTF-8 JSON。

## 风险点

- 如果终端经过 NAT 或代理，服务端看到的 remote IP 可能不是终端真实 IP。
- 如果多个终端共享同一个服务端可见 IP，会发生连接互斥，后连接会替换先连接。
