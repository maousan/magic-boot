# 2026-04-23 拣货数据上传全量重写变更日志

## 变更范围
- 重写文件：
  - `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- 测试文件补充：
  - `http/test-dongxinheping-picking-upload.http`

## 主要改动
1. 移除上传接口对旧上传封装的依赖，改为单文件流程实现。
2. 固化两段事务：
   - 事务1：主从表落库（覆盖旧明细 + 批量插入新明细）。
   - 事务2：在全局锁内重算并 UPSERT `t_location_status`。
3. 新增 MySQL 全局锁控制：
   - `GET_LOCK('light:picking:upload:global', timeout)`
   - 锁超时返回 `503`。
   - `finally` 内 `RELEASE_LOCK` 释放。
4. 灯控改为锁内同步调用：
   - `status0_user_count > 1` -> `BLUE`
   - `status0_user_count = 1` -> 用户色（缺省 `GREEN`）
   - `status0_user_count = 0` -> 关灯
5. 灯控失败策略：
   - 状态事务已提交后，灯控失败时接口返回 `500`；
   - 不回滚已提交的明细与 `t_location_status`。
6. 可观测性精简：
   - 不新增 trace 持久化逻辑；
   - 保留最小必要日志（请求、锁、状态更新、灯控结果）。

## 接口兼容性
- 路径保持不变：`POST /api/light/picking/upload`
- 入参结构保持不变。
- 响应语义：
  - 成功：`200`
  - 锁繁忙：`503`
  - 灯控失败或其他异常：`500`

## 验证结果
- 已完成静态检查：
  - 上传脚本不再引用旧上传公共函数。
  - 存在 `GET_LOCK/RELEASE_LOCK` 与两段事务控制。
  - 存在 `503`（锁繁忙）与 `500`（异常）出口。
- 未执行服务端动态联调（本次仅完成代码改造与用例补充）。
