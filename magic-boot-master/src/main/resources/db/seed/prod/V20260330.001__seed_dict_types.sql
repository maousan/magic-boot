-- Seed whitelist dictionary types for production bootstrap.
-- Compatible with the current magic-boot schema in database `magic-boot`.

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_common_status', 'Common Status', 'System Seed Dictionary', 0, 'common_status', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'common_status'
);

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_db_type', 'Database Type', 'System Seed Dictionary', 0, 'db_type', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'db_type'
);

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_dict_type', 'Dictionary Type', 'System Seed Dictionary', 0, 'dict_type', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'dict_type'
);

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_office_type', 'Office Type', 'System Seed Dictionary', 0, 'office_type', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'office_type'
);

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_is_login', 'Login Status', 'System Seed Dictionary', 0, 'is_login', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'is_login'
);

INSERT INTO `sys_dict` (`id`, `name`, `remark`, `dict_type`, `type`, `is_del`, `create_date`, `update_date`)
SELECT 'dict_seed_gender', 'Gender', 'System Seed Dictionary', 1, 'gender', 0, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `type` = 'gender'
);
