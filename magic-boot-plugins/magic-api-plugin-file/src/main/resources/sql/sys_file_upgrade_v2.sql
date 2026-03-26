-- sys_file incremental migration to tree model
-- idempotent: check information_schema before each DDL

-- 1) add parent_id column
SELECT COUNT(1) INTO @col_exists
FROM information_schema.columns
WHERE table_schema = DATABASE()
  AND table_name = 'sys_file'
  AND column_name = 'parent_id';

SET @ddl = IF(
    @col_exists = 0,
    'ALTER TABLE `sys_file` ADD COLUMN `parent_id` varchar(36) DEFAULT NULL COMMENT ''parent node id, root is NULL'' AFTER `file_path`',
    'SELECT ''skip add column parent_id'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) add idx_parent
SELECT COUNT(1) INTO @idx_exists
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name = 'sys_file'
  AND index_name = 'idx_parent';

SET @ddl = IF(
    @idx_exists = 0,
    'ALTER TABLE `sys_file` ADD INDEX `idx_parent` (`parent_id`)',
    'SELECT ''skip add index idx_parent'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) add idx_storage_parent
SELECT COUNT(1) INTO @idx_exists
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name = 'sys_file'
  AND index_name = 'idx_storage_parent';

SET @ddl = IF(
    @idx_exists = 0,
    'ALTER TABLE `sys_file` ADD INDEX `idx_storage_parent` (`storage_key`, `parent_id`)',
    'SELECT ''skip add index idx_storage_parent'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4) add unique index for same-level uniqueness
SELECT COUNT(1) INTO @idx_exists
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name = 'sys_file'
  AND index_name = 'uk_storage_parent_name_del';

SET @ddl = IF(
    @idx_exists = 0,
    'ALTER TABLE `sys_file` ADD UNIQUE INDEX `uk_storage_parent_name_del` (`storage_key`, `parent_id`, `file_name`, `is_deleted`)',
    'SELECT ''skip add unique index uk_storage_parent_name_del'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;