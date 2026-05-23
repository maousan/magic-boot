# LED Netty指定客户端发送函数测试接口计划

## 目标

新增一个 magic-api 测试接口，用于调用 `data/dongxinheping/function/led/向指定客户端发送消息.ms`，验证 LED 插件 `sendToClient` 发送链路。

## 实施步骤

1. 在 `data/dongxinheping/api/东信和平/亮灯控制` 下新增测试接口。
   - 验证：接口 `groupId` 指向亮灯控制分组，`method` 为 `POST`。
2. 接口脚本调用 `@/led/sendToClient` 函数。
   - 验证：按函数参数顺序传入 `remoteAddress, content, payload, payloadArray, payloadFormat`，示例覆盖 `payloadFormat=json`。
3. 补充变更日志和里程碑。
   - 验证：`.claude/changelogs` 与 `.claude/milestones` 下存在对应文档。

## 成功标准

- 测试接口文件可被 magic-api 加载。
- 请求体包含 `remoteAddress` 且包含 `payload/content` 或 `payloadArray` 时，会调用 LED Netty 发送函数；`payloadFormat=json` 时由函数层反序列化。
- 请求体缺少必要字段时返回 400 错误。
