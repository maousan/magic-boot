# Zintis RFID WebSocket 路径调整变更日志

## 变更时间

2026-04-25

## 变更背景

RFID WebSocket 服务此前为了兼容根路径连接，使用 `/` 作为握手路径。讨论后决定采用更清晰的业务入口路径，统一设备连接地址。

## 变更内容

- 将 RFID Netty WebSocket 握手路径从 `/` 调整为 `/ws`。
- 保留 `checkStartsWith=true`，支持 `/ws?deviceId=RFID-00` 这类带查询参数的握手请求。

## 使用方式

设备或调试工具连接地址调整为：

```text
ws://127.0.0.1:9090/ws?deviceId=RFID-00
```

## 验证方式

- 执行 `mvn -DskipTests package` 构建 RFID 插件模块。
- 替换 `plugins/magic-plugin-zintis-rfid.jar` 后重启应用或 reload 插件。
- 使用 WebSocket 客户端连接 `/ws?deviceId=RFID-00`，观察设备注册日志。
