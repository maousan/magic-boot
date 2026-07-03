-- LED 待重试指令表
-- 采用 per-(mac,color) 最新目标状态覆盖语义（B 模型）：
--   同一 mac + color 的新指令通过 ON DUPLICATE KEY UPDATE 覆盖旧指令，
--   保证设备最终状态 = 用户最后操作，过期指令自动作废。
CREATE TABLE IF NOT EXISTS t_led_pending_command (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    mac_address     VARCHAR(32)  NOT NULL                COMMENT '规范化MAC，大写带冒号',
    color           VARCHAR(16)  NOT NULL DEFAULT 'ALL'  COMMENT '颜色通道 ALL/RED/YELLOW/GREEN',
    command         VARCHAR(8)   NOT NULL                COMMENT '目标状态 ON/OFF',
    frame_hex       VARCHAR(64)  NOT NULL                COMMENT '完整指令帧hex编码',
    remote_address  VARCHAR(64)           DEFAULT NULL   COMMENT '最近一次尝试的远端地址',
    retry_count     INT          NOT NULL DEFAULT 0      COMMENT '已重试次数，仅记录不限制',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mac_color (mac_address, color)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LED待重试指令';
