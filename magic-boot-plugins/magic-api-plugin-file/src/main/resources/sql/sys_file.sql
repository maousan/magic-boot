-- 文件元数据表
-- 用于存储上传文件的元数据信息，支持文件搜索、历史记录和去重功能
-- 对应实体类: org.ssssssss.magicapi.file.model.FileInfo

DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
    `id` varchar(36) NOT NULL COMMENT '主键',
    `storage_key` varchar(50) NOT NULL COMMENT '存储平台标识(local/minio/aliyun/tencent等)',
    `file_path` varchar(500) NOT NULL COMMENT '文件存储路径',
    `file_name` varchar(255) NOT NULL COMMENT '原始文件名',
    `file_type` varchar(20) DEFAULT 'FILE' COMMENT '类型(FILE-文件/DIR-目录)',
    `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
    `content_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
    `file_ext` varchar(20) DEFAULT NULL COMMENT '文件扩展名(不含.)',
    `md5` varchar(32) DEFAULT NULL COMMENT 'MD5哈希(用于去重)',
    `url` varchar(1000) DEFAULT NULL COMMENT '访问URL',
    `metadata` json DEFAULT NULL COMMENT '扩展元数据(JSON格式)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(36) DEFAULT NULL COMMENT '创建人ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '删除标识(0-正常 1-已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_storage_path` (`storage_key`, `file_path`(191)),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_md5` (`md5`),
    KEY `idx_file_name` (`file_name`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件元数据表';
