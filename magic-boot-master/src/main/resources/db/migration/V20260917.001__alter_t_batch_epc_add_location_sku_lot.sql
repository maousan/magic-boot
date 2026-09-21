-- 批次EPC绑定：拆分存储 库位号/物料编码/批次号（原仅存拼接后的 batch_id）
-- 配套脚本：data/dongxinheping/api/东信和平/库位/批次EPC绑定/{绑定,查询绑定,列表查询,编辑}.ms
-- 2026-09-17 已在运行库(192.168.2.90)执行；本文件幂等，可重复执行/在其他环境补齐
-- 注意：t_batch_epc 为历史运行库表，新环境若表不存在则自动跳过（建表不在本文件范围）

SET @table_exists = (
    SELECT COUNT(*) FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = 't_batch_epc'
);
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 't_batch_epc' AND column_name = 'location_id'
);
SET @ddl = IF(
    @table_exists = 1 AND @col_exists = 0,
    'ALTER TABLE t_batch_epc
        ADD COLUMN location_id varchar(64) NULL COMMENT ''库位号'' AFTER batch_id,
        ADD COLUMN sku varchar(64) NULL COMMENT ''物料编码(二维码MtlCode)'' AFTER location_id,
        ADD COLUMN lot_no varchar(255) NULL COMMENT ''批次号(二维码Lot)'' AFTER sku',
    'SELECT ''t_batch_epc 不存在或拆分列已存在，跳过'' AS info'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 存量数据回填：batch_id 格式为 locationId_sku_lotNo（locationId、sku 不含下划线，lotNo 可含）
UPDATE t_batch_epc
   SET location_id = SUBSTRING_INDEX(batch_id, '_', 1),
       sku = SUBSTRING_INDEX(SUBSTRING_INDEX(batch_id, '_', 2), '_', -1),
       lot_no = SUBSTRING(batch_id, CHAR_LENGTH(SUBSTRING_INDEX(batch_id, '_', 2)) + 2)
 WHERE location_id IS NULL
   AND batch_id LIKE '%\_%\_%';

-- 存量数据回填：幂等（仅回填 NULL 行），重复执行无副作用
