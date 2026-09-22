{
  "properties": {},
  "id": "a98f2a54967e45e98e0b57a76587d5ab",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "修改",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/update",
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

let id = body.id
let name = body.name
let role = body.role
if (id == null || (id + '').trim() == '') {
    exit 400, '用户ID不能为空'
}
if (role != 'admin' && role != 'ops') {
    exit 400, '角色不合法'
}
id = (id + '').trim()

var exists = db.selectValue("select count(1) from sys_user where id = #{id} and is_del = 0", { id: id })
if (exists == 0) {
    exit 400, '用户不存在'
}

db.update("""
    update sys_user set name = #{name}, update_by = #{updateBy}, update_date = now()
    where id = #{id}
""", { id: id, name: name, updateBy: StpUtil.getLoginIdDefaultNull() + '' })

if (role == 'admin') {
    var bound = db.selectValue("""
        select count(1) from sys_user_role where user_id = #{id} and role_id = '1'
    """, { id: id })
    if (bound == 0) {
        db.update("insert into sys_user_role (user_id, role_id) values (#{id}, '1')", { id: id })
    }
} else {
    db.update("delete from sys_user_role where user_id = #{id} and role_id = '1'", { id: id })
}
return true
