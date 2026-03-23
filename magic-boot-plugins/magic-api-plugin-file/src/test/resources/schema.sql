-- 测试用文件元数据表（H2 数据库语法）
CREATE TABLE IF NOT EXISTS sys_file (
  id varchar(36) NOT NULL,
  storage_key varchar(50) NOT NULL,
  file_path varchar(500) NOT NULL,
  file_name varchar(255) NOT NULL,
  file_type varchar(20) DEFAULT 'FILE',
  file_size bigint DEFAULT 0,
  content_type varchar(100) DEFAULT NULL,
  file_ext varchar(20) DEFAULT NULL,
  md5 varchar(32) DEFAULT NULL,
  url varchar(1000) DEFAULT NULL,
  metadata clob DEFAULT NULL,
  create_time timestamp DEFAULT CURRENT_TIMESTAMP,
  create_by varchar(36) DEFAULT NULL,
  update_time timestamp DEFAULT CURRENT_TIMESTAMP,
  is_deleted int DEFAULT 0,
  PRIMARY KEY (id)
);

-- 创建索引（H2 语法）
CREATE INDEX IF NOT EXISTS idx_storage_key ON sys_file (storage_key);
CREATE INDEX IF NOT EXISTS idx_file_path ON sys_file (file_path);
CREATE INDEX IF NOT EXISTS idx_file_type ON sys_file (file_type);
CREATE INDEX IF NOT EXISTS idx_md5 ON sys_file (md5);
CREATE INDEX IF NOT EXISTS idx_file_name ON sys_file (file_name);
CREATE INDEX IF NOT EXISTS idx_create_time ON sys_file (create_time);
CREATE INDEX IF NOT EXISTS idx_create_by ON sys_file (create_by);
