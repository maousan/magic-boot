{
  "properties": {},
  "id": "40f0ce88379b474dac97416f7d80508b",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "修改密码",
  "createTime": null,
  "updateTime": 1790065933940,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/change-password",
  "method": "POST",
  "parameters": [],
  "options": [],
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": "用户自助修改密码：校验原密码，改后强制重新登录"
}
================================
import cn.dev33.satoken.stp.StpUtil

let oldPassword = body.oldPassword
let newPassword = body.newPassword
if (oldPassword == null || (oldPassword + '').trim() == '') {
    exit 400, '请输入原密码'
}
if (newPassword == null || (newPassword + '').trim() == '') {
    exit 400, '请输入新密码'
}

// 只允许改自己的密码，userId 取自会话而非请求体
var userId = StpUtil.getLoginId() + ''
var user = db.selectOne("""
    select id, username, password
    from sys_user
    where id = #{userId} and is_del = 0
""", { userId: userId })
if (user == null) {
    StpUtil.logout()
    exit 402, '用户不存在或已删除'
}
// 前端传输与库内存储均为 sha256(明文)
if (oldPassword != user.password) {
    exit 400, '原密码不正确'
}
if (newPassword == user.password) {
    exit 400, '新密码不能与原密码相同'
}

db.update("update sys_user set password = #{newPassword}, update_date = now() where id = #{userId}", { userId: userId, newPassword: newPassword })
// 改密后该账号全部会话失效，需用新密码重新登录
StpUtil.logout(userId)
return true
