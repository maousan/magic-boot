# 里程碑：Get all articles information 适配函数

## 里程碑目标
- 在 `dongxinheping` 目录完成 OpenAPI “Get all articles information” 的函数侧适配。

## 完成项
- [x] 新增函数文件并接入 AIMS 配置读取。
- [x] 支持关键查询参数与可选授权头。
- [x] 输出统一返回结构（success/status/url/response）。
- [x] 补齐计划与变更日志文档。

## 验收建议
- 在 magic-api 控制台直接调用 `/getAllArticlesInfo`。
- 验证三类场景：
  - 无查询参数（全量查询）
  - 带分页参数
  - 带 Authorization 头访问受保护接口
