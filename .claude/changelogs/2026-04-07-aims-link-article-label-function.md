# 变更日志：新增AIMS绑定article和label函数

- 日期：2026-04-07
- 文件：data/dongxinheping/function/aims/标签/绑定article和label.ms

## 变更内容
- 新增函数 `绑定article和label`，path 为 `/linkArticleToLabel`。
- 按接口定义支持参数：
  - `stationCode`（路径参数）
  - `labelCode`
  - `articleIdList`
  - 以及 `customBatchId/templateName/arrow/addInfo*/skipChecksumValidation/arrowDirection` 可选字段
- 调用 AIMS 接口：
  - `POST http://119.29.52.245:9002/labels/link/{stationCode}`
- 返回统一结构 `{ success, status, response }`。
