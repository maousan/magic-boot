# 2026-04-09 ScriptId 解析工具函数抽离变更日志

## 变更摘要

- 新增统一工具类 `ScriptIdUtils`，集中处理脚本执行 `scriptId` 解析规则。
- 将以下重复逻辑改为统一调用工具函数：
  - `DefaultMagicAPIService`
  - `RequestMagicDynamicRegistry`
  - `FunctionMagicDynamicRegistry`
  - `RequestHandler`

## 统一规则

- 优先使用资源自身 `id`（若非空）。
- API 脚本回退规则：`METHOD:/path`（支持传入 `prefix`）。
- Function 脚本回退规则：`FUNCTION:path`（path 缺失时回退 name，再回退 `unknown`）。

## 兼容性说明

- 仅做解析逻辑抽离，不改变对外接口协议与执行入口。
- `RequestHandler` 保持原行为：按无前缀规则生成 API fallback scriptId。
