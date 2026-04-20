# 2026-04-09 Seed 目录重建（基于开发库数据）

## 变更摘要

- 清空 `magic-boot-master/src/main/resources/db/seed` 下旧种子脚本。
- 重新生成生产初始种子：
  - `magic-boot-master/src/main/resources/db/seed/prod/V20260409.001__seed_initial_data_from_dev.sql`
- 保留开发环境占位脚本：
  - `magic-boot-master/src/main/resources/db/seed/dev/R__seed_dev_placeholder.sql`

## 数据来源

- 数据源：开发数据库 `192.168.2.90 / magic-boot`
- 导出策略：`mysqldump --no-create-info --replace --complete-insert`（幂等导入）

## 本次导出表

- `sys_dict`
- `sys_dict_items`
- `sys_menu`
- `sys_role`
- `sys_role_menu`
- `sys_user`
- `sys_user_role`
- `sys_office`
- `sys_permission_code`
- `sys_user_code`
- `sys_user_binding`
- `sys_dynamic_component`
- `sys_configure`
- `sys_database`
- `sys_gen_info`
- `magic_plugin`
- `magic_plugin_config`
