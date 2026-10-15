-- 2026-10-15 按钮权限：t_admin_menu_config 增加 buttons 列
-- 存 `路由:动作` 权限点数组的 JSON 文本；NULL = 未配置（视为全量，兼容存量角色），[] = 无操作权限（只读）
-- 执行需带 utf8mb4：mysql --default-character-set=utf8mb4 ... < 本文件
SET NAMES utf8mb4;

ALTER TABLE t_admin_menu_config
    ADD COLUMN buttons text NULL COMMENT '按钮权限点数组JSON（路由:动作），NULL=全量' AFTER menus;
