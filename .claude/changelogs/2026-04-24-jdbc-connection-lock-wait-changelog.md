# 2026-04-24 JDBC连接获取失败修复（全局锁等待去阻塞化）

## 问题现象
- 接口高并发时频繁出现：`Could not open JDBC Connection for transaction`。
- 业务表现为上传/完成/控制接口偶发或持续 `500`。

## 根因分析
- 三个接口均使用 MySQL `GET_LOCK(lockKey, 10)` 阻塞等待全局锁。
- 阻塞等待期间会持续占用 JDBC 连接。
- 并发请求累积后，连接池可用连接被等待中的请求占满，后续请求在 `beginTrans()` 阶段无法获取连接，触发上述错误。

## 修复内容
- 将三处 `acquireGlobalLock` 从阻塞模式改为非阻塞轮询：
  - 由 `GET_LOCK(lockKey, timeoutSeconds)` 改为 `GET_LOCK(lockKey, 0)`。
  - 在应用层按 `timeoutSeconds` 控制总等待时长，`80ms` 间隔短睡眠重试。
- 保持原有业务语义不变：
  - 在总等待时长内抢到锁即继续；超时仍返回锁繁忙（503）。
  - `RELEASE_LOCK` 逻辑不变。

## 影响范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 预期收益
- 等锁请求不再长期占用 JDBC 连接。
- 显著降低连接池耗尽导致的事务建连失败概率。
