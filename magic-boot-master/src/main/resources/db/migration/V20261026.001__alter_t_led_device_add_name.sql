-- LED 设备表新增名称字段（管理端可维护，列表支持名称模糊查询）

ALTER TABLE t_led_device ADD COLUMN name varchar(100) NULL COMMENT '设备名称' AFTER mac_address;
