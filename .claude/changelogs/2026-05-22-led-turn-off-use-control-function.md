# 根据库位码关巷道灯函数变更日志

## 变更内容

- `根据库位码关巷道灯.ms` 改为调用 `/led/controlAisleLedDevice`。
- 保留库位码解析和 `t_location_led` 绑定关系查询。
- 移除函数内重复的设备查询、颜色命令解析和插件接口调用逻辑。

## 影响范围

- 关灯函数支持透传 `mode` 和 `waitResponse`。
- 返回结果保留原关键字段，并新增 `controlResult` 查看底层控制结果。
