# Zintis RFID TCP Socket 改造计划

## 背景

`magic-plugin-zintis-rfid` 原实现使用独立 Netty WebSocket 服务接入终端。当前业务场景为 Android 工业手持终端 App 与服务端保持长连接，开放接口层接收命令后按 `deviceId` 分发给对应终端连接。接口文档已约定 `deviceId` 字段不可变，且其值当前约定为终端 IP。

## 目标

1. 将终端通信从 WebSocket 改为常规 TCP Socket。
2. 保持开放接口字段 `deviceId` 不变。
3. 连接建立后使用客户端 remote IP 作为 `deviceId` 注册连接。
4. 保留现有 JSON 消息模型、ACK 处理、离线命令缓存和设备列表接口。

## 实施步骤

1. 替换 Netty 服务端 pipeline。
   - verify: 搜索确认不再使用 `WebSocketServerProtocolHandler` 和 `TextWebSocketFrame`。
2. 调整服务端 handler。
   - verify: 连接建立时自动注册 remote IP，收到 JSON 字符串后继续调用 `MessageService`。
3. 调整配置类和配置前缀。
   - verify: 使用 `rfid.socket` 前缀，定时缓存配置兼容旧 `rfid.websocket.batchIntervalMs`。
4. 保持 REST 接口请求字段 `deviceId` 不变。
   - verify: `/rfid/command` 请求体仍读取 `deviceId`。

## 协议约定

TCP 消息格式：

```text
4字节大端长度 + UTF-8 JSON内容
```

对外接口示例：

```json
{
  "deviceId": "192.168.1.23",
  "command": "scan",
  "params": {}
}
```

`deviceId` 当前业务含义为终端 IP。
