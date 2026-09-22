{
  "properties": {},
  "id": "cd054084a75d44b09bab844ebf98c7b1",
  "script": null,
  "groupId": "1713a9b35b73415da26919dd54682bdc",
  "name": "验证token",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/validateToken",
  "method": "GET",
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
  "description": "校验 token 并返回当前用户信息（含角色）"
}
================================
import cn.dev33.satoken.stp.StpUtil

if (!StpUtil.isLogin()) {
    exit 402, '凭证已过期'
}
var userId = StpUtil.getLoginId() + ''
var user = db.selectOne("""
    select id, username, name
    from sys_user
    where id = #{userId} and is_del = 0
""", { userId: userId })
if (user == null) {
    StpUtil.logout()
    exit 402, '用户不存在或已删除'
}
var adminCount = db.selectValue("""
    select count(1)
    from sys_user_role sur
    join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.code = 'admin' and r.is_del = 0
""", { userId: userId })
return {
    id: user.id,
    username: user.username,
    name: user.name,
    role: (userId == '1' || adminCount > 0) ? 'admin' : 'ops'
}
