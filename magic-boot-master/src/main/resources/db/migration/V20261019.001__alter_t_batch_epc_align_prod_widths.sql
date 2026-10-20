-- 2026-10-19 生产 t_batch_epc.epc 列过窄导致绑定报 Data too long for column 'epc'
-- 该表历来直接在运行库管理（仓库无建表 DDL），测试库 2026-09 已是下列宽度，生产未同步
-- 在生产库执行（无需重启）；测试库已符合，重复执行无害
SET NAMES utf8mb4;

ALTER TABLE t_batch_epc
  MODIFY COLUMN epc varchar(128) NOT NULL COMMENT 'RFID EPC',
  MODIFY COLUMN batch_id varchar(128) NOT NULL,
  MODIFY COLUMN location_id varchar(64) NULL,
  MODIFY COLUMN sku varchar(64) NULL,
  MODIFY COLUMN lot_no varchar(255) NULL;
