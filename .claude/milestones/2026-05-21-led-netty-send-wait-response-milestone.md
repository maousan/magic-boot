# LED Netty发送可选等待回包里程碑

## 完成内容

- `sendToClient` 支持可选 `waitResponse` 参数。
- 默认不等待，兼容旧调用。
- 开启等待后，接口会返回客户端下一帧回包或超时结果。
