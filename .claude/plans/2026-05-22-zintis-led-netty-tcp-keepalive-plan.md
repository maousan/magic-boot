# Zintis LED Netty TCP KeepAlive 参数计划

## 目标

在不恢复 read idle 主动关闭连接的前提下，尝试通过 TCP keepalive 探测半开连接，帮助服务端更及时发现设备断网或异常断开。

## 步骤

1. 为 Netty NIO 子连接启用平台 TCP keepalive 参数。
   - 验证：插件编译通过。
2. 保留原有 read idle 行为，不在读空闲时主动关闭连接。
   - 验证：原有 read idle 测试语义不变。
3. 补充变更日志说明平台兼容性和限制。
   - 验证：文档记录清晰。

## 说明

JDK 扩展 TCP keepalive 参数依赖操作系统支持。不支持时保持 `SO_KEEPALIVE=true`，不影响服务启动。
