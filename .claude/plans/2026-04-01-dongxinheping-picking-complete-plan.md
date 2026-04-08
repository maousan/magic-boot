# 2026-04-01 东信和平拣货完成接口落地计划

## 目标
- 新增并交付 `POST /api/light/picking/complete` 接口。
- 与已上线拣货上传接口保持同目录与同鉴权策略。

## 实施范围
- 仅新增“拣货完成”单接口。
- 复用现有主从表：`t_picking_upload`、`t_picking_upload_detail`。

## 实施项
1. 在 `data/dongxinheping/api/东信和平/亮灯对接` 新增 `拣货完成.ms`。
2. 入参校验：`waveNo/userId/details` 必填，明细字段必填且数量非负。
3. 数据更新：按 `userId+waveNo` 命中主表，明细按 `bill_id+material_code+batch_no+location_code` 更新；未命中明细则插入新明细。
4. 事务保障：主表与明细更新全流程同一事务提交/回滚。
5. 新增 `.http` 测试用例与测试报告。

## 验收标准
- 接口返回结构符合 OpenAPI：`code/message/data`。
- 200 返回消息：`拣货完成同步成功`。
- 参数错误返回 400，事务异常返回 500。
- 主从数据更新与插入行为符合接口语义。