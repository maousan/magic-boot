# LED Netty客户端列表新增MAC地址

## 变更内容

- `LedNettyServerHandler` 在收到客户端报文并解析到 MAC 时，按远端地址缓存最后一次识别到的 MAC。
- `LedNettyClientListResponse` 新增 `clientDetails` 字段，每项包含 `remoteAddress` 和 `macAddress`。
- `LedNettyServerService.listClients()` 返回 `clientDetails`，同时保留原有 `clients` 字符串列表以兼容旧调用。

## 注意

- MAC 地址来自客户端上报报文解析；客户端连接后尚未上报或报文中无法解析 MAC 时，`macAddress` 为空字符串。
