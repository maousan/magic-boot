# 2026-04-24 拣货完成巷道灯聚合控制变更日志

## 变更概述
- 在`拣货完成.ms`接入巷道灯聚合控制逻辑。
- 新增对称函数：
  - `resolveAisleLedTurnOnCandidates(turnOnLocationCodes)`
  - `resolveAisleLedTurnOffCandidates(turnOffLocationCodes)`
- 实现规则：同一`led_id`下只要存在任一`status=1`库位则不关灯；仅当全部为`status=0`才关灯。

## 关键实现
- 新增批量SQL入参构造函数：`buildInSqlParams(values, paramPrefix)`。
- 亮灯路径：
  - 先批量更新`t_location_led.status=1`。
  - 按`led_id`去重，避免同次请求同LED重复亮灯。
- 灭灯路径：
  - 先批量更新`t_location_led.status=0`。
  - 再按`led_id`聚合统计`status=1`数量，仅对“全部status=0”的LED放行关灯。
- 主流程保持原有外部接口与状态码语义不变。

## 测试用例更新
- 更新`http/test-dongxinheping-picking-complete.http`，新增巷道灯聚合场景：
  - 同LED两库位先完成一个：不关灯。
  - 同LED最后一个完成：关灯。
  - 同次请求命中同LED多库位：仅一次开/关灯决策。

## 兼容性说明
- 未修改`/api/light/picking/complete`对外入参/出参结构。
- 未改动上传接口与巷道灯独立函数文件。
