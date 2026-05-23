# WebSocket指定客户端消息发送函数里程碑

## 完成内容

- 复用 `data/dongxinheping/function/led/group.json` 函数组。
- 新增 `data/dongxinheping/function/led/向指定客户端发送消息.ms`，调用 LED 插件 `LedDeviceController.sendToClient` 对应的 `/plugin/zintis-led-plugin/api/netty/server/send` 接口。

## 调用约定

- 函数路径：`/led/sendToClient`
- 参数：`remoteAddress`、`payload`、`payloadArray`、`payloadFormat`
- `payloadFormat` 支持 `ascii`、`hex`、`json`；`json` 会先反序列化后再转换为 LED 插件可接收的格式。
- 返回：调用成功时返回插件接口响应文本；参数缺失或插件接口异常时返回结构化错误。
