-- 批次EPC绑定 严格1箱1标 开关（sys_configure）
-- key: batch-epc.strict-one-label  值: true=严格1箱1标（同批次仅允许一个活跃EPC，绑定/编辑拦截）/ false=一箱多标（默认）
-- 绑定.ms / 编辑.ms 每次调用读库判断，改值即时生效，无需重启；记录不存在时视为 false
-- 切换：UPDATE sys_configure SET configure_value = 'true', update_date = NOW() WHERE configure_key = 'batch-epc.strict-one-label' AND is_del = 0;

INSERT INTO sys_configure (id, configure_key, configure_value, configure_name, configure_type, is_del, create_by, create_date)
SELECT REPLACE(UUID(), '-', ''), 'batch-epc.strict-one-label', 'false', '批次EPC严格1箱1标（true=同批次仅允许一个活跃EPC）', '0', 0, '1', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_configure WHERE configure_key = 'batch-epc.strict-one-label' AND is_del = 0
);
