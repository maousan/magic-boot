# 2026-03-30 Flyway 字典 Seed 纳管实施计划

## 背景
系统运行依赖字典仍缺少统一自动初始化机制。需要将生产必需字典纳入 Flyway seed，并与业务运营字典隔离，避免误覆盖。

## 实施项
1. 在 `db/seed/prod` 新增字典 seed 脚本：
   - `V20260330.001__seed_dict_types.sql`
   - `R__seed_dict_items_core.sql`
2. 在 `db/seed/prod` 新增身份权限 seed 脚本：
   - `V20260330.002__seed_identity_core.sql`
   - `R__seed_access_matrix_core.sql`
3. 在 `application.yml` 与 `application-prod.yml` 中确保生产加载 `db/seed/prod`。
4. 在 `application-dev.yml` 追加 `db/seed/dev`（保留开发演示扩展）。
5. 在治理文档中维护字典白名单与非删除策略。

## 白名单
- common_status
- db_type
- dict_type
- office_type
- is_login
- gender

## 验证
- 执行 `mvn -pl magic-boot-master -am test -DfailIfNoTests=false`
- 验证脚本幂等（重复启动无重复脏数据）
