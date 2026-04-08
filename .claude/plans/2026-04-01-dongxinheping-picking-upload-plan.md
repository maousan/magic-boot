# 2026-04-01 东信和平拣货上传接口落地计划

## 目标
- 在 `data/dongxinheping` 新增并启用 `POST /api/light/picking/upload`。
- 按 OpenAPI 完成参数定义、校验、主从覆盖写入与标准响应。

## 范围
- 仅实现拣货上传接口，不包含 complete/control/off。
- 不新增 Flyway，依赖现有 `t_picking_upload`、`t_picking_upload_detail`。

## 实施项
1. 新增 API 分组与 `拣货数据上传.ms`。
2. 实现入参校验：必填、明细非空、数量非负、status 枚举。
3. 实现主从覆盖写入：按 `waveNo` 先删后写。
4. 实现宽松字段映射：优先同名字段，字段不存在时跳过。
5. 新增 HTTP 测试用例并覆盖成功/参数错误/覆盖/异常建议场景。
6. 输出变更日志与里程碑文档。

## 验收标准
- 接口可被 magic-api 从 `data/dongxinheping` 正常加载。
- 成功返回 `code=200`；参数错误返回 400；异常返回 500。
- 同 `waveNo` 重传后数据以最新请求为准。