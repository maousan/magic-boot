# 亮灯链路（CommonCountMap口径）测试报告（最终版）

## 测试时间
- 2026-04-20

## 测试范围
- `/api/light/picking/upload`
- `/api/light/picking/complete`
- `/api/light/control`
- `CommonCountMap`（库位维度）
- 应用重启后的链路可用性

## 测试环境
- 应用：`dongxinheping`（端口 `8090`）
- 日志文件：`D:\IdeaProjects\magic-boot\logs\all.log`

## 总结论
- 本轮共执行 8 个测试用例：**7 个符合预期，1 个部分符合预期**。
- `upload/complete/control` 主链路均验证通过，库位维度计数与灯色分支逻辑符合预期。
- “重启后立即恢复缓存”因启动阶段缺少恢复器明确日志，当前只能判定为“链路恢复可用，启动即恢复证据不足”。

---

## 测试用例与结果

| 用例ID | 测试项 | 预期结果 | 实际结果 | 是否符合预期 |
|---|---|---|---|---|
| TC-01 | upload 单用户上报 | 接口200；`CommonCountMap` 对应库位计数=1；下发GREEN | 16:29:16 upload(U1) 返回200；`snapshot={A1-11-02-01-01=1, A1-11-01-01-01=2}`；日志有 `turn on ... color=GREEN` | 符合 |
| TC-02 | upload 多用户上报 | 接口200；同库位计数>=2；下发BLUE | 16:29:16 upload(U2) 返回200；`snapshot={A1-11-02-01-01=2, A1-11-01-01-01=2}`；日志有 `turn on ... color=BLUE` | 符合 |
| TC-03 | upload 幂等（重复上报） | 重复上报后计数不异常漂移，按DB回填校准 | 日志多次出现 `reconcile set(upload)` 且 `dbCount=memCount`，无异常增长 | 符合 |
| TC-04 | complete 库位状态评估 | 未完成数量>0时继续亮灯 | 16:12:47 `unpickedCount=2`，`turnOn=[A1-11-01-01-01]` | 符合 |
| TC-05 | complete 计数回填 | `common count reconcile done(complete)` 快照正确 | 16:12:47 `snapshot={A1-11-02-01-01=2, A1-11-01-01-01=2}`；16:13:58 变为 `{A1-11-02-01-01=1, A1-11-01-01-01=2}` | 符合 |
| TC-06 | control ON 指令 | 接口200；写入控制日志表 | 16:29:16~16:29:17 两次ON返回200；日志有 `insert ... t_light_control_log` | 符合 |
| TC-07 | control OFF 指令 | 接口200；写入控制日志表 | 16:29:17 OFF返回200；日志有 `insert ... t_light_control_log` | 符合 |
| TC-08 | 应用重启后恢复能力 | 重启后链路可用；计数可回到正确口径 | 15:28 应用重启成功（PID=6396）；后续16:29链路正常并回填到正确快照；但启动时无恢复器明确日志 | 部分符合 |

---

## 关键证据（日志摘录）

### 1) 重启成功
- `2026-04-20 15:28:05 ... Starting MagicBootApplication ... PID 6396`
- `2026-04-20 15:28:22 ... Tomcat started on port(s): 8090`
- `2026-04-20 15:28:26 ... Started MagicBootApplication in 22.889 seconds`

### 2) upload + CommonCountMap
- `2026-04-20 16:29:16 ... reconcile done(upload), snapshot={A1-11-02-01-01=1, A1-11-01-01-01=2}`
- `2026-04-20 16:29:16 ... CommonCountMap: {A1-11-02-01-01=1, A1-11-01-01-01=2}`
- `2026-04-20 16:29:16 ... reconcile done(upload), snapshot={A1-11-02-01-01=2, A1-11-01-01-01=2}`
- `2026-04-20 16:29:16 ... CommonCountMap: {A1-11-02-01-01=2, A1-11-01-01-01=2}`

### 3) 灯色分支（单/多用户）
- `2026-04-20 16:29:16 ... turn on from upload(single user) ... color=GREEN ... [A1-11-02-01-01]`
- `2026-04-20 16:29:16 ... turn on from upload(multi user) ... color=BLUE ... [A1-11-01-01-01]`

### 4) control ON/OFF 落库
- `2026-04-20 16:29:16 ... insert ... table:t_light_control_log`
- `2026-04-20 16:29:16 ... 后置处理 - API: /api/light/control - 耗时: 399ms`
- `2026-04-20 16:29:16 ... insert ... table:t_light_control_log`
- `2026-04-20 16:29:17 ... 后置处理 - API: /api/light/control - 耗时: 190ms`
- `2026-04-20 16:29:17 ... insert ... table:t_light_control_log`
- `2026-04-20 16:29:17 ... 后置处理 - API: /api/light/control - 耗时: 205ms`

---

## 不符合项与说明
- 无“明确失败”用例。
- `TC-08` 判定为“部分符合”：
  - 已满足：重启后主链路可用，且计数可被业务回填到正确值。
  - 尚缺：`CommonCountMapRecoveryInitializer` 在启动阶段的明确恢复日志证据，暂无法证明“启动即恢复完成”。

## 结论（是否符合预期）
- 从业务链路结果看：**整体符合预期**。
- 从“重启即恢复可观测性”看：**部分符合预期（可用性满足，可观测性不足）**。
