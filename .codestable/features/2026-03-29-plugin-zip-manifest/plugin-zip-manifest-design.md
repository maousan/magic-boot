---
doc_type: design
feature: plugin-zip-manifest
status: current
created: 2026-03-29
migrated_from: docs/superpowers/specs/2026-03-29-plugin-zip-manifest-design.md
tags: [pf4j, plugin, upload, manifest, governance]
---

# PF4J 插件 ZIP 强约束 + Manifest 治理设计

## 1. 背景与目标

当前 PF4J 上传安装链路仅接收 `.jar`，并依赖插件内描述信息完成加载。该模式在运行时可用，但缺少平台层面的包治理能力（结构规范、完整性校验、兼容性声明、权限声明）。

本设计目标：

- 上传入口强约束为 ZIP（不再接受 JAR 直传）
- ZIP 必须包含 `Manifest.json` 与核心插件 JAR
- 首版不强制签名，但预留签名字段
- 保持 PF4J 运行时加载逻辑稳定，新增平台治理层校验

## 2. 范围与非目标

### 2.1 范围

- 调整 `/plugin/admin/upload` 上传规则为 zip-only
- 增加 ZIP 结构、安全、Schema、一致性、完整性、兼容性校验
- 扩展插件元数据落库字段用于审计和治理
- 补充 HTTP 用例与验收标准

### 2.2 非目标

- 首版不引入签名强制校验
- 首版不引入人工审核工作流
- 首版不改造 PF4J 生命周期核心机制

## 3. 插件包规范

### 3.1 文件格式

上传文件后缀必须为 `.zip`。

### 3.2 ZIP 结构

ZIP 根目录必须包含：

- `Manifest.json`
- `plugin.jar`（或 Manifest 中 `entryJar` 指向的 JAR 文件）

### 3.3 Manifest 字段（v1）

必填字段：

- `pluginId`
- `version`
- `displayName`
- `entryJar`
- `requiresMagicBoot`
- `permissions`
- `checksumSha256`

可选字段：

- `description`
- `author`
- `signature`（预留，不强制）

## 4. 元数据分层策略

### 4.1 PF4J 元数据（运行时事实来源）

插件内部 `plugin.properties` 继续作为运行时加载事实来源：

- `pluginId`
- `version`
- `pluginClass`
- `provider`

### 4.2 Manifest 元数据（平台治理来源）

Manifest 作为分发、审计、兼容、权限治理来源：

- 兼容版本声明
- 权限声明
- 包完整性摘要

### 4.3 一致性约束

安装时强制校验：

- `Manifest.pluginId == plugin.properties.pluginId`
- `Manifest.version == plugin.properties.version`

任一不一致即拒绝安装。

## 5. 服务端校验流程

`POST /plugin/admin/upload` 处理流程：

1. 基础校验：文件非空、后缀 `.zip`、大小限制
2. 安全解压到临时目录（拒绝路径穿越：`../`、绝对路径、盘符路径）
3. 结构校验：存在 `Manifest.json` 与 `entryJar`
4. Schema 校验：Manifest 必填字段完整
5. 完整性校验：计算 `entryJar` SHA-256 比对 `checksumSha256`
6. 一致性校验：Manifest 与 `plugin.properties` 的 `pluginId/version` 一致
7. 兼容性校验：`requiresMagicBoot` 满足当前系统版本
8. 安装与持久化：移动 JAR -> PF4J load -> 插件信息入库 -> 清理临时目录

## 6. 接口设计

保持现有路径：`POST /plugin/admin/upload`

- 请求：`multipart/form-data`，字段 `file`
- 成功：返回插件基础信息 + 校验摘要
- 失败：返回标准错误结构（含错误码）

建议错误码：

- `PLUGIN_UPLOAD_INVALID_TYPE`
- `PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID`
- `PLUGIN_MANIFEST_INVALID`
- `PLUGIN_CHECKSUM_MISMATCH`
- `PLUGIN_DESCRIPTOR_MISMATCH`
- `PLUGIN_VERSION_INCOMPATIBLE`
- `PLUGIN_INSTALL_FAILED`

## 7. 数据库扩展设计

在 `magic_plugin` 表新增治理字段（均可空，兼容历史数据）：

- `package_type`（`ZIP` / `LEGACY_JAR`）
- `package_checksum`
- `manifest_version`
- `manifest_json`
- `requires_magic_boot`
- `permissions`
- `install_source`
- `install_time`

## 8. 兼容与迁移策略

- 历史记录保留为 `LEGACY_JAR`，可查可管
- 新上传仅允许 ZIP
- 历史记录 `manifest_json` 可为空，不影响查询
- 分阶段上线：先加字段，再启用 zip-only 逻辑，再补前端展示

## 9. 错误处理与可观测性

- 所有拒绝场景必须返回明确错误码与错误说明
- 关键日志包含：请求 ID、插件 ID、失败阶段、异常摘要
- 临时目录清理失败记录警告日志，不影响主流程返回

## 10. 测试方案与验收

新增 `http` 用例建议：

- `http/plugin-upload-zip.http`
- `http/plugin-upload-negative.http`
- `http/plugin-upload-compatibility.http`

验收标准：

- zip-only 生效，jar 上传被拒
- 结构/一致性/完整性/兼容性校验均可命中
- 插件生命周期接口无回归
- 新增治理字段正确落库

## 11. 风险与后续演进

已识别风险：

- 对现有发布流程有规范切换成本
- Manifest 与插件内描述文件可能出现双源冲突

缓解策略：

- 通过强一致性校验阻止冲突
- 通过明确错误码降低排障成本

后续建议（非首版）：

- 将 `signature` 从可选升级为必填并强制校验
- 引入"上传待审核 -> 审核通过安装"的工作流
