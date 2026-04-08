# 测试报告：拣货完成按库位灭灯

- 日期：2026-04-07
- 接口：`POST /api/light/picking/complete`
- 关联脚本：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 用例文件：`http/test-dongxinheping-picking-complete.http`

## 本次新增验证点
1. 同一 `locationCode` 下仅部分物料完成时，不触发灭灯。
2. 同一 `locationCode` 下最后一个未完成物料完成时，触发灭灯调用。

## 用例清单
- 用例 A（准备数据）：`1.1` 上传 `WV-COMPLETE-0002`，同库位 `A-02-01` 下两条物料。
- 用例 B（未全部完成）：`1.2` 完成 `MAT101`，预期仅更新明细状态，不触发灭灯。
- 用例 C（全部完成）：`1.3` 完成 `MAT102`，预期触发 `turnOffLedByArticleId`，入参 `articleIds` 包含 `A-02-01`。

## 执行结果
- 结果状态：未在当前会话内直连环境执行（待你在本地服务启动后执行 `.http` 用例确认）。
- 静态检查结果：通过。
  - 已确认新增 `locationCode` 收集逻辑。
  - 已确认按 `bill_id + location_code + status != 1` 的未完成统计逻辑。
  - 已确认仅当未完成数为 0 时调用灭灯接口。

## 结论
- 代码层面已满足“location_code 下所有物料都已拣时调用灭灯接口”的需求。