-- Extend magic_plugin with package governance columns.
SET @table_exists := (
    SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin'
);

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'package_type') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN package_type varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER provider',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'package_checksum') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN package_checksum varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER package_type',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'manifest_version') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN manifest_version varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER package_checksum',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'manifest_json') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN manifest_json longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL AFTER manifest_version',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'requires_magic_boot') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN requires_magic_boot varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER manifest_json',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'permissions') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN permissions text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL AFTER requires_magic_boot',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'install_source') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN install_source varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER permissions',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1 AND (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'magic_plugin' AND COLUMN_NAME = 'install_time') = 0,
    'ALTER TABLE magic_plugin ADD COLUMN install_time datetime NULL DEFAULT NULL AFTER install_source',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
