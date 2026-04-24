# 2026-04-24 拣货完成巷道灯开灯不生效修复

## 变更文件
- data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms

## 修复内容
1. 去除对 map 遍历推导 singleColorKeys 的依赖：
- 改为在构建 `singleColorLocationMap` 时同步维护 `singleColorKeys`。
- 避免运行时对象遍历语义差异导致“有候选但未执行开灯调用”。

2. 巷道灯开灯候选同步构建：
- 在构建单色候选时直接 `addUnique(turnOnAisleDispatchCodes, ...)`。
- 多用户候选仍从 `turnOnMultiDispatchCodes` 进入巷道开灯列表。

3. 增加派发结果日志字段：
- `aisleTurnOn`
- `aisleTurnOff`
用于快速判断巷道灯是否进入实际调用列表。
