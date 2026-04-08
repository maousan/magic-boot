# 变更日志：拣货数据上传增加亮灯功能

- 日期：2026-04-07
- 文件：data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms

## 变更内容
- 新增 turnOnLedByArticleId 调用。
- 上传明细后收集 status != 1 的库位并去重。
- 事务提交后按 wave_no + location_code + status!=1 判断是否仍需亮灯。
- 颜色规则：同波次用户数 > 1 为 BLUE，否则 GREEN。
- 亮灯调用改为异步触发，不阻塞主流程。
