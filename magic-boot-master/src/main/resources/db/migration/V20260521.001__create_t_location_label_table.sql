CREATE TABLE IF NOT EXISTS t_location_label (
  id varchar(36) NOT NULL COMMENT '主键',
  location_code varchar(255) NOT NULL COMMENT '库位码',
  label_code varchar(255) NOT NULL COMMENT '标签码',
  bind_time varchar(30) DEFAULT NULL COMMENT '绑定时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_label_code (label_code),
  KEY idx_location_code (location_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位-标签绑定记录';
