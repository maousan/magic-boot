# 2026-04-29 仓库库位 Excel 导入接口变更日志

## 新增内容
- 新增接口：`POST /api/location/warehouse-location/import`
- 新增脚本：`data/dongxinheping/api/东信和平/库位/批量导入仓库库位.ms`
- 新增测试样例：`http/test-dongxinheping-warehouse-location-import.http`

## 数据表
- 表名：`t_location_warehouse`
- 字段：
  - `id`
  - `warehouse_code`
  - `location_id`
  - `create_time`
  - `update_time`

## 行为
- 接口上传字段为 `file`。
- 首次导入时自动建表。
- 按 `warehouse_code + location_id` 做唯一约束，重复导入刷新更新时间。
- 不保存来源文件名、导入批次、Excel行号和软删除字段。
