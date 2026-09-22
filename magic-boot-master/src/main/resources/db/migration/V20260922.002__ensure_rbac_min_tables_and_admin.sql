-- dongxinheping 管理后台最小 RBAC：确保 sys_user / sys_role / sys_user_role 及日志表存在，并种子内置管理员
-- 幂等：已存在的表/行不改动；现场库若已有 admin 账号则沿用其密码
-- Flyway 已关闭，本文件为部署时手工执行的 DDL 存档

CREATE TABLE IF NOT EXISTS `sys_user`  (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `username` varchar(255) NOT NULL COMMENT '登录名',
  `name` varchar(255) NULL DEFAULT NULL COMMENT '姓名/昵称',
  `password` varchar(255) NULL DEFAULT NULL COMMENT '密码(sha256)',
  `status` char(1) NULL DEFAULT NULL COMMENT '状态',
  `phone` varchar(255) NULL DEFAULT NULL COMMENT '手机号',
  `head_portrait` varchar(1000) NULL DEFAULT NULL COMMENT '头像',
  `is_login` int(1) NULL DEFAULT 0 COMMENT '禁止登录：0未禁用，1已禁用',
  `sort` int(12) NULL DEFAULT NULL COMMENT '排序',
  `office_id` varchar(36) NULL DEFAULT NULL COMMENT '组织机构id',
  `is_del` int(1) NULL DEFAULT 0 COMMENT '删除标识：0未删除，1已删除',
  `create_by` varchar(36) NULL DEFAULT NULL COMMENT '创建人',
  `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(36) NULL DEFAULT NULL COMMENT '更新人',
  `update_date` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

CREATE TABLE IF NOT EXISTS `sys_role`  (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `type` varchar(1) NULL DEFAULT NULL COMMENT '类型',
  `name` varchar(255) NULL DEFAULT NULL COMMENT '角色名称',
  `code` varchar(255) NULL DEFAULT NULL COMMENT '角色编码',
  `permission` varchar(255) NULL DEFAULT NULL COMMENT '0：全部，1：自定义，2：本级级子级，3：本级',
  `sort` int(12) NULL DEFAULT NULL COMMENT '排序',
  `desc_ribe` varchar(1000) NULL DEFAULT NULL COMMENT '描述',
  `is_del` int(1) NULL DEFAULT 0 COMMENT '是否删除：0未删除，1已删除',
  `create_by` varchar(36) NULL DEFAULT NULL COMMENT '创建人',
  `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(36) NULL DEFAULT NULL COMMENT '更新人',
  `update_date` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表';

CREATE TABLE IF NOT EXISTS `sys_user_role`  (
  `user_id` varchar(36) NULL DEFAULT NULL COMMENT '用户id',
  `role_id` varchar(36) NULL DEFAULT NULL COMMENT '角色id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表';

CREATE TABLE IF NOT EXISTS `sys_login_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) NULL DEFAULT NULL COMMENT '登录账号',
  `fail_password` varchar(255) NULL DEFAULT NULL COMMENT '失败密码',
  `type` varchar(255) NULL DEFAULT NULL COMMENT '登录类型（成功、失败）',
  `browser` varchar(255) NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(255) NULL DEFAULT NULL COMMENT '操作系统',
  `address` varchar(255) NULL DEFAULT NULL COMMENT '地理位置',
  `ip` varchar(255) NULL DEFAULT NULL COMMENT 'ip地址',
  `token` varchar(255) NULL DEFAULT NULL COMMENT 'token',
  `create_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '登录日志表';

CREATE TABLE IF NOT EXISTS `sys_oper_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_name` varchar(255) NULL DEFAULT NULL COMMENT '接口名',
  `api_path` varchar(4096) NULL DEFAULT NULL COMMENT '路径',
  `api_method` varchar(32) NULL DEFAULT NULL COMMENT '方法',
  `cost_time` int(6) NULL DEFAULT NULL COMMENT '耗时',
  `create_by` varchar(36) NULL DEFAULT NULL COMMENT '操作人',
  `create_date` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `user_agent` varchar(255) NULL DEFAULT NULL COMMENT '用户代理',
  `user_ip` varchar(255) NULL DEFAULT NULL COMMENT '用户IP',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志';

-- 种子：内置管理员角色（id=1, code=admin，与平台初始数据一致）
-- 仅写核心列，兼容新老 sys_role 结构
INSERT INTO `sys_role` (`id`, `name`, `code`, `is_del`)
SELECT '1', '管理员', 'admin', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `id` = '1');

-- 种子：内置管理员 admin，初始密码 12345678（sha256），首次登录后请在用户管理中重置
INSERT INTO `sys_user` (`id`, `username`, `name`, `password`, `status`, `is_login`, `is_del`, `create_date`)
SELECT '1', 'admin', '管理员', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '1', 0, 0, NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user` WHERE `id` = '1' OR `username` = 'admin');

-- 种子：admin 用户绑定管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT '1', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_user_role` WHERE `user_id` = '1' AND `role_id` = '1');
