# 定时任务执行日志查询变更日志

## 变更内容

- 定时任务管理页操作列新增“日志”按钮。
- 新增执行日志弹窗，复用现有 `/magic/job/{jobId}/history` 接口查询任务执行记录。
- 日志表格展示状态、触发方式、开始时间、结束时间、耗时、执行结果和异常信息。
- 日志弹窗支持上一页、下一页切换查询。

## 影响范围

- `dongxinheping-admin/src/views/JobManagement.vue`

## 验证

- `npm run build` 通过。
