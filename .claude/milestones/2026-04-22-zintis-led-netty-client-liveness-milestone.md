# 2026-04-22 zintis-led netty client liveness milestone

## 里程碑
- 完成 Netty 客户端存活校验能力（读空闲超时自动断链）。
- 保持现有 TCP 协议和业务接口不变。
- 通过单元测试验证关键行为，支持后续在生产中观察并按需调整超时阈值。
