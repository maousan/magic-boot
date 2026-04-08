# 变更日志：拣货完成按库位灭灯

- 日期：2026-04-07
- 接口文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 测试文件：`http/test-dongxinheping-picking-complete.http`

## 背景
- 需求：当同一 `location_code` 下所有物料都已拣完时，调用灭灯接口。

## 变更内容
- 在拣货完成请求处理中，新增 `locationCodeMap` 收集本次涉及的库位。
- 更新明细后，按 `bill_id + location_code` 查询未拣完数量（`status != 1`）。
- 当某库位未拣完数量为 0 时，将该库位加入灭灯列表。
- 事务提交后，统一调用 `turnOffLedByArticleId({ articleIds: [...] })` 执行灭灯。

## 影响范围
- 仅影响“拣货完成”接口的后置行为（灭灯触发条件）。
- 不修改上传接口与表结构。

## 风险与说明
- 灭灯调用在事务提交后执行；若第三方接口异常，会抛出错误并返回失败信息，但主数据事务已提交。