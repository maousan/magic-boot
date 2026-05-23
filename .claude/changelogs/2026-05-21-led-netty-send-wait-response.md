# LED Netty发送可选等待回包

## 变更内容

- `LedNettySendRequest` 新增 `waitResponse`，默认 `false`。
- `LedNettyServerHandler.sendTo` 新增等待回包模式，发送后可等待同一远端地址的下一帧回包。
- `LedNettySendResponse` 新增 `waitResponse`、`responseCount`、`responses`。

## 行为说明

- 默认不等待回包，保持原有接口行为。
- `waitResponse=true` 时最多等待 3 秒。
- 收到回包时返回 `rawResponseHex`、`payloadAscii`、`macAddress`、`ipAddress`、`crc`。
- 超时未收到回包时，发送结果仍按写入 socket 是否成功判断，回包项标记 `timeout=true`。
