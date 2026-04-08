# 2026-04-08 变更日志：用户ID灯光颜色映射

## 变更概述
- 新增用户ID与灯光颜色映射能力，支持同一业务流程中不同用户展示不同灯光颜色。

## 详细变更
1. 数据库迁移
- 新增 Flyway 脚本：`V20260408.001__create_t_user_light_color_table.sql`
- 新建表：`t_user_light_color`
  - 字段：`user_id`、`color`、`enabled`、`remark`、`create_time`、`update_time`
  - 约束：`user_id` 唯一

2. 工具函数
- 新增函数：`data/dongxinheping/function/工具函数/获取用户灯光颜色.ms`
- 能力：按 `userId` 查询映射并返回合法颜色（RED/GREEN/BLUE/YELLOW/CYAN），未命中返回 `null`

3. 业务流程改造
- `拣货数据上传.ms`
  - 亮灯颜色优先取用户映射色；未配置时保持原逻辑（单人GREEN/多人BLUE）
- `拣货完成.ms`
  - 亮灯颜色优先取用户映射色；未配置时保持原逻辑（单人GREEN/多人CYAN）

4. 测试用例
- 新增 `http/test-dongxinheping-user-light-color-mapping.http`
