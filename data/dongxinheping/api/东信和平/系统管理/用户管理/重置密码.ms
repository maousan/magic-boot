{
  "properties": {},
  "id": "37bbf020e11d41ceba44733542ed9e75",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "重置密码",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/reset-password",
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
let id = body.id
let password = body.password
if (id == null || (id + '').trim() == '') {
    exit 400, '用户ID不能为空'
}
if (password == null || (password + '').trim() == '') {
    exit 400, '请输入新密码'
}
id = (id + '').trim()

var exists = db.selectValue("select count(1) from sys_user where id = #{id} and is_del = 0", { id: id })
if (exists == 0) {
    exit 400, '用户不存在'
}
db.update("update sys_user set password = #{password}, update_date = now() where id = #{id}", { id: id, password: password })
return true
