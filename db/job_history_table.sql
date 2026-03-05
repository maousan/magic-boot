CREATE TABLE magic_job_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id VARCHAR(64) NOT NULL COMMENT '任务ID',
    job_name VARCHAR(128) COMMENT '任务名称',
    job_group VARCHAR(128) COMMENT '任务分组',
    script_path VARCHAR(512) COMMENT '脚本路径',
    
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration BIGINT COMMENT '耗时(毫秒)',
    
    status VARCHAR(16) NOT NULL COMMENT '状态: RUNNING/SUCCESS/FAILED',
    result TEXT COMMENT '执行结果',
    exception_message TEXT COMMENT '异常信息',
    exception_stack TEXT COMMENT '异常堆栈',
    
    trigger_type VARCHAR(16) COMMENT '触发类型: CRON/MANUAL',
    triggered_by VARCHAR(128) COMMENT '触发者',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_job_id (job_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行历史';