# 2026-04-01 东信和平拣货上传接口变更日志

## 新增
- 新增接口分组：`data/dongxinheping/api/东信和平/亮灯对接`。
- 新增接口脚本：`拣货数据上传.ms`，路由 `POST /api/light/picking/upload`。
- 新增 HTTP 用例：`http/test-dongxinheping-picking-upload.http`。
- 新增任务计划文档：`.claude/plans/2026-04-01-dongxinheping-picking-upload-plan.md`。

## 功能调整
- 接口校验规则对齐 OpenAPI：`waveNo`、`userId`、`updateTime`、`details` 必填，明细数量非负，`status` 仅支持 0/1。
- 写库语义调整为按 `waveNo` 覆盖：主表/明细先删后写。
- 写入策略采用宽松字段映射：同名字段优先，缺失字段自动跳过。

## 配置
- `application-dongxinheping.yml` 保持 `magic-api.resource.location: data/dongxinheping`。