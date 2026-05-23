# LED Netty发送可选等待回包计划

## 目标

为 `sendToClient` 增加可选参数 `waitResponse`，默认不等待；显式开启时，发送后等待同一客户端的下一帧上行报文并返回。

## 实施步骤

1. 扩展请求与响应 DTO。
   - 验证：请求新增 `waitResponse=false`；响应新增 `waitResponse`、`responseCount`、`responses`。
2. 在 Netty handler 中实现按远端地址等待下一帧回包。
   - 验证：发送前注册等待器，收到报文时完成等待器，超时返回 timeout 结果。
3. 在 service 层接入 `waitResponse`。
   - 验证：默认调用不阻塞；`waitResponse=true` 时最多等待 3 秒。

## 成功标准

- 未传 `waitResponse` 时行为与原先一致。
- 传 `waitResponse=true` 且设备回包时，响应中包含回包 HEX、payload ASCII、MAC、IP、CRC。
- 传 `waitResponse=true` 但设备不回包时，发送成功仍可返回，同时 `responses` 标记 `timeout=true`。
