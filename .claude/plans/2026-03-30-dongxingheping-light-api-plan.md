# 东信和平亮灯对接接口开发计划

## 目标
- 在 `data/dongxingheping` 下新增 4 个 magic-api 接口：
  - `/api/light/picking/upload`
  - `/api/light/picking/complete`
  - `/api/light/control`
  - `/api/light/control/off`
- 完成严格入参校验、日志落库、统一 ApiResponse。

## 实施项
1. 新增 `东信和平/亮灯对接` 分组与 4 个 `.ms` 文件。
2. 新增 3 张日志表（`t_picking_upload_log`、`t_picking_complete_log`、`t_light_control_log`）。
3. 增加 HTTP 测试用例，覆盖成功、参数错误、异常路径。
4. 输出变更日志与里程碑文档。

## 验收标准
- 4 个接口可被 magic-api 正常加载并路由。
- 参数错误时返回 400，DB 异常返回 500，正常返回 200。
- 三张日志表可正常入库。