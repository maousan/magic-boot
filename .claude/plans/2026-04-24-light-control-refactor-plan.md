# 亮灯灭灯控制接口重构计划（2026-04-24）

## 目标
- 重构 `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`。
- 统一到 `t_location_status` 真值口径，移除 `locationStatusUtils` 与异步分发分支。
- 与上传/完成接口保持一致并发模型（`GET_LOCK` 全局锁）。

## 实施范围
- 控制接口脚本重写：
  - 参数校验保持兼容（mode/color/duration 规则保持）。
  - 控制日志仍写 `t_light_control_log`。
  - 在锁内按波次库位批量重算 `t_location_status`。
  - 按 `status0_user_count` 分流同步执行亮/灭灯。
- 回归脚本对齐：
  - 更新 `http/test-dongxinheping-light-control.http` 中 mode=4 的 duration 参数。

## 关键设计
1. 固定锁键：`light:picking:upload:global`。
2. 分片策略：
   - `locationChunkSize=200`，对库位 `IN` 查询与 upsert 均分片。
3. 灯控决策：
   - `status0<=0` -> 关灯
   - `status0=1` -> 单人色（mode1/2用请求色，mode3/4用GREEN）
   - `status0>1` -> 多人色（`mixColor`，默认 `CYAN`）
4. 失败语义：
   - 锁失败：503
   - 灯控调用失败：500

## 验证
- 静态验证：
  - 不再引用 `locationStatusUtils`。
  - 不再包含 async 分发逻辑。
  - 存在 `GET_LOCK/RELEASE_LOCK`、分片重算、同步灯控失败抛错路径。
