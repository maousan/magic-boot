# 2026-04-22 zintis-led netty client liveness check plan

## 目标
- 为 Netty 已连接客户端增加存活校验能力，避免长时间无数据的僵尸连接长期占用 active clients 列表。

## 方案
1. 在 Netty Server pipeline 增加 `IdleStateHandler`，启用读空闲检测（默认 120 秒）。
2. 在 `LedNettyServerHandler` 处理 `READER_IDLE` 事件：记录日志并主动关闭连接。
3. 连接关闭后依赖 `channelInactive` 自动从 `activeChannels/activeConnections` 移除。
4. 补充单元测试验证空闲事件触发后 channel 会被关闭。

## 风险与回滚
- 风险：若设备上报间隔天然超过 120 秒，可能被误判为超时断开。
- 回滚：移除 `IdleStateHandler` 与 `userEventTriggered` 中的超时关闭逻辑即可恢复原行为。

## 验证
- 运行 `LedNettyServerHandlerTest` 和 `LedNettyServerServiceTest`，确认测试通过。
