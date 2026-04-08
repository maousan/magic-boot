# 变更日志：新增AIMS更新article信息函数

- 日期：2026-04-07
- 文件：data/dongxinheping/function/aims/数据/更新article信息.ms

## 变更内容
- 新增函数 `更新article信息`，path 为 `/updateArticleInfo`。
- 支持请求体 `ArticleParam` 结构（`customBatchId` + `dataList`）。
- 新增字段校验：
  - `data` 不能为空
  - `dataList` 必须为非空数组
  - 每条 `dataList` 的 `id` 和 `data` 必填
- 调用 AIMS 接口：
  - `POST http://119.29.52.245:9002/articles`
  - `Content-Type: application/json;charset=UTF-8`

## 返回约定
- 返回 `{ success, status, response }` 结构，便于上游脚本判断。
