# LED Netty客户端列表新增MAC地址里程碑

## 完成内容

- `LedNettyServerHandler` 新增活跃连接 MAC 缓存。
- `LedNettyClientListResponse` 新增 `clientDetails` 字段。
- `LedNettyServerService.listClients()` 同时返回旧的 `clients` 和新的 `clientDetails`。

## 行为说明

- MAC 地址来自客户端报文解析。
- 客户端尚未上报或无法解析 MAC 时，`macAddress` 返回空字符串。
