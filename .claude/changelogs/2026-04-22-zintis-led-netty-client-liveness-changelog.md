# 2026-04-22 zintis-led netty client liveness changelog

## 变更内容
- `LedNettyServerService`：在 Netty pipeline 增加 `IdleStateHandler`，默认读空闲超时为 120 秒。
- `LedNettyServerHandler`：新增 `userEventTriggered`，收到 `READER_IDLE` 后主动关闭连接。
- `LedNettyServerHandlerTest`：新增超时事件单测，验证空闲事件触发时 channel 会被关闭。

## 影响
- 活跃客户端列表会更及时剔除无响应连接。
- 不改动现有发送协议与 payload 结构。
