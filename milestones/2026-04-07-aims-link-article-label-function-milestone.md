# 里程碑：AIMS article-label绑定函数落地

- 日期：2026-04-07
- 范围：东信和平 AIMS 标签函数

## 达成
- 新增可复用函数 `@/aims/labels/linkArticleToLabel`。
- 完成对 `POST /labels/link/{stationCode}` 的封装。
- 增加了参数校验与统一返回结构。
- 新增了对应 HTTP 测试文件用于联调。
