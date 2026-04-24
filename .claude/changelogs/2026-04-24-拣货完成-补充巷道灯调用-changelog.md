# 2026-04-24 拣货完成补充巷道灯真实调用

## 变更文件
- data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms

## 变更内容
1. 新增巷道灯函数导入：
- `@/led/turnOnAisleLedByLotNo`
- `@/led/turnOffAisleLedByLotNo`

2. 在完成接口派发阶段新增巷道灯实际调用：
- 对 `turnOffDispatchCodes` 循环调用 `turnOffAisleLedByLotNo({ lotNo })`
- 对 `turnOnAisleDispatchCodes` 循环调用 `turnOnAisleLedByLotNo({ lotNo })`
- `turnOnAisleDispatchCodes` 由 `turnOnMultiDispatchCodes + singleColorLocationMap` 去重汇总得到

## 说明
- 保持原有 `t_location_led` 聚合判定逻辑不变，仅补齐“实际调用巷道灯函数”。
- 不涉及数据库结构变更。
