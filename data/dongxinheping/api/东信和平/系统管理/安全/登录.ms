{
  "properties": {},
  "id": "2e4911aa22134a51bf8770917c740575",
  "script": null,
  "groupId": "1713a9b35b73415da26919dd54682bdc",
  "name": "登录",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/login",
  "method": "POST",
  "parameters": [],
  "options": [
    {
      "name": "require_login",
      "value": "false",
      "description": "该接口需要登录才允许访问",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": "dongxinheping 管理后台登录，返回 Sa-Token 与角色"
}
================================
import cn.dev33.satoken.stp.StpUtil
import log

let username = body.username
let password = body.password
if (username == null || (username + '').trim() == '') {
    exit 400, '请输入用户名'
}
if (password == null || (password + '').trim() == '') {
    exit 400, '请输入密码'
}
username = (username + '').trim()

// 前端传输 sha256(明文)，库中 password 列同样存 sha256(明文)
var user = db.selectOne("""
    select id, username, name, password, is_login
    from sys_user
    where username = #{username} and is_del = 0
""", { username: username })

if (user == null || password != user.password) {
    exit 400, '用户名或密码错误'
}
if (user.isLogin == 1) {
    exit 400, '该账号已被禁用，请联系管理员'
}

StpUtil.login(user.id)
var token = StpUtil.getTokenValue()

var userId = user.id + ''
var adminCount = db.selectValue("""
    select count(1)
    from sys_user_role sur
    join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.code = 'admin' and r.is_del = 0
""", { userId: userId })
// 角色 tag 仅驱动前端固有菜单：admin 或运营；平台超管(id=1)恒为 admin
var role = (userId == '1' || adminCount > 0) ? 'admin' : 'ops'

try {
    db.update("""
        insert into sys_login_log (username, type, token, create_date)
        values (#{username}, 'success', #{token}, now())
    """, { username: username, token: token })
} catch (err) {
    log.warn('登录日志写入失败: {}', err)
}

return {
    token: token,
    username: user.username,
    name: user.name,
    role: role
}
