# 库位列表删除操作计划

## 目标

在 `dongxinheping-admin` 的库位列表操作列新增“删除”操作，删除 `t_location_warehouse` 中对应库位记录。

## 实施步骤

1. 新增 magic-api 删除接口 `/location/warehouse-location/delete`
   - verify: 接口参数、分组、方法和 SQL 与现有库位接口风格一致。
2. 前端 API 封装新增 `deleteWarehouseLocation(id)`
   - verify: 调用路径和返回类型与现有 API 封装一致。
3. 库位列表操作列新增删除按钮和确认弹窗
   - verify: 删除成功后刷新列表，并显示成功提示。
4. 构建前端静态资源
   - verify: `npm run build` 通过。

## 约束

- 不调整现有同步库存、更新标签、导入、手动添加逻辑。
- 删除接口按主键 `id` 删除；找不到记录时返回业务错误。
