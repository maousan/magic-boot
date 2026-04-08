# 东信和平拣货上传主从表开发计划

- 新增拣货上传主表：`t_picking_upload`
- 新增拣货上传明细表：`t_picking_upload_detail`
- 改造 `拣货数据上传.ms`：写主表后循环写明细表
- 保持原有入参校验规则