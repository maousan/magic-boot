# Zintis RFID TCP Socket 改造里程碑

## 目标

完成 `magic-plugin-zintis-rfid` 从 WebSocket 长连接到 TCP Socket 长连接的传输层改造，同时保持开放接口 `deviceId` 契约不变。

## 已完成

1. 新增 `NettyTcpServer`，使用 Netty TCP 服务监听终端连接。
2. 新增 `TcpServerHandler`，连接建立时按 remote IP 注册 `deviceId`。
3. 保留现有 JSON 消息处理、ACK、离线命令缓存和 RFID 数据缓存落库逻辑。
4. REST 接口层继续按 `deviceId` 下发命令。
5. 插件名称和描述调整为 TCP Socket 通信。

## 验收口径

1. 终端 App 可以建立 TCP 长连接。
2. 终端发送长度头 JSON 后，服务端能进入现有 `MessageService` 处理流程。
3. 开放接口传入 `deviceId = 终端IP` 后，服务端能找到对应 Channel 并下发命令。
4. 同 IP 重连时，服务端保留新连接并关闭旧连接。

## 未执行项

本次未主动编译或启动服务，按项目约定由用户自行编译测试。
