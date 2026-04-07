-- migration note
-- migration note
-- migration note

DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
`id` varchar(36) NOT NULL,
`storage_key` varchar(50) NOT NULL,
`file_path` varchar(500) NOT NULL,
`file_name` varchar(255) NOT NULL,
`file_type` varchar(20) DEFAULT 'FILE',
`file_size` bigint DEFAULT 0,
`content_type` varchar(100) DEFAULT NULL,
`file_ext` varchar(20) DEFAULT NULL,
`md5` varchar(32) DEFAULT NULL,
`url` varchar(1000) DEFAULT NULL,
`metadata` json DEFAULT NULL,
`create_time` datetime DEFAULT CURRENT_TIMESTAMP,
`create_by` varchar(36) DEFAULT NULL,
`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`is_deleted` tinyint(1) DEFAULT 0,
PRIMARY KEY (`id`),
KEY `idx_storage_path` (`storage_key`, `file_path`(191)),
KEY `idx_file_type` (`file_type`),
KEY `idx_md5` (`md5`),
KEY `idx_file_name` (`file_name`),
KEY `idx_create_time` (`create_time`),
KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
