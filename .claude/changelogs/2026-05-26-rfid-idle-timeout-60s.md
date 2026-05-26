# RFID TCP 读空闲超时调整变更日志

## 变更时间

2026-05-26

## 变更范围

`magic-plugin-zintis-rfid`

## 变更内容

1. RFID TCP 服务默认读空闲超时时间由 30 秒调整为 60 秒。
2. 仍支持通过 `rfid.socket.idleTimeoutSeconds` 配置覆盖。

## 影响

客户端心跳间隔可以放宽，但仍建议低于 60 秒，例如 20-30 秒发送一次 `ping`。
