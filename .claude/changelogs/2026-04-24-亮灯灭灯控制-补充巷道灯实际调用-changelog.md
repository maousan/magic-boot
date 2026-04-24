# 2026-04-24 亮灯灭灯控制补充巷道灯实际调用

## 变更文件
- data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms

## 变更内容
1. 新增巷道灯函数导入：
- `@/led/turnOnAisleLedByLotNo`
- `@/led/turnOffAisleLedByLotNo`

2. 在主灯派发后新增巷道灯实际调用：
- 对 `turnOffDispatchCodes` 循环调用 `turnOffAisleLedByLotNo({ lotNo })`
- 对 `turnOnAisleDispatchCodes` 循环调用 `turnOnAisleLedByLotNo({ lotNo })`
- `turnOnAisleDispatchCodes` 由 `turnOnMultiDispatchCodes` 与 `singleColorLocationMap` 去重汇总。

## 兼容性
- 对外接口结构和状态码不变。
- 不涉及数据库结构变更。
