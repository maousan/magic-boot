# ExtendedMagicJobController 迁移到 .ms 计划

## 背景
将 `ExtendedMagicJobController.java` 的 5 个接口能力迁移为 magic-api `.ms` 脚本，统一到 `系统管理/任务管理` 分组。

## 目标
- 新增 5 个 `.ms` 接口，覆盖暂停、恢复、立即执行、历史查询、状态查询。
- 保持核心行为一致：成功返回 `ok` 或数据对象，异常返回错误信息字符串。
- 提供可执行的 HTTP 测试用例。

## 迁移清单
1. 创建 `暂停任务.ms`（POST `/{jobId}/pause`）
2. 创建 `恢复任务.ms`（POST `/{jobId}/resume`）
3. 创建 `立即执行任务.ms`（POST `/{jobId}/trigger`）
4. 创建 `查询任务历史.ms`（GET `/{jobId}/history`，支持 `page`、`size`）
5. 创建 `查询任务状态.ms`（GET `/{jobId}/state`）
6. 新增 `http/test-magic-job-ms.http` 用例

## 风险与对策
- 风险：Bean 获取失败导致脚本运行异常。
- 对策：脚本统一 `try/catch`，异常按原控制器语义返回错误字符串。

## 验收标准
- 脚本文件可被 magic-api 正常识别（JSON 元信息 + `================================` + 脚本体）。
- 5 个接口路径和方法与迁移设计一致。
- 提供测试用例及测试结果说明。
