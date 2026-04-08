# 东信和平库位-巷道灯绑定单页面变更日志

## 变更日期
- 2026-04-04

## 变更内容
- 新增 PDA 兼容单页面：`/pda/led-mapping.html`。
- 页面支持两类操作：
  - 创建绑定（POST `/api/location/led-mapping`）
  - 删除绑定（DELETE `/api/location/led-mapping?lotNo=...`）
- 增加移动/PDA 友好交互：
  - 大尺寸输入框和按钮
  - 默认焦点
  - 回车即提交
  - 提交中禁用按钮防重复提交
- 增加统一响应面板，展示请求参数、HTTP 状态、接口 code/message/data 与耗时。

## 影响评估
- 不涉及后端接口变更。
- 不涉及数据库结构与数据迁移。
- 仅新增前端静态页面资源。
