# 2026-04-21 关灯时长改为0s变更日志

## 变更背景
业务确认：关灯场景下发时长应为`0s`，而不是`0`。

## 变更内容
- 修改 [根据articleId关灯.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/function/aims/控制灯/根据articleId关灯.ms) 中关灯请求项的 `duration`：`0 -> 0s`
- 修改 [根据labelCode关灯.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/function/aims/控制灯/根据labelCode关灯.ms) 中关灯请求项的 `duration`：`0 -> 0s`

## 影响评估
- 仅影响关灯下发参数格式，不改变调用路径和其他字段。
- 开灯场景不受影响。

## 建议验证
- 执行一次关灯链路，确认请求体中 `duration` 为 `0s`。
- 观察AIMS返回状态码是否仍为预期成功码。
