# 2026-04-09 Flyway 缺失表回补（sys_component）

## 背景

- 开发库对账发现：`db/migration` 期望存在 `sys_component`，但当前库缺失。
- 当前 Flyway 基线版本为 `20260329.005`，早期迁移可能被 baseline 跳过，因此不会自动创建该表。

## 变更内容

- 新增幂等迁移脚本：
  - `magic-boot-master/src/main/resources/db/migration/V20260409.001__create_sys_component_if_missing.sql`
- 迁移逻辑：
  - 使用 `CREATE TABLE IF NOT EXISTS` 回补 `sys_component`
  - 不影响已存在该表的环境

## 预期结果

- Flyway 执行后，缺失环境会自动补齐 `sys_component`。
- 已有环境不会发生重复建表错误。
