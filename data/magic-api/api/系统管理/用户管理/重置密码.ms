{
  "properties" : { },
  "id" : "j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5",
  "script" : null,
  "groupId" : "4f0230049d7e4f39b1e0897cc0f46f9a",
  "name" : "重置密码",
  "createTime" : null,
  "updateTime" : 1773902454592,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/reset-password",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "user:reset",
    "description" : "允许拥有该权限的访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "demo_deny",
    "value" : "true",
    "description" : "是否禁止Demo环境操作",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\r\n    \"userId\": \"用户ID\",\r\n    \"newPassword\": \"新密码（可选，不填则使用默认密码）\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "管理员重置用户密码（默认密码：123456）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.secure.SaSecureUtil
import cn.dev33.satoken.stp.StpUtil

// 验证参数
if(!body.userId){
    exit 0, '用户ID不能为空'
}

// 检查用户是否存在
var user = db.selectOne("""
    select id, username, name from sys_user
    where id = #{body.userId} and is_del = 0
""")

if(!user){
    exit 0, '用户不存在'
}

// 设置新密码（默认密码：123456）
var newPassword = body.newPassword || '123456'

// 确保密码是字符串类型
newPassword = newPassword + ''

// 验证密码长度
if(newPassword.length() < 6 || newPassword.length() > 32){
    exit 0, '密码长度必须在6-32位之间'
}

// 加密密码
var encryptedPassword = SaSecureUtil.sha256(newPassword)

// 更新密码
db.table('sys_user').primary('id').update({
    id: body.userId,
    password: encryptedPassword
}, true)

// 踢下线该用户的所有会话（强制重新登录）
try {
    StpUtil.logout(body.userId)
} catch(e) {
    // 忽略踢下线失败的情况
}

return true
