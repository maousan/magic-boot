-- Seed baseline data for user/role/menu core tables.
-- Scope: sys_user, sys_role, sys_menu.

INSERT INTO `sys_role` (`id`, `readonly`, `name`, `code`, `status`, `scope`, `sort`, `remark`, `is_del`, `create_date`, `update_date`)
SELECT '1', 1, 'Administrator', 'admin', 1, 0, 0, NULL, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `code` = 'admin');

INSERT INTO `sys_role` (`id`, `readonly`, `name`, `code`, `status`, `scope`, `sort`, `remark`, `is_del`, `create_date`, `update_date`)
SELECT '3a408f5157c841ea8884ade4fa56d0f4', 0, 'Default Access', 'default', 1, 1, 1, NULL, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `code` = 'default');

INSERT INTO `sys_user` (`id`, `username`, `name`, `password`, `status`, `phone`, `email`, `gender`, `is_login`, `sort`, `is_del`, `create_date`, `update_date`, `introduction`)
SELECT '1', 'admin', 'Administrator', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 1, '13888888888', 'dehang@corner-s.com', 0, 0, 0, 0, NOW(), NOW(), '12323244'
WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `username` = 'admin');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT 'b1851d1b13594e71840103c11a37a669', '0', 'lucide:columns-settings', 'System Settings', NULL, 'system_settings', '/system', NULL, NULL, 10, NULL, 'catalog', 0, 1, 0, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 'b1851d1b13594e71840103c11a37a669');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT '39be13ef6f0745568c80bf35202ddb2b', 'b1851d1b13594e71840103c11a37a669', 'lucide:menu', 'Menu Management', NULL, 'menu_management', '/system/menu', '/system/menu/list', NULL, 10, '63227c407c5d40f98df2486390a6f841', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = '39be13ef6f0745568c80bf35202ddb2b');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT '6f3594d0-5445-41e1-a13c-890a57485036', 'b1851d1b13594e71840103c11a37a669', 'lucide:network', 'Organization', NULL, NULL, '/system/dept', '/system/dept/list', NULL, 20, 'fe533026-d929-11ee-9675-c2b02ed3977b', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = '6f3594d0-5445-41e1-a13c-890a57485036');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT '8e9455740091486c914495cfb0c7faa5', 'b1851d1b13594e71840103c11a37a669', 'lucide:shield-user', 'Role Management', NULL, NULL, '/system/role', '/system/role/list', NULL, 40, '8c426b7d397b4ffeb4c9f89b4c318fbb', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = '8e9455740091486c914495cfb0c7faa5');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT '06b8a427e4cd4c1ba11752070f565f20', 'b1851d1b13594e71840103c11a37a669', 'lucide:user', 'User Management', NULL, NULL, '/system/user', '/system/user/list', NULL, 50, 'bc38eb5038ce44aab4bd586fb32d79bb', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = '06b8a427e4cd4c1ba11752070f565f20');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT 'c5f407478c4e4c9cbcdbee6389d2c909', 'b1851d1b13594e71840103c11a37a669', 'lucide:book-search', 'Dictionary', NULL, NULL, '/system/dict', '/system/dict/list', NULL, 60, '28e3f82164de4bbf98a342c2a94da076', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 'c5f407478c4e4c9cbcdbee6389d2c909');

INSERT INTO `sys_menu` (`id`, `pid`, `icon`, `title`, `description`, `name`, `path`, `component`, `permission`, `sort`, `component_id`, `type`, `keep_alive`, `status`, `readonly`, `is_del`, `create_date`, `update_date`)
SELECT '7ad229f12c8f4b57bbb9349e2ffd8932', 'b1851d1b13594e71840103c11a37a669', 'lucide:settings-2', 'Config Center', NULL, NULL, '/system/configure', '/system/configure/list', NULL, 110, 'b94688b24b724a50945b55899d6199bc', 'menu', 1, 1, 1, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = '7ad229f12c8f4b57bbb9349e2ffd8932');
