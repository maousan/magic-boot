-- 批次EPC绑定记录增加物料名称列（来源：外箱二维码 MtlDesc 键，parseLot 通用解析直接取）
-- 绑定时写入；编辑不改动物料名称（编辑入参是最终批次ID，无法回溯描述）

ALTER TABLE t_batch_epc ADD COLUMN material_name varchar(500) NULL COMMENT '物料名称（外箱二维码MtlDesc）';
