# Zintis LED Netty TCP KeepAlive 参数调整

## 背景

设备断开网络后，服务端内存活跃连接可能长时间不更新。当前 read idle 策略为了兼容设备只上报一次，不主动关闭连接，因此需要尝试使用 TCP keepalive 辅助探测半开连接。

## 变更

- 保留 `SO_KEEPALIVE=true`。
- 为 Netty NIO 子连接配置 JDK 扩展 TCP keepalive 参数：
  - `TCP_KEEPIDLE=60`
  - `TCP_KEEPINTERVAL=10`
  - `TCP_KEEPCOUNT=3`
- 参数通过运行时检测配置，不支持的平台会跳过扩展参数并保持服务启动。

## 影响

- 不改变 read idle 行为，读空闲仍不主动关闭连接。
- 半开连接检测依赖操作系统 TCP keepalive 实现，不能替代业务心跳。
