-- 日志保留天数配置（sys_configure），分别控制两个清理定时任务：
--   access-log.retention-days  访问日志清理任务（job/访问日志/清理过期日志.ms，每天 03:30 清 magic_access_log）
--   error-log.retention-days   错误日志清理任务（job/错误日志/清理过期日志.ms，每天 03:40 清 magic_error_log）
-- 默认均 90 天；设为 0 = 停用对应自动清理（手动清理接口不受影响，/api/system/configure/clean-logs）

INSERT INTO sys_configure (id, configure_key, configure_value, configure_name, configure_type, is_del, create_by, create_date)
SELECT REPLACE(UUID(), '-', ''), 'access-log.retention-days', '90', '访问日志保留天数（天，0=停用自动清理）', '0', 0, '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_configure WHERE configure_key = 'access-log.retention-days' AND is_del = 0
);

INSERT INTO sys_configure (id, configure_key, configure_value, configure_name, configure_type, is_del, create_by, create_date)
SELECT REPLACE(UUID(), '-', ''), 'error-log.retention-days', '90', '错误日志保留天数（天，0=停用自动清理）', '0', 0, '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_configure WHERE configure_key = 'error-log.retention-days' AND is_del = 0
);
