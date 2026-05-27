---
doc_type: dev-guide
slug: rfid-terminal-tcp-integration
component: magic-plugin-zintis-rfid
status: current
summary: Android RFID 手持终端接入 Magic Boot RFID TCP 服务的协议与接口说明。
tags: [rfid, tcp, android, integration]
last_reviewed: 2026-05-27
---

# RFID 手持终端 TCP 接入技术文档

## 概述

Magic Boot 通过 `magic-plugin-zintis-rfid` 插件提供 RFID 终端接入服务。Android 手持终端作为 TCP 客户端主动连接服务端，保持长连接，上报 RFID 数据并接收服务端下发的业务指令。

服务端通过终端连接的远端 IP 识别终端。对外 HTTP 接口字段仍使用 `deviceId`，当前约定 `deviceId` 的值为终端 IP。

## 前置依赖

| 项目 | 说明 |
| --- | --- |
| 终端类型 | Android 工业手持终端 |
| 通信方式 | TCP 长连接 |
| 默认端口 | `9090` |
| 字符编码 | UTF-8 |
| 消息边界 | 4 字节大端长度 + UTF-8 内容 |
| 心跳超时 | 服务端默认 60 秒读空闲超时 |
| 建议心跳 | 每 20-30 秒发送一次 `ping` |

服务端端口可由平台侧配置：

```yaml
rfid:
  socket:
    port: 9090
    idleTimeoutSeconds: 60
```

## 快速上手

终端连接服务端：

```text
TCP: {server_host}:9090
```

终端连接成功后不需要额外注册包。服务端会使用 TCP 连接的远端 IP 注册终端：

```text
deviceId = 终端 IP
```

例如服务端看到终端 IP 为 `192.168.2.180`，则开放接口下发命令时使用：

```json
{
  "deviceId": "192.168.2.180",
  "command": "scan",
  "params": {}
}
```

## TCP 帧格式

所有客户端发给服务端、服务端发给客户端的消息都使用同一种帧格式：

```text
4字节大端长度 + UTF-8内容
```

长度字段表示后续 UTF-8 内容的字节数，不包含长度字段本身。

示例：发送纯文本 `ping`

```text
内容: ping
内容字节: 70 69 6E 67
内容长度: 4
长度头: 00 00 00 04
完整帧: 00 00 00 04 70 69 6E 67
```

服务端回复 `pong`：

```text
完整帧: 00 00 00 04 70 6F 6E 67
```

## Android 发送与接收示例

发送一帧：

```java
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public static void sendFrame(OutputStream outputStream, String text) throws Exception {
    byte[] body = text.getBytes(StandardCharsets.UTF_8);
    ByteBuffer buffer = ByteBuffer.allocate(4 + body.length);
    buffer.putInt(body.length); // ByteBuffer 默认大端
    buffer.put(body);
    outputStream.write(buffer.array());
    outputStream.flush();
}
```

读取一帧：

```java
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public static String readFrame(InputStream inputStream) throws Exception {
    byte[] lenBytes = readExact(inputStream, 4);
    int len = ByteBuffer.wrap(lenBytes).getInt();
    byte[] body = readExact(inputStream, len);
    return new String(body, StandardCharsets.UTF_8);
}

private static byte[] readExact(InputStream inputStream, int len) throws Exception {
    byte[] buffer = new byte[len];
    int offset = 0;
    while (offset < len) {
        int read = inputStream.read(buffer, offset, len - offset);
        if (read < 0) {
            throw new java.io.EOFException("socket closed");
        }
        offset += read;
    }
    return buffer;
}
```

## 心跳协议

终端应定时发送纯文本 `ping` 帧：

```text
00 00 00 04 70 69 6E 67
```

服务端收到后回复纯文本 `pong` 帧：

```text
00 00 00 04 70 6F 6E 67
```

建议终端每 20-30 秒发送一次 `ping`。如果服务端 60 秒内没有收到该连接的任何数据，会主动关闭连接。

心跳发送示例：

```java
sendFrame(outputStream, "ping");
String reply = readFrame(inputStream);
if (!"pong".equalsIgnoreCase(reply.trim())) {
    // 业务方可记录异常或触发重连
}
```

## 终端上报 RFID 数据

RFID 数据使用 JSON 文本作为帧内容。示例：

```json
{
  "type": "data",
  "msgId": "rfid-20260527-0001",
  "seq": 1,
  "payload": {
    "epc": "E2000017221101441890ABCD",
    "rssi": -50,
    "timestamp": "2026-05-27T10:20:30"
  }
}
```

服务端收到后会返回 ACK：

```json
{
  "msgId": "rfid-20260527-0001",
  "seq": 2,
  "type": "ack",
  "payload": null
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `type` | 是 | 消息类型。RFID 数据上报固定为 `data` |
| `msgId` | 建议 | 消息唯一 ID，用于 ACK 与去重 |
| `seq` | 建议 | 客户端递增序号 |
| `payload.epc` | 是 | RFID 标签 EPC |
| `payload.rssi` | 否 | 信号强度 |
| `payload.timestamp` | 否 | 终端采集时间，建议 ISO-8601 格式 |

## 服务端下发命令

平台侧调用 HTTP 接口后，服务端会把命令通过 TCP 长连接下发给对应终端。

终端收到的命令帧内容示例：

```json
{
  "msgId": "7e3a6c4c-1c2a-4e83-9e58-0c16c8b3f02a",
  "seq": 3,
  "type": "data",
  "payload": {
    "command": "scan",
    "params": {}
  }
}
```

终端执行后建议回复 ACK：

```json
{
  "type": "ack",
  "msgId": "7e3a6c4c-1c2a-4e83-9e58-0c16c8b3f02a",
  "seq": 4
}
```

当前服务端会接收 ACK 并移除内部待确认记录。

## 平台 HTTP 接口

插件接口完整路径带插件前缀：

```text
/plugin/zintis-rfid-plugin/api/rfid
```

### 查询服务状态

```http
GET /plugin/zintis-rfid-plugin/api/rfid/status
```

响应示例：

```json
{
  "running": true,
  "port": 9090,
  "activeConnections": 1
}
```

### 查询在线终端

```http
GET /plugin/zintis-rfid-plugin/api/rfid/devices
```

响应示例：

```json
{
  "running": true,
  "devices": [
    {
      "deviceId": "192.168.2.180",
      "remoteAddress": "192.168.2.180:63985",
      "connectedAt": 1779840000000,
      "lastActiveAt": 1779840030000
    }
  ]
}
```

### 下发命令

```http
POST /plugin/zintis-rfid-plugin/api/rfid/command
Content-Type: application/json
```

请求示例：

```json
{
  "deviceId": "192.168.2.180",
  "command": "scan",
  "params": {}
}
```

在线时响应：

```json
{
  "success": true,
  "msgId": "7e3a6c4c-1c2a-4e83-9e58-0c16c8b3f02a",
  "status": "delivered",
  "message": "指令已送达设备"
}
```

离线时响应：

```json
{
  "success": true,
  "msgId": "7e3a6c4c-1c2a-4e83-9e58-0c16c8b3f02a",
  "status": "pending",
  "message": "设备离线，指令已缓存"
}
```

### 启动 / 停止 TCP 服务

```http
POST /plugin/zintis-rfid-plugin/api/rfid/start
POST /plugin/zintis-rfid-plugin/api/rfid/stop
```

`start` 可选传入端口：

```json
{
  "port": 9090
}
```

## 重连建议

终端应实现自动重连：

1. TCP 连接失败时，等待 3-5 秒后重试。
2. 读取服务端数据时遇到 EOF 或异常，关闭旧 socket 并重连。
3. 连续多次未收到 `pong`，可主动断开并重连。
4. 重连后服务端会使用新的连接替换同 IP 的旧连接。

## 验收步骤

1. 终端连接 `服务端IP:9090`。
2. 终端发送 `ping` 帧，确认收到 `pong`。
3. 调用在线终端接口，确认 `devices` 中出现终端 IP。
4. 终端发送一条 RFID `data` 消息，确认收到 ACK。
5. 平台调用下发命令接口，确认终端收到命令 JSON。
6. 断开终端连接后再次查询在线终端，确认连接数下降。

## 已知限制与注意事项

1. `deviceId` 当前使用服务端看到的终端 IP。若终端经过 NAT、代理或多台终端共用同一出口 IP，会造成识别冲突。
2. 一台 IP 只保留一个在线连接。同 IP 新连接建立后，旧连接会被关闭。
3. 所有 TCP 消息都必须带 4 字节大端长度头，不能直接发送裸 JSON 或裸字符串。
4. 心跳 `ping/pong` 是纯文本内容，不是 JSON。
5. 服务端默认读空闲超时为 60 秒，终端心跳间隔必须小于该值。

## 相关文档

- `.claude/changelogs/2026-05-26-zintis-rfid-tcp-socket-change.md`
- `.claude/changelogs/2026-05-26-rfid-tcp-ping-pong.md`
- `.claude/changelogs/2026-05-26-rfid-idle-timeout-60s.md`
