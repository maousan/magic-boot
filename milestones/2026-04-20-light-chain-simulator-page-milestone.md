# 亮灯链路模拟测试页面里程碑

## 里程碑名称
- 亮灯链路模拟测试页面（PC）

## 完成时间
- 2026-04-20

## 交付物
- 页面：`magic-boot-master/src/main/resources/static/pda/light-chain-simulator.html`
- 入口：`magic-boot-master/src/main/resources/static/pda/mapping-entry.html`
- 计划文档：`.claude/plans/2026-04-20-light-chain-simulator-page-plan.md`
- 变更日志：`.claude/changelogs/2026-04-20-light-chain-simulator-page-changelog.md`

## 达成目标
- 提供可直接用于联调的 PC 页面，覆盖 upload/complete/control 三个接口的模拟触发。
- 支持单步执行和一键整链路执行。
- 支持在页面内查看请求和响应结果，减少手工拼接请求成本。

## 验收要点
- 页面可正常访问并渲染。
- 单步调用可命中对应接口。
- 一键执行按顺序调用并在任一步失败时中断并提示。
