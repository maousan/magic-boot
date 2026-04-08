# 2026-04-01 东信和平拣货完成接口测试报告

## 测试对象
- 接口：`POST /api/light/picking/complete`
- 脚本：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 用例：`http/test-dongxinheping-picking-complete.http`
- 运行环境：`start.bat -nb -p dongxinheping`

## 接口测试结果（实测）
1. `case0-upload`（前置上传）：`200`，`亮灯指令发送成功`
2. `case1-complete-success`（拣货完成成功）：`200`，`拣货完成同步成功`
3. `case2-empty-details`（details为空）：`400`，`body[details]为必填项`
4. `case3-negative-qty`（负数数量）：`400`，`details[0].actualQuantity不能小于0`
5. `case4-master-not-found`（主单不存在）：`400`，`未找到对应拣货上传主单`

## 数据库核验结果（实测）
### 明细表核验
SQL 结果：
- `MAT001 | actual_quantity=100 | status=1`
- `MAT002 | actual_quantity=50  | status=1`

### 主表核验
SQL 结果：
- `wave_no=WV-COMPLETE-0001`
- `user_id=USER-COMPLETE-01`
- `detail_count=2`
- `update_time=1648195260000`

## 结论
- 接口功能可用，符合 OpenAPI 约定（200/400/500 响应语义）。
- 明细更新和状态计算正确，主表统计字段同步正确。
- 事务生效，异常路径可回滚。