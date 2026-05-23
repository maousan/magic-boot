# WebSocket指定客户端消息发送函数计划

## 目标

在 `data/dongxinheping` 下新增一个 magic-api 函数，调用 LED 插件 `LedDeviceController.sendToClient` 对应的 HTTP 接口，供脚本内按远端地址向 Netty 客户端发送消息。

## 实施步骤

1. 复用 `data/dongxinheping/function/led` 函数组，保证函数归类到 LED 能力下。
   - 验证：函数 `groupId` 指向现有 led 分组。
2. 新增 `向指定客户端发送消息.ms` 函数。
   - 验证：参数包含 `remoteAddress`、`payload`、`payloadArray`、`payloadFormat`，支持 `ascii/hex/json`，脚本通过 Forest 调用 `/plugin/zintis-led-plugin/api/netty/server/send`。
3. 记录功能里程碑。
   - 验证：`.claude/milestones` 下存在对应说明文档。

## 成功标准

- 函数文件位于 `data/dongxinheping/function/led`，可被 magic-api 加载。
- 调用函数时传入 `remoteAddress` 与 `payload` 或 `payloadArray` 后，脚本会调用 LED 插件现有发送接口；`payloadFormat=json` 时会先反序列化。
- 缺少必填参数时返回结构化错误，不调用插件接口。
