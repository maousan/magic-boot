{
  "properties": {},
  "id": "e0db9ad6de294f05b36471ca26527459",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "保存",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/save",
  "method": "POST",
  "parameters": [],
  "options": [],
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": ""
}
================================
import cn.dev33.satoken.stp.StpUtil

let username = body.username
let name = body.name
let password = body.password
let role = body.role

if (username == null || (username + '').trim() == '') {
    exit 400, '请输入用户名'
}
if (password == null || (password + '').trim() == '') {
    exit 400, '请输入初始密码'
}
// if (role != 'admin' && role != 'ops') {
//     exit 400, '角色不合法'
// }
// 2026-10-15 改因：角色选项改为读取真实角色列表，按编码校验并绑定对应角色（不再限定 admin/ops）
if (role == null || (role + '').trim() == '') {
    exit 400, '请选择角色'
}
role = (role + '').trim()
var roleRow = db.selectOne("""
    select id from sys_role where code = #{code} and is_del = 0
""", { code: role })
if (roleRow == null) {
    exit 400, '角色不存在: ' + role
}
username = (username + '').trim()

let exists = db.selectValue("""
    select count(1) from sys_user where username = #{username} and is_del = 0
""", { username: username })
if (exists > 0) {
    exit 400, '用户名已存在'
}

// 主键用 MySQL uuid 生成，随后回查用于角色绑定
db.update("""
    insert into sys_user (id, username, name, password, is_login, is_del, create_by, create_date)
    values (replace(uuid(), '-', ''), #{username}, #{name}, #{password}, 0, 0, #{createBy}, now())
""", { username: username, name: name, password: password, createBy: StpUtil.getLoginIdDefaultNull() + '' })

// if (role == 'admin') {
//     var userId = db.selectValue("""
//         select id from sys_user where username = #{username} and is_del = 0
//     """, { username: username })
//     db.update("insert into sys_user_role (user_id, role_id) values (#{userId}, '1')", { userId: userId })
// }
// 2026-10-15 改因：role=ops 也写显式 ops 绑定，用户管理与角色管理成员口径一致
// var userId = db.selectValue("""
//     select id from sys_user where username = #{username} and is_del = 0
// """, { username: username })
// if (role == 'admin') {
//     db.update("insert into sys_user_role (user_id, role_id) values (#{userId}, '1')", { userId: userId })
// } else {
//     db.update("insert into sys_user_role (user_id, role_id) values (#{userId}, '2')", { userId: userId })
// }
// 2026-10-15 改因：按所选角色编码绑定（不再写死内置角色 id）
var userId = db.selectValue("""
    select id from sys_user where username = #{username} and is_del = 0
""", { username: username })
db.update("insert into sys_user_role (user_id, role_id) values (#{userId}, #{roleId})", { userId: userId, roleId: roleRow.id })
return true
