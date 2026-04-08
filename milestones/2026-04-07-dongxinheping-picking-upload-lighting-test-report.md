# 测试报告：拣货上传亮灯功能

- 日期：2026-04-07
- 接口：POST /api/light/picking/upload
- 文件：http/test-dongxinheping-picking-upload.http

## 新增用例
1. 单人上传同波次：预期异步亮灯 GREEN。
2. 第二用户上传同波次：预期异步亮灯 BLUE。

## 执行结果
- 静态检查通过：脚本已包含异步亮灯分支与颜色判定。
- 本会话未启动服务执行接口实测，请按 .http 用例联调验证日志：
  - dispatch async turnOn from upload ... color=GREEN/BLUE
