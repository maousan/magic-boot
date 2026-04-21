# 2026-04-21 关灯duration统一为0s计划

## 目标
将关灯场景下发给AIMS的`duration`由`0`统一调整为`0s`，与协议期望一致。

## 范围
- `data/dongxinheping/function/aims/控制灯/根据articleId关灯.ms`
- `data/dongxinheping/function/aims/控制灯/根据labelCode关灯.ms`

## 风险
- 若三方只接受`0`不接受`0s`，可能导致关灯调用失败（需联调确认）。

## 验证
- 检查代码中关灯请求体是否统一为`duration: "0s"`。
- 通过接口联调观察AIMS响应码与日志。
