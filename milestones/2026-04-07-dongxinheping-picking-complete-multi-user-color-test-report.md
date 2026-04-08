# 测试报告：拣货完成多用户同库位颜色与灭灯联动

- 日期：2026-04-07
- 接口：`POST /api/light/picking/complete`
- 关联脚本：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 用例文件：`http/test-dongxinheping-picking-complete.http`

## 新增用例
1. 甲乙同波次同库位初始化上传（WV-MULTI-0001）。
2. 甲先完成，乙未完成：预期不灭灯，触发异步亮灯 `BLUE`。
3. 乙随后完成：预期触发异步灭灯。

## 结果
- 静态检查：通过
  - 已确认使用 `wave_no` 聚合统计未完成。
  - 已确认按波次用户数计算颜色（>1 为 `BLUE`，否则 `GREEN`）。
  - 已确认亮/灭灯均为异步触发。
- 接口联调：未在当前会话中启动服务执行，待本地环境按 `.http` 用例验证。