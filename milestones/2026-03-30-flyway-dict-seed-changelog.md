# 2026-03-30 Flyway 字典 Seed 变更日志

## 变更内容
- 新增生产字典 seed 版本脚本 `V20260330.001__seed_dict_types.sql`，用于白名单字典类型初始化。
- 新增生产身份基线脚本 `V20260330.002__seed_identity_core.sql`，初始化预定义用户/角色/菜单骨架。
- 新增生产字典 repeatable 脚本 `R__seed_dict_items_core.sql`，用于白名单字典项幂等对齐。
- 新增生产权限矩阵脚本 `R__seed_access_matrix_core.sql`，对齐预定义账号-角色、角色-菜单关系。
- 新增开发扩展脚本 `db/seed/dev/R__seed_dict_items_dev.sql`（仅开发环境加载）。
- 更新 Flyway locations：生产默认包含 `db/seed/prod`，开发追加 `db/seed/dev`。
- 更新治理文档，明确字典白名单、非删除策略与严格模式清单约定。
- 按 `magic-boot` 实库结构对齐 seed 字段（`sys_dict.name/remark/dict_type`），并将白名单类型调整为现网核心类型。

## 兼容性
- 仅纳管白名单字典类型，不影响非白名单业务字典。
- 对已有库接入与重启均为幂等执行。

## 验证结果
- `mvn -pl magic-boot-master -am test -DfailIfNoTests=false` 通过。
