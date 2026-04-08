# 计划：拣货完成支持多用户库位联动灭灯与颜色切换

- 日期：2026-04-07
- 目标接口：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 背景
- 当前灭灯判断基于当前用户主单，未覆盖同波次多用户并行作业场景。
- 业务要求：
  - 同一 `waveNo + locationCode` 维度，只有所有用户相关明细都完成，才允许灭灯。
  - 颜色规则：多人作业 `BLUE`，单人作业 `GREEN`。

## 设计
1. 完成后统计本次涉及的 `locationCode`。
2. 在事务内基于 `wave_no` 聚合查询未完成明细（`status != 1`）按库位统计。
3. 基于 `wave_no` 统计参与用户数（`count(distinct user_id)`）：
   - `>1` 使用 `BLUE`
   - 否则使用 `GREEN`
4. 对本次涉及库位分流：
   - 未完成数为 0 -> 加入异步灭灯列表
   - 未完成数 >0 -> 加入异步亮灯列表（颜色按用户数）
5. 事务提交后异步触发：
   - `async turnOffLedByArticleId({ articleIds })`
   - `async turnOnLedByArticleId({ color, articleIds })`

## 影响与兼容
- 不改变已有请求入参与表结构。
- 主流程成功返回不等待灯控执行结果（异步）。

## 验证
- 增加多用户同库位测试用例：
  - 用户甲完成，用户乙未完成 -> 不灭灯，颜色应为 BLUE。
  - 用户乙随后完成 -> 触发灭灯。