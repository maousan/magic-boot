-- 角色管理配套：运营(ops)角色入表。此前 ops 是隐式角色（无 sys_role 行，非 admin 即视为运营），
-- 菜单权限页与角色管理页需要它作为可选项；登录逻辑同步改为：无角色绑定的用户仍按运营处理，行为不变。
-- 幂等：仅当不存在 code='ops' 的有效行时插入；Flyway 关闭，本目录仅作 DDL 存档
-- 2026-10-15 已在 192.168.2.90 执行；现场生产库需另行执行
SET NAMES utf8mb4;

INSERT INTO `sys_role` (`id`, `name`, `code`, `is_del`, `create_by`, `create_date`)
SELECT '2', '运营', 'ops', 0, 'seed', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `code` = 'ops' AND `is_del` = 0);
