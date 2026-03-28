# 里程碑：文件浏览批量删除改造（DELETE + Body）

日期：2026-03-26  
范围：`文件浏览` 模块（magic-api 脚本）

## 变更内容（已完成）
- 批量删除接口改为：`DELETE /system/file/browse/batch-delete`
- 入参改为仅支持 `application/json` Body（不再兼容 query 参数）
- Body 结构：
  - `fileIds: string[]`（推荐）
  - `filePaths: string[]`（可选）
  - `storageKey: string`（必填）
- 删除规则：
  - `fileIds`：先查 `sys_file` 获取 `storage_key + file_path`，再执行删除
  - `filePaths`：按 `storageKey`（或默认存储）执行删除
- 返回保持统计与明细字段：
  - `total/deletedCount/failedCount/notFoundCount/allSuccess`
  - `deleted[]/failed[]/notFound[]`
  - `deletedIds/deletedPaths/failedIds/failedPaths/notFoundIds/notFoundPaths`

## 联调支持
- 新增 `http/test-file-batch-delete.http`，提供 `DELETE + Body` 的独立联调示例。
