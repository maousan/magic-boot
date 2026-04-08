# 变更日志：拣货完成灭灯调用异步化

- 日期：2026-04-07
- 文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 变更内容
- 将事务提交后的灭灯调用由同步改为异步：
  - `async turnOffLedByArticleId({ articleIds: shouldTurnOffLocationCodes })`
- 增加异步触发异常捕获并记录日志，不影响主流程返回。

## 行为变化
- 拣货完成接口成功响应不再等待灭灯接口执行完成。
- 灭灯触发失败时记录日志，主流程仍保持成功返回（事务已提交）。