-- Repeatable seed for access matrix.
-- Scope: predefined user-role and role-menu relations only.

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_user_role_core`;
CREATE TEMPORARY TABLE `tmp_seed_user_role_core` (
    `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
);

INSERT INTO `tmp_seed_user_role_core` (`username`, `role_code`) VALUES
('admin', 'admin');

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_role_menu_core`;
CREATE TEMPORARY TABLE `tmp_seed_role_menu_core` (
    `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `menu_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
);

INSERT INTO `tmp_seed_role_menu_core` (`role_code`, `menu_id`) VALUES
('admin', '06b8a427e4cd4c1ba11752070f565f20'),
('admin', '2035766138490843137'),
('admin', '7ad229f12c8f4b57bbb9349e2ffd8932'),
('admin', '8e9455740091486c914495cfb0c7faa5'),
('admin', 'c5f407478c4e4c9cbcdbee6389d2c909'),
('default', '06b8a427e4cd4c1ba11752070f565f20'),
('default', '39be13ef6f0745568c80bf35202ddb2b'),
('default', '6f3594d0-5445-41e1-a13c-890a57485036'),
('default', '7ad229f12c8f4b57bbb9349e2ffd8932'),
('default', '8e9455740091486c914495cfb0c7faa5'),
('default', 'b1851d1b13594e71840103c11a37a669'),
('default', 'c5f407478c4e4c9cbcdbee6389d2c909');

DELETE ur
FROM `sys_user_role` ur
JOIN `sys_user` u ON u.`id` = ur.`user_id`
WHERE u.`username` IN ('admin');

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.`id`, r.`id`
FROM `tmp_seed_user_role_core` s
JOIN `sys_user` u ON u.`username` = s.`username`
JOIN `sys_role` r ON r.`code` = s.`role_code`
LEFT JOIN `sys_user_role` ur ON ur.`user_id` = u.`id` AND ur.`role_id` = r.`id`
WHERE ur.`user_id` IS NULL;

DELETE rm
FROM `sys_role_menu` rm
JOIN `sys_role` r ON r.`id` = rm.`role_id`
WHERE r.`code` IN ('admin', 'default');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.`id`, s.`menu_id`
FROM `tmp_seed_role_menu_core` s
JOIN `sys_role` r ON r.`code` = s.`role_code`
JOIN `sys_menu` m ON m.`id` = s.`menu_id` AND m.`is_del` = 0
LEFT JOIN `sys_role_menu` rm ON rm.`role_id` = r.`id` AND rm.`menu_id` = s.`menu_id`
WHERE rm.`role_id` IS NULL;

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_role_menu_core`;
DROP TEMPORARY TABLE IF EXISTS `tmp_seed_user_role_core`;
