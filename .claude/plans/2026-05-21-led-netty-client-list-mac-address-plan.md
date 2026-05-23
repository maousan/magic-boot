# LED Netty客户端列表新增MAC地址计划

## 目标

让 `LedNettyServerService.listClients()` 返回当前连接客户端的 MAC 地址，便于调用方按设备识别连接。

## 实施步骤

1. 在 Netty handler 中缓存客户端 MAC。
   - 验证：收到报文并解析到 MAC 后，按远端地址保存；连接断开、异常、重置时清理。
2. 扩展客户端列表响应 DTO。
   - 验证：保留原 `clients` 字段，新增 `clientDetails.remoteAddress/macAddress`。
3. 更新 `listClients()` 返回值。
   - 验证：`totalClients` 仍基于活跃连接数，`clientDetails` 与活跃连接对应。

## 成功标准

- 原有 `clients` 字符串列表仍存在。
- 新增 `clientDetails` 可返回每个客户端的 `remoteAddress` 和 `macAddress`。
- 尚未识别 MAC 的连接返回空字符串。
