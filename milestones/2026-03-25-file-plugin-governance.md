# 里程碑：文件管理插件治理（内部系统精简版）

日期：2026-03-25
范围：`magic-api-plugin-file` + `data/magic-api` 文件浏览脚本

## M1 修正期（已完成）
- 修复 `mkdir` 事件落库语义错位风险（按完整路径拆分父路径与目录名）。
- 删除事件全链路补齐 `storage_key` 维度，避免多存储源串数据。
- 上传结果统一返回逻辑全路径，减少路径歧义。

## M2 建模期（已完成）
- `sys_file` 增加 `parent_id` 字段与树查询索引。
- 增加同级唯一约束 `uk_storage_parent_name_del`。
- `SysFileService` 增强：children 查询、按存储源路径查找、子树软删、重命名/移动元数据更新。

## M3 一致性期（已完成）
- `FileModule.mkdir` 改为仅落库目录元数据，不再写 `.folder` 占位对象。
- `FileModule.delete` 目录走子树软删，文件走存储删除 + 元数据软删。
- `move/rename` 支持元数据同步更新，降低列表漂移风险。

## M4 验收期（待你环境回归）
- 验证目录创建/列表/删除/重命名/移动在单存储与多存储源下行为一致。
- 验证异步物理清理任务按保留期执行，不误删未软删文件。

## 运行参数
- `magic.file.cleanup.cron`：默认 `0 */10 * * * ?`
- `magic.file.cleanup.retention-days`：默认 `7`
- `magic.file.cleanup.batch-size`：默认 `100`
