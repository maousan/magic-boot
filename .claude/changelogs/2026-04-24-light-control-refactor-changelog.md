# 2026-04-24 亮灯灭灯控制接口重构变更日志

## 变更文件
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`
- `http/test-dongxinheping-light-control.http`

## 主要改动
1. 控制接口从 `locationStatusUtils` 方案改为脚本内直连 `t_location_status` 口径。
2. 移除异步分发配置与 async 分支，统一为同步调用亮/灭灯接口。
3. 引入全局锁保护：
   - `GET_LOCK('light:picking:upload:global', timeout)`
   - `finally` 中 `RELEASE_LOCK`。
4. 引入分片批处理：
   - `splitLocationCodeChunks` 对库位列表分片（默认 200）。
   - 分片执行聚合查询与 `t_location_status` upsert。
5. 灯控失败处理标准化：
   - `assertLightControlSuccess` 校验返回对象，失败抛错并返回 500。
6. 回归脚本对齐：
   - `mode=4` 用例补充 `duration=0`，匹配当前校验规则。

## 结果
- 控制接口与上传/完成接口的并发控制、状态真值与灯控执行策略达成一致。
