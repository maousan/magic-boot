CREATE TABLE IF NOT EXISTS sys_file (
  id varchar(36) PRIMARY KEY,
  storage_key varchar(50) NOT NULL,
  file_path varchar(500) NOT NULL,
  parent_id varchar(36),
  file_name varchar(255) NOT NULL,
  file_type varchar(20) DEFAULT 'FILE',
  file_size bigint DEFAULT 0,
  content_type varchar(100),
  file_ext varchar(20),
  md5 varchar(32),
  url varchar(1000),
  metadata varchar(2000),
  create_time timestamp DEFAULT CURRENT_TIMESTAMP,
  create_by varchar(36),
  update_time timestamp DEFAULT CURRENT_TIMESTAMP,
  is_deleted tinyint DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_storage_parent_name_del
ON sys_file (storage_key, parent_id, file_name, is_deleted);

CREATE INDEX IF NOT EXISTS idx_storage_key ON sys_file (storage_key);
CREATE INDEX IF NOT EXISTS idx_file_path ON sys_file (file_path);
CREATE INDEX IF NOT EXISTS idx_parent ON sys_file (parent_id);
CREATE INDEX IF NOT EXISTS idx_storage_parent ON sys_file (storage_key, parent_id);
CREATE INDEX IF NOT EXISTS idx_file_type ON sys_file (file_type);
CREATE INDEX IF NOT EXISTS idx_md5 ON sys_file (md5);
CREATE INDEX IF NOT EXISTS idx_is_deleted ON sys_file (is_deleted);
