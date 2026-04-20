-- 亮灯链路可观测日志表（摘要）
CREATE TABLE IF NOT EXISTS t_light_trace_log (
  id VARCHAR(32) NOT NULL COMMENT '主键ID',
  trace_id VARCHAR(64) NOT NULL COMMENT '链路追踪ID',
  api_name VARCHAR(32) NOT NULL COMMENT '接口标识(upload/complete/control)',
  stage VARCHAR(32) NOT NULL COMMENT '阶段(done/error)',
  action_type VARCHAR(32) NULL DEFAULT NULL COMMENT '动作类型(turn_on/turn_off/mix)',
  wave_no VARCHAR(64) NULL DEFAULT NULL COMMENT '波次号',
  user_id VARCHAR(64) NULL DEFAULT NULL COMMENT '用户ID',
  location_codes TEXT NULL COMMENT '库位列表(JSON数组)',
  location_count INT NOT NULL DEFAULT 0 COMMENT '库位数量',
  extra_json LONGTEXT NULL COMMENT '扩展信息(JSON)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_t_light_trace_log_trace_id (trace_id),
  KEY idx_t_light_trace_log_wave_time (wave_no, create_time),
  KEY idx_t_light_trace_log_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='亮灯链路摘要日志表';
