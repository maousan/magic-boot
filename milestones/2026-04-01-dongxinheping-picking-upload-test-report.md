# 2026-04-01 东信和平拣货上传接口测试报告

## 测试对象
- 接口：`POST /api/light/picking/upload`
- 脚本：`data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- 用例：`http/test-dongxinheping-picking-upload.http`

## 一、静态检查结果（本次已执行）
1. 用例文件覆盖 8 个场景（成功、4类参数错误、重传覆盖、异常验证建议）：通过。
2. 脚本包含必填/范围校验（waveNo/userId/updateTime/details、数量非负、status枚举）：通过。
3. 脚本包含按 waveNo 覆盖写入逻辑（主表+明细先删后写）：通过。
4. 脚本采用动态字段探测并按同名优先写入：通过。

## 二、接口联调结果（待运行环境执行）
> 说明：当前会话未启动后端与数据库联调环境，以下为待执行项。

1. 成功上传-单条明细：待执行（预期 HTTP 200，message=亮灯指令发送成功）。
2. waveNo 缺失：待执行（预期 HTTP 400）。
3. details 为空：待执行（预期 HTTP 400）。
4. planQuantity 负数：待执行（预期 HTTP 400）。
5. status 非法：待执行（预期 HTTP 400）。
6. 同 waveNo 重传覆盖：待执行（预期第二次请求后仅保留最新主从数据）。
7. 数据库异常：待执行（预期 HTTP 500）。

## 三、结论
- 代码与测试资产已按计划交付，具备联调条件。
- 建议你在目标环境执行 `http/test-dongxinheping-picking-upload.http` 后回填“接口联调结果”章节，以完成最终验收。