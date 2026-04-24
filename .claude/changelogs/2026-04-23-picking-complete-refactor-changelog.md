# 2026-04-23 拣货完成接口重构变更日志

## 变更文件
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `http/test-dongxinheping-picking-complete.http`

## 核心改动
1. 重写 `拣货完成.ms` 主流程，移除 `locationStatusUtils` 依赖。
2. 并发模型改为与上传接口一致：
   - 全局锁：`GET_LOCK('light:picking:upload:global', timeout)`
   - `finally` 中释放锁。
3. 状态更新改为批量模式：
   - 批量聚合查询 `t_picking_upload_detail`。
   - 批量 `upsert` 到 `t_location_status`。
4. 灯控改为锁内同步调用并显式校验返回：
   - 调用失败直接抛错，接口返回 500。
5. 保留原有错误语义：
   - 主单不存在返回 400。
   - 锁繁忙返回 503。
   - 其他异常返回 500。
6. 回归用例调整：
   - 为 complete 请求补充 `details[].id`，匹配当前校验规则。

## 结果
- 代码路径更直接，完成接口与上传接口形成同一套状态真值与并发策略。
