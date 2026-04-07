-- Repeatable seed for core dictionary items.
-- Strategy: whitelist only, non-destructive for non-seed data.

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_dict_type_core`;
CREATE TEMPORARY TABLE `tmp_seed_dict_type_core` (
    `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `dict_remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `dict_type_value` int NOT NULL
);

INSERT INTO `tmp_seed_dict_type_core` (`dict_type_code`, `dict_name`, `dict_remark`, `dict_type_value`) VALUES
('common_status', 'Common Status', 'System Seed Dictionary', 0),
('db_type', 'Database Type', 'System Seed Dictionary', 0),
('dict_type', 'Dictionary Type', 'System Seed Dictionary', 0),
('office_type', 'Office Type', 'System Seed Dictionary', 0),
('is_login', 'Login Status', 'System Seed Dictionary', 0),
('gender', 'Gender', 'System Seed Dictionary', 1);

UPDATE `sys_dict` d
JOIN `tmp_seed_dict_type_core` t ON t.`dict_type_code` = d.`type`
SET
    d.`name` = t.`dict_name`,
    d.`remark` = t.`dict_remark`,
    d.`dict_type` = t.`dict_type_value`,
    d.`is_del` = 0,
    d.`update_date` = NOW();

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT
    REPLACE(UUID(), '-', ''),
    t.`dict_name`,
    t.`dict_remark`,
    t.`dict_type_value`,
    t.`dict_type_code`,
    0,
    NOW(),
    NOW()
FROM `tmp_seed_dict_type_core` t
LEFT JOIN `sys_dict` d ON d.`type` = t.`dict_type_code`
WHERE d.`id` IS NULL;

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_dict_items_core`;
CREATE TEMPORARY TABLE `tmp_seed_dict_items_core` (
    `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `item_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `item_label` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `sort_no` int NOT NULL
);

INSERT INTO `tmp_seed_dict_items_core` (`dict_type_code`, `item_value`, `item_label`, `sort_no`) VALUES
('common_status', '1', 'Enabled', 0),
('common_status', '0', 'Disabled', 10),
('db_type', 'mysql', 'mysql', 0),
('db_type', 'postgresql', 'postgresql', 10),
('dict_type', '0', 'System', 0),
('dict_type', '1', 'Business', 10),
('office_type', '1', 'Department', 0),
('office_type', '2', 'Company', 10),
('is_login', '0', 'Not Restricted', 0),
('is_login', '1', 'Restricted', 10),
('gender', '1', 'Male', 0),
('gender', '0', 'Female', 10);

UPDATE `sys_dict_items` i
JOIN `sys_dict` d ON d.`id` = i.`dict_id`
JOIN `tmp_seed_dict_items_core` s ON s.`dict_type_code` = d.`type` AND s.`item_value` = i.`value`
SET
    i.`label` = s.`item_label`,
    i.`sort` = s.`sort_no`,
    i.`update_date` = NOW()
WHERE i.`is_del` = 0;

INSERT INTO `sys_dict_items` (`id`, `value`, `label`, `dict_id`, `sort`, `is_del`, `create_date`, `update_date`)
SELECT
    REPLACE(UUID(), '-', ''),
    s.`item_value`,
    s.`item_label`,
    d.`id`,
    s.`sort_no`,
    0,
    NOW(),
    NOW()
FROM `tmp_seed_dict_items_core` s
JOIN `sys_dict` d ON d.`type` = s.`dict_type_code`
LEFT JOIN `sys_dict_items` i ON i.`dict_id` = d.`id` AND i.`value` = s.`item_value` AND i.`is_del` = 0
WHERE i.`id` IS NULL;

DROP TEMPORARY TABLE IF EXISTS `tmp_seed_dict_items_core`;
DROP TEMPORARY TABLE IF EXISTS `tmp_seed_dict_type_core`;
