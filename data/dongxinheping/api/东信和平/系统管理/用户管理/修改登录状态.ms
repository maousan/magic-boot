{
  "properties": {},
  "id": "93b527963434415eb76c27bae9f6b387",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "修改登录状态",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/toggle-login",
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
let isLogin = body.isLogin
if (id == null || (id + '').trim() == '') {
    exit 400, '用户ID不能为空'
}
if (isLogin != 0 && isLogin != 1) {
    exit 400, '状态不合法'
}
id = (id + '').trim()
if (id == '1') {
    exit 400, '内置管理员不允许禁用'
}
if (id == StpUtil.getLoginId() + '') {
    exit 400, '不能禁用当前登录账号'
}
var exists = db.selectValue("select count(1) from sys_user where id = #{id} and is_del = 0", { id: id })
if (exists == 0) {
    exit 400, '用户不存在'
}
db.update("update sys_user set is_login = #{isLogin}, update_date = now() where id = #{id}", { id: id, isLogin: isLogin })
// 禁用后立即踢下线
if (isLogin == 1) {
    StpUtil.logout(id)
}
return true
