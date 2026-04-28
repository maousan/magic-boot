# Zintis RFID Netty WebSocket 插件设计

## 概述

在 magic-boot 平台中开发 PF4J 插件 `magic-plugin-zintis-rfid`，基于 Netty 实现 WebSocket 服务端，与 Android RFID 设备进行双向实时通讯。支持设备上报 RFID 标签数据（Redis 缓存 + MySQL 异步落库），以及外部系统通过 HTTP API 下发控制指令。

## 技术选型

- **通讯框架**：Netty（WebSocket 协议）
- **插件框架**：PF4J + PF4J-Spring
- **缓存**：Redis
- **持久化**：MySQL（MyBatis Plus）
- **Java 版本**：17
- **Spring Boot**：3.1.2

## 插件结构

```
magic-plugin-zintis-rfid/
├── pom.xml
├── src/main/java/org/ssssssss/magicboot/zintis/rfid/
│   ├── ZintisRfidPlugin.java                # PF4J 插件入口类
│   ├── config/
│   │   └── RfidWebSocketProperties.java     # 配置属性（端口、Redis 开关等）
│   ├── server/
│   │   ├── NettyWebSocketServer.java        # Netty 服务启动/停止，Pipeline 组装
│   │   └── WebSocketServerHandler.java      # WebSocket 消息收发，空闲检测，连接生命周期
│   ├── model/
│   │   ├── WsMessage.java                   # 消息协议模型（msgId/seq/type/payload）
│   │   └── DeviceInfo.java                  # 设备信息（deviceId、连接时间、最后活跃时间）
│   ├── service/
│   │   ├── MessageService.java              # ACK 追踪、去重、seq 管理、消息分发
│   │   ├── DeviceManager.java               # 设备注册/注销、重复连接处理、离线指令缓存
│   │   └── DataPersistenceService.java      # Redis 写入 + 定时批量落库 MySQL
│   └── controller/
│       └── RfidApiController.java           # HTTP API（指令下发、广播、设备列表、服务状态）
├── src/main/resources/
│   └── plugin.properties
```

## 依赖

- `io.netty:netty-all`（provided，主应用已有）
- `org.ssssssss:magic-plugin-api`（provided）
- `org.pf4j:pf4j` / `pf4j-spring`（provided）
- `org.springframework.boot:spring-boot-starter-web`（provided）
- `org.springframework.boot:spring-boot-starter-data-redis`
- `com.baomidou:mybatis-plus-spring-boot3-starter`（provided）

## Netty WebSocket 服务端

### Pipeline

```
HttpServerCodec
→ HttpObjectAggregator(65536)
→ IdleStateHandler(30, 0, 0)                    // 30s 无读活动触发空闲事件
→ WebSocketServerProtocolHandler("/ws", null, true, 65536)
→ WebSocketServerHandler                         // 自定义业务处理
```

### 服务配置

- 默认端口：9090（可通过 `rfid.websocket.port` 配置修改）
- Android 客户端连接地址：`ws://ip:9090/ws?deviceId=RFID-001`
- 最大连接数：20
- 最大帧大小：64KB
- WebSocket 子协议：null（不使用）

### 服务生命周期

- `@PostConstruct`：自动启动 Netty 服务
- `@PreDestroy`：先向所有设备发送 `{ type: "disconnect" }`，等待短暂时间后关闭所有 Channel，释放 Netty 线程组

### WebSocketServerHandler

- `channelRead0`：解析 JSON 消息，按 type 分发处理
- `userEventTriggered`：处理 `IdleStateEvent`，关闭空闲超时的连接
- `channelActive`：提取 deviceId（从 URL query 参数），注册到 DeviceManager
- `channelInactive`：从 DeviceManager 注销，触发离线指令缓存

## 消息协议

### 格式（与 Android 客户端对齐）

```json
{
  "msgId": "uuid-string",
  "seq": 1,
  "type": "data | ack | disconnect | error",
  "payload": { ... }
}
```

### 接收处理

| 收到的 type | 服务端行为 |
|---|---|
| `data` | 发送 ACK 回设备 → msgId 去重检查 → payload 写入 Redis → 异步落库 MySQL |
| `ack` | 匹配待确认队列中的 msgId，标记投递成功，移除重试定时器 |
| `disconnect` | 清理设备 Channel，触发离线流程 |
| 格式错误/未知 type | 回复 `{ type: "error", payload: { code, message } }` |

### 发送处理（指令下发）

- 构造 `{ msgId, seq, type: "data", payload: {...} }`
- 加入待确认队列，设置 5s 超时重试（最多 3 次）
- 收到设备 ACK 后移除队列，全部失败则标记投递失败

### seq 管理

- 服务端 seq 持久化到 Redis（key: `ws:seq:{deviceId}`）
- 使用 `AtomicLong` 保证线程安全
- 重启时从 Redis 恢复

### 去重

- 服务端维护 msgId LRU 缓存（100 条），重复消息回复 ACK 但不重复处理

### 消息处理顺序

- 同一设备的消息在 Netty EventLoop 内完成 ACK 回复和 Redis 写入
- 仅 MySQL 落库交给异步线程池

## 设备管理

### DeviceManager

- `ConcurrentHashMap<String, Channel>` 管理设备连接（key: deviceId）
- 新连接注册时，若 deviceId 已存在则关闭旧 Channel 再注册新 Channel
- 设备 ID 从 WebSocket 握手的 URL query 参数 `deviceId` 提取

### 离线指令缓存

- 设备离线时，下发给该设备的指令存入 Redis 队列（key: `cmd:pending:{deviceId}`）
- 设备重连后，DeviceManager 触发补发离线指令

## 数据流

### 设备上报 RFID

```
Android 发送 { type:"data", payload: { epc, rssi, timestamp } }
  → WebSocketServerHandler 接收
  → MessageService 发送 ACK + 去重
  → Redis Sorted Set（key: rfid:pending:{deviceId}，score: timestamp）
  → 定时任务（5s 或 100 条，取先到者）批量读取 Redis 并写入 MySQL
```

### RFID 数据 MySQL 表

```sql
CREATE TABLE rfid_record (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 自增主键
  device_id   VARCHAR(64) NOT NULL,               -- 采集设备标识，如 "RFID-001"
  epc         VARCHAR(128) NOT NULL,               -- RFID 标签唯一标识符（Electronic Product Code）
  rssi        INT,                                 -- 信号强度（dBm），值越大信号越强，用于判断标签距离
  read_time   DATETIME NOT NULL,                   -- 标签被读取的实际时间，由 Android 设备采集时记录
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 记录入库时间，用于排查数据延迟
  INDEX idx_device_time (device_id, read_time)     -- 按设备+时间范围查询
);
```

### 服务端下发指令

```
外部系统调用 HTTP POST /api/rfid/command
  → RfidApiController 接收
  → DeviceManager 查找设备 Channel
  → 设备在线 → 立即发送 + ACK 追踪
  → 设备离线 → 写入 Redis 队列（key: cmd:pending:{deviceId}）
  → 设备重连 → 自动补发
```

## HTTP API

### 指令下发

```
POST /api/rfid/command
请求：{ "deviceId": "RFID-001", "command": "start_scan", "params": { "interval": 1000 } }
响应：{ "success": true, "msgId": "uuid", "status": "delivered", "message": "指令已送达设备" }
```

status 取值：
- `delivered` — 设备在线，指令已送达并收到 ACK
- `pending` — 设备离线，指令已缓存，待上线补发
- `timeout` — 设备在线但投递超时（ACK 重试 3 次失败）

### 广播指令

```
POST /api/rfid/broadcast
请求：{ "command": "stop_scan", "params": {} }
响应：{ "success": true, "totalTargets": 3, "successCount": 3, "failedCount": 0, "failedTargets": [] }
```

### 设备列表

```
GET /api/rfid/devices
响应：{ "running": true, "devices": [{ "deviceId": "RFID-001", "remoteAddress": "192.168.1.50:54321", "connectedAt": "2026-04-25T10:00:00", "lastActiveAt": "2026-04-25T10:05:30" }] }
```

### 服务状态

```
GET /api/rfid/status
响应：{ "running": true, "port": 9090, "activeConnections": 3 }
```

### 支持的下发指令

| command | 说明 | params 示例 |
|---------|------|-------------|
| `start_scan` | 开始扫描标签 | `{ "interval": 1000, "power": 30 }` |
| `stop_scan` | 停止扫描 | `{}` |
| `set_power` | 设置射频功率 | `{ "power": 30 }` |
| `get_status` | 获取设备状态 | `{}` |
| `reboot` | 重启设备 | `{}` |
