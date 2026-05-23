# 2026-05-06 变更日志：AIMS article 库存信息同步

## 背景
- `定时同步article数据.ms` 原先是占位脚本。
- 需要从 `t_inventory` 读取库存信息，并同步到 AIMS article 的 `data` 字段。

## 变更内容
1. `定时同步article数据.ms`
- 新增按 `location_id` 分批读取逻辑，每批 `batchSize` 个库位。
- `queryLocationBatch` 同时返回 `location_id` 和该库位最新库存行的 `sku`，article item 的 `name` 使用该 `sku`。
- 新增库存明细查询，按单个 `location_id` 分页读取，每个库位最多 3 页，按 `edit_time desc` 排序。
- 新增 `buildArticleDataList`，将同库位多条库存记录合并成一个 article 更新项。
- 新增字段展开规则：`字段名_分页索引_行索引`，分页索引和行索引都从 1 开始，例如 `sku_1_1`、`qty_1_1`、`sku_1_2`。
- 2026-05-09 调整字段展开规则为 `字段名_行索引`，行索引使用 `(pageIndex - 1) * batchSize + rowIndex` 计算，例如第 2 页第 1 行在 `batchSize=20` 时为 `sku_21`。
- `modifiedDate` 改为 `yyyy-MM-ddTHH:mm:ss.SSS+0000` 格式，例如 `2026-03-20T05:45:54.167+0000`。
- 调用 `@/aims/articles/updateArticleInfo` 批量更新 AIMS article。
- 返回同步统计：批次数、读取库位数、读取库存行数、更新 article 数、失败批次。

2. 验证脚本
- 新增 `.claude/tmp/verify-aims-article-inventory-sync-job.ps1`，校验 `.ms` 结构和关键逻辑片段。

## 影响范围
- 仅影响 AIMS article 数据同步定时任务。
- 不创建或修改数据库表结构。
- 不启用该定时任务，仍保持元数据中的 `enabled: false`。
