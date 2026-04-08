# 2026-04-03 东信和平亮灯控制接口变更日志

## 新增
- 新增分组：`data/dongxinheping/api/东信和平/亮灯控制`。
- 新增接口：`亮灯灭灯控制.ms`，路由 `POST /api/light/control`。
- 新增测试文件：`http/test-dongxinheping-light-control.http`。

## 逻辑说明
- 当前版本仅执行日志入库，记录到 `t_light_control_log`。
- 三方亮灯系统调用逻辑已预留 TODO，后续补充。
- 入参支持 `mode=1/2/3/4`、`color` 和 `duration` 枚举校验。