-- 管理后台菜单权限设置页（配套 data/dongxinheping/api/东信和平/系统管理/菜单权限/ 分组）
-- 接口：GET /system/menu-config/detail、POST /system/menu-config/save（均需登录且服务端校验 admin 角色）
-- ops 角色可见路由名清单存 JSON 数组文本；admin 恒全量（登录/validateToken 下发 '*'），不配置
-- 登录.ms / 验证token.ms 读取本表 role_code='ops' 行；行不存在时脚本内保留 12 项默认清单兜底
-- 幂等：CREATE TABLE IF NOT EXISTS + INSERT IGNORE 种子；Flyway 在 dongxinheping 部署关闭，本目录仅作 DDL 存档
-- 2026-10-15 已在 192.168.2.90 执行；现场生产库需另行执行

CREATE TABLE IF NOT EXISTS t_admin_menu_config (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_code varchar(32) NOT NULL COMMENT '角色码（当前仅 ops）',
    menus text NOT NULL COMMENT '可见路由名 JSON 数组',
    update_by varchar(64) DEFAULT NULL COMMENT '最后修改人',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理后台菜单权限配置(东信和平)';

INSERT IGNORE INTO t_admin_menu_config (role_code, menus, update_by)
VALUES ('ops', '["Dashboard","WarehouseLocation","LedMapping","LabelMapping","InventoryQuery","LedDevice","LedColor","ZintisNetty","BatchEpc","RfidServer","PickingUpload","UserLightColor"]', 'seed');
