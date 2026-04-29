# 2026-04-29 仓库库位 Excel 导入接口计划

## 目标
- 新增一个 magic-api 接口，支持上传 Excel 文件并批量导入仓库编码和库位ID。

## 变更范围
- 新增 API 脚本：`data/dongxinheping/api/东信和平/库位/批量导入仓库库位.ms`
- 新增 HTTP 测试样例：`http/test-dongxinheping-warehouse-location-import.http`
- 自动创建数据表：`t_location_warehouse`

## 表设计
```sql
create table if not exists t_location_warehouse (
    id varchar(32) not null primary key comment '主键ID',
    warehouse_code varchar(64) not null comment '仓库编码',
    location_id varchar(128) not null comment '库位ID',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_location_warehouse_code_location (warehouse_code, location_id)
) comment='仓库库位导入数据';
```

## 导入规则
- 上传字段名：`file`
- 支持表头：
  - 仓库编码：`仓库编码`、`warehouse_code`、`warehouseCode`
  - 库位ID：`库位ID`、`库位id`、`location_id`、`locationId`
- 空文件或空数据返回失败。
- 单行仓库编码或库位ID为空时跳过该行，并在 `errors` 返回行号和原因。
- 相同 `warehouse_code + location_id` 重复导入时只刷新 `update_time`。

## 验证方式
- 静态校验 `.ms` 元数据 JSON 可解析。
- 检查接口方法、路径、上传参数和建表 SQL。
- 通过 HTTP multipart 样例进行联调。
