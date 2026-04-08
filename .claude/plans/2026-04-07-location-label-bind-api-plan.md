# 2026-04-07 新增库位码与电子标签码绑定接口计划

## 目标
- 新增 `POST /api/location/label-mapping` 接口。
- 绑定时仅调用 AIMS 接口，不写入本地数据库表。

## 实施步骤
1. 在 `data/dongxinheping/api/东信和平/巷道灯` 新增绑定 `.ms` 文件。
2. 做入参校验：`locationCode`、`labelCode` 必填，`stationCode` 默认 `10001`。
3. 调用 AIMS `POST /labels/link/{stationCode}`，将 `locationCode` 作为 `articleIdList` 唯一值。
4. 失败返回 502，成功返回 `1`。
5. 编译验证。
