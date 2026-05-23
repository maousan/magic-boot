# LED Netty发送函数测试接口

## 变更内容

- 新增 `data/dongxinheping/api/东信和平/亮灯控制/测试向指定客户端发送消息函数.ms`。
- 接口通过 `import '@/led/sendToClient' as sendToClient` 调用 LED Netty 指定客户端发送函数。

## 调用方式

- 方法：`POST`
- 路径：`/api/light/netty-send-function-test`
- 请求体字段：`remoteAddress`、`content`、`payload`、`payloadArray`、`payloadFormat`、`waitResponse`
- `payloadFormat=json` 时，函数会先反序列化 `payload`；数组转为 `payloadArray`，对象转为 JSON 字符串后按 ASCII 发送。
- `waitResponse=true` 时，会等待 LED 插件返回客户端下一帧回包。
