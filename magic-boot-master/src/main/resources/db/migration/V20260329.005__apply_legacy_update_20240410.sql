-- Legacy update script from db/update20240410.sql with defensive guards.
-- This migration is no-op when target table/columns are absent or already migrated.
SET @table_exists := (
    SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dynamic_component'
);

SET @col_type_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dynamic_component' AND COLUMN_NAME = 'type'
);
SET @sql := IF(@table_exists = 1 AND @col_type_exists = 0,
    'ALTER TABLE sys_dynamic_component ADD COLUMN type INT(11) COMMENT "0:group,1:vue-component" AFTER compile_css',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_remark_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_dynamic_component' AND COLUMN_NAME = 'remark'
);
SET @sql := IF(@table_exists = 1 AND @col_remark_exists = 0,
    'ALTER TABLE sys_dynamic_component ADD COLUMN remark TEXT COMMENT "remark" AFTER type',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(@table_exists = 1,
    'UPDATE sys_dynamic_component SET type = 0 WHERE id IN (SELECT pid FROM (SELECT pid FROM sys_dynamic_component WHERE is_del = 0) a) AND is_del = 0',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
