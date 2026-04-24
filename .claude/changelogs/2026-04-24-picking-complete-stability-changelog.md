# 2026-04-24 拣货完成接口稳定性优化变更日志

## 变更文件
- data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms

## 变更内容
1. 强化灯控调用结果判定：
- `assertLightControlSuccess` 从“`result == null` 直接忽略”改为“抛错失败”。
- 当 `result.success == false` 时，优先拼接 `message/msg` 返回，便于定位失败原因。

2. 修复单色开灯遍历稳定性：
- 将 `for(colorKey in singleColorLocationMap)` 直接遍历改为：
  - 先构建 `singleColorKeys`（显式 key 列表）
  - 再按 key 列表调用 `turnOnLedByArticleId`
- 目的：避免 map 直接遍历在运行时语义差异导致“应调用未调用”。

## 兼容性
- 不改变 `POST /api/light/picking/complete` 入参/返回结构。
- 不涉及数据库结构变更。
- 不引入字符集转换 SQL（按当前数据库已统一前提）。
