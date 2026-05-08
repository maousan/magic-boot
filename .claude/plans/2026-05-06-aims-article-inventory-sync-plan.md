# 2026-05-06 AIMS article 库存信息同步计划

## 目标
- 完善 `data/dongxinheping/job/aims/定时同步article数据.ms`。
- 从 `t_inventory` 按 `location_id` 分批读取库位，再按单个 `location_id` 分页读取库存数据。
- 按 `location_id` 聚合，每个库位构建一个 article 更新项。
- 调用 `@/aims/articles/updateArticleInfo` 更新 AIMS article 的 `data`。

## 数据构造规则
- `id` 使用 `location_id`，与现有灯控和绑定流程中的 articleId 使用库位码保持一致。
- `data` 中字段按“库存字段名 + 下划线 + 分页索引 + 下划线 + 行索引”生成，分页索引和行索引都从 1 开始。
- article 顶层 `modifiedDate` 使用 `2026-03-20T05:45:54.167+0000` 格式。
- 同一个 `location_id` 下多条库存记录按 `edit_time desc` 排序后编号。
- 每个 `location_id` 最多读取 3 页库存明细，每页 `batchSize` 条。

示例：`location_id = A` 有两条库存记录：
```json
{
  "id": "A",
  "data": {
    "warehouse_id_1_1": "WH01",
    "location_id_1_1": "A",
    "sku_1_1": "29137435",
    "lot_att09_1_1": "29137435",
    "qty_1_1": "29",
    "qty_allocated_1_1": "12",
    "qty_pa_1_1": "2",
    "edit_time_1_1": "2026-05-06 13:30:00",
    "user_define1_1_1": "1",
    "warehouse_id_1_2": "WH02",
    "location_id_1_2": "A",
    "sku_1_2": "29137436",
    "qty_1_2": "8"
  },
  "modifiedDate": "2026-03-20T05:45:54.167+0000"
}
```

## 实现要点
1. 批次读取
- 分页查询非空 `location_id`，按 `location_id` 分组排序。
- 对每个 `location_id` 单独分页查询 `t_inventory` 明细，最多 3 页，按 `edit_time desc` 排序。

2. 构建请求
- 每个 `location_id` 生成一个 `{ id, data }`。
- 同库位多行用分页索引和行索引后缀展开字段。
- 空值不写入 `data`，减少无意义字段覆盖。

3. 调用 AIMS
- 每批调用一次 `updateArticleInfo(dataList)`。
- 失败批次记录到 `failedBatches`，继续处理后续批次。

## 验证
- 校验 `.ms` 元数据 JSON 和分隔线。
- 静态校验读取 `t_inventory`、按 `location_id` 分组、单库位最多 3 页、按 `edit_time desc` 排序、调用更新函数、字段后缀构造逻辑。
