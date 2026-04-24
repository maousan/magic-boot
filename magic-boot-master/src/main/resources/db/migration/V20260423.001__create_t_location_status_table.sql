CREATE TABLE IF NOT EXISTS t_location_status (
  location_code varchar(64) NOT NULL COMMENT '库位号',
  status0_user_count int NOT NULL DEFAULT 0 COMMENT '未完成(status=0)操作用户数',
  status1_user_count int NOT NULL DEFAULT 0 COMMENT '已完成(status=1)操作用户数',
  updated_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  version bigint NOT NULL DEFAULT 1 COMMENT '版本号',
  PRIMARY KEY (location_code),
  KEY idx_t_location_status_status0 (status0_user_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位操作状态真值表';
