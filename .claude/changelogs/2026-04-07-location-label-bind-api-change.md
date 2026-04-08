# 2026-04-07 库位标签绑定接口变更日志

## 变更内容
- 新增接口：`POST /api/location/label-mapping`
- 新增文件：`data/dongxinheping/api/东信和平/巷道灯/绑定库位码和电子标签码的绑定关系.ms`

## 行为说明
- 接口接收 `locationCode`、`labelCode`、可选 `stationCode`。
- 默认 `stationCode=10001`。
- 仅调用 AIMS `labels/link/{stationCode}`，不写入本地数据库。
