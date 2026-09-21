-- License 授权：防回拨时间戳表（客户业务库）+ 签发留痕表（办公库；客户库建了也无妨）
-- 配套：magic-boot-master org.ssssssss.magicboot.license 包（校验/防回拨）与 issue 包（签发）
-- 幂等：CREATE TABLE IF NOT EXISTS，可重复执行；Flyway 在 dongxinheping 部署关闭，本目录仅作 DDL 存档
-- 红线：t_license_time_mark 内是防回拨关键状态，严禁 DROP/清空/恢复旧备份该表

CREATE TABLE IF NOT EXISTS t_license_time_mark (
    id varchar(16) NOT NULL COMMENT '固定行 default',
    mark_json mediumtext COMMENT '防回拨标记 {v,installId,maxSeen,writeTime,sig}',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='License 防回拨单调时间戳(勿删勿还原)';

CREATE TABLE IF NOT EXISTS t_license_issue_log (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    license_id varchar(64) NOT NULL COMMENT '授权文件内 licenseId',
    customer varchar(128) NOT NULL COMMENT '客户/项目标识',
    expire_at date NOT NULL COMMENT '到期日',
    fingerprints text COMMENT '3 行机器指纹',
    notes varchar(512) DEFAULT NULL COMMENT '备注',
    operator varchar(64) DEFAULT NULL COMMENT '签发人',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签发时间',
    PRIMARY KEY (id),
    KEY idx_customer (customer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='License 签发留痕(办公库)';
