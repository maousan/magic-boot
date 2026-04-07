CREATE TABLE magic_job_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id VARCHAR(64) NOT NULL,
    job_name VARCHAR(128),
    job_group VARCHAR(128),
    script_path VARCHAR(512),
    
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration BIGINT,
    
    status VARCHAR(16) NOT NULL,
    result TEXT,
    exception_message TEXT,
    exception_stack TEXT,
    
    trigger_type VARCHAR(16),
    triggered_by VARCHAR(128),
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_job_id (job_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
