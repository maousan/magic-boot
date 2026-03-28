# 2026-03-29 插件 ZIP + Manifest 改造计划

## 目标

将 PF4J 插件上传从 JAR 直传改为 ZIP 强约束，并落地 Manifest 治理能力（首版不强制签名）。

## 实施步骤

1. 定义数据结构
- 新增 Manifest DTO、校验模型、错误码枚举
- 新增上传配置项（大小限制、临时目录、zip-only 开关）

2. 改造上传服务
- 在 `PluginManagerService` 增加 ZIP 上传处理流程
- 增加安全解压、防 Zip Slip、结构校验、Schema 校验
- 增加 checksum 和 descriptor 一致性校验

3. 扩展持久化
- 扩展 `magic_plugin` 表字段
- 更新 `PluginInfo` 与 Mapper 写入逻辑

4. 接口与返回统一
- 保持 `/plugin/admin/upload` 路径不变
- 增加标准错误码返回

5. 测试用例
- 在 `http` 目录补充正向、反向、兼容性用例
- 输出测试报告模板与实际结果

## 回滚策略

- 配置层关闭 zip-only（若保留开关）
- 回退上传校验逻辑版本
- 数据库新增字段保留，不影响旧流程

## 验收清单

- 仅 ZIP 可上传
- Manifest 校验与 checksum 校验生效
- PF4J 一致性校验生效
- 历史插件记录无回归
- 日志与错误码可追踪