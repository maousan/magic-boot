{
  "properties": {},
  "id": "62f5997c8cc945c1b765b63bda82a5c0",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "删除",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/delete",
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
if (id == null || (id + '').trim() == '') {
    exit 400, '用户ID不能为空'
}
id = (id + '').trim()
if (id == '1') {
    exit 400, '内置管理员不允许删除'
}
if (id == StpUtil.getLoginId() + '') {
    exit 400, '不能删除当前登录账号'
}
var exists = db.selectValue("select count(1) from sys_user where id = #{id} and is_del = 0", { id: id })
if (exists == 0) {
    exit 400, '用户不存在'
}
db.update("update sys_user set is_del = 1, update_date = now() where id = #{id}", { id: id })
db.update("delete from sys_user_role where user_id = #{id}", { id: id })
return true
