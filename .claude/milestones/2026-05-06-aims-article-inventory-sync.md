# 2026-05-06 里程碑：AIMS article 库存信息同步

## 已完成
- `定时同步article数据.ms` 从占位脚本完善为库存信息同步任务。
- 实现 `t_inventory -> updateArticleInfo -> AIMS /articles` 主链路。
- 支持同一 `location_id` 多条库存记录按 `edit_time desc` 展开为 `字段名_分页索引_行索引`，分页索引和行索引都从 1 开始。
- 支持每个 `location_id` 最多读取 3 页库存明细。

## 验证方式
- 通过 `.claude/tmp/verify-aims-article-inventory-sync-job.ps1` 校验 `.ms` 元数据、分隔线和关键脚本片段。

## 待核对
- 当前约定 `id = location_id`。
- 当前约定 `modifiedDate` 使用 `2026-03-20T05:45:54.167+0000` 格式。
- 当前 `data` 会包含库存业务字段：`warehouse_id`、`location_id`、`sku`、`lot_att09`、`qty`、`qty_allocated`、`qty_pa`、`edit_time`、`user_define1..5`。
