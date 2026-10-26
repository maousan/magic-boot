-- 日志开关配置（sys_configure）：
--   access-log.enable  接口访问日志（magic_access_log，写操作+异常请求），false=关闭
--   error-log.enable   异常错误日志（magic_error_log 落库），false=关闭（响应将不带错误ID）
-- 默认均为 true；配置页（系统管理→系统配置）可直接开关，保存即时生效；记录缺失时视为开启

INSERT INTO sys_configure (id, configure_key, configure_value, configure_name, configure_type, is_del, create_by, create_date)
SELECT REPLACE(UUID(), '-', ''), 'access-log.enable', 'true', '接口访问日志开关（false=关闭 magic_access_log 记录）', '0', 0, '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_configure WHERE configure_key = 'access-log.enable' AND is_del = 0
);

INSERT INTO sys_configure (id, configure_key, configure_value, configure_name, configure_type, is_del, create_by, create_date)
SELECT REPLACE(UUID(), '-', ''), 'error-log.enable', 'true', '异常错误日志开关（false=关闭 magic_error_log 落库）', '0', 0, '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_configure WHERE configure_key = 'error-log.enable' AND is_del = 0
);
