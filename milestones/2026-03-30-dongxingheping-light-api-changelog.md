# 2026-03-30 东信和平亮灯接口变更日志

## 新增
- 新增 magic-api 分组：`data/dongxingheping/api/东信和平/亮灯对接`。
- 新增接口脚本：
  - `拣货数据上传.ms`
  - `拣货完成.ms`
  - `亮灯灭灯控制.ms`
  - `灭灯控制.ms`
- 新增 Flyway 迁移：`V20260330.001__create_t_light_api_log_tables.sql`。
- 新增 HTTP 测试文件：`http/test-dongxingheping-light-api.http`。

## 说明
- 接口统一配置 `require_login=false`，用于系统对接调用。
- 表前缀按要求统一使用 `t_`。