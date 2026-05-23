---
doc_type: decision
decision: flyway-migration-governance
category: infrastructure
status: current
created: 2026-03-29
migrated_from: docs/flyway-migration-governance.md
tags: [flyway, database, migration, seed, governance]
---

# Flyway 自动迁移与 Seed 治理说明

## 1. 目标
- 统一通过 Flyway 管理数据库结构与系统预置数据，不再手工执行 `db/*.sql`。
- 启动阶段自动迁移，迁移失败即阻断启动，禁止在不一致 schema 下运行。

## 2. 目录与命名
- 结构迁移目录：`magic-boot-master/src/main/resources/db/migration/`
- 生产 seed 目录：`magic-boot-master/src/main/resources/db/seed/prod/`
- 开发 seed 目录：`magic-boot-master/src/main/resources/db/seed/dev/`
- 版本脚本命名：`V{version}__{description}.sql`
- 可重复脚本命名：`R__{description}.sql`

## 3. 配置约定
- 生产默认加载：`classpath:db/migration,classpath:db/seed/prod`
- 开发默认加载：`classpath:db/migration,classpath:db/seed/prod,classpath:db/seed/dev`
- 关键开关：
  - `spring.flyway.enabled=true`
  - `spring.flyway.validate-on-migrate=true`
  - `spring.flyway.baseline-on-migrate=true`

## 4. 字典 Seed 白名单（生产纳管）
仅以下 `dict_type` 纳入生产 seed（与当前 `magic-boot` 库对齐）：
- `common_status`
- `db_type`
- `dict_type`
- `office_type`
- `is_login`
- `gender`

说明：
- 以上属于系统运行依赖字典，缺失会影响菜单、权限、插件与任务状态展示。
- 运营内容型、高频调整字典不纳入生产强制 seed。

## 5. 数据策略（V + R）
- `V`：用于初始化白名单字典类型骨架（不存在则插入）。
- `R`：用于幂等对齐白名单字典项（补齐并更新标准项的文案/排序）。
- 默认非删除策略：不删除非种子字典项，避免误伤业务维护数据。

### 5.1 预定义身份与权限 Seed（生产纳管）
- 纳管表：`sys_user`、`sys_role`、`sys_menu`、`sys_user_role`、`sys_role_menu`。
- `V20260330.002__seed_identity_core.sql`：初始化预定义用户/角色/菜单骨架（不存在才插入）。
- `R__seed_access_matrix_core.sql`：对齐预定义账号-角色、角色-菜单矩阵（仅作用于白名单对象）。
- 白名单对象：
  - 用户：`admin`
  - 角色：`admin`、`default`

## 6. 严格模式清单（可选）
- 默认严格模式清单为空。
- 若后续某些 `dict_type` 需要强一致，可将其加入严格模式清单，并在对应 `R` 脚本中对该类型启用"全量覆盖"策略。

## 7. 发布与回滚原则
- 禁止修改已发布版本的 Flyway 脚本内容。
- 回滚通过新增更高版本修复脚本实现，不直接编辑历史脚本。
- 任何迁移失败均视为发布失败，需修复后重新发布。
