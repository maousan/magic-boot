# 2026-03-30 拣货上传主从表变更日志

- 新增 Flyway 迁移：`V20260330.101__create_t_picking_upload_master_detail_tables.sql`
- 改造 `拣货数据上传.ms`：由单表日志写入改为主从表写入