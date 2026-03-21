{
  "properties" : { },
  "id" : "b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7",
  "script" : null,
  "groupId" : "profilepassword001",
  "name" : "修改密码",
  "createTime" : null,
  "updateTime" : 1774086378312,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/password/update",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"oldPassword\": \"\",\r\n    \"newPassword\": \"\",\r\n    \"confirmPassword\": \"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "修改当前用户的登录密码",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.secure.SaSecureUtil
import cn.dev33.satoken.stp.StpUtil

var userId = StpUtil.getLoginId()

// 验证必填字段
if(!body.oldPassword || !body.newPassword || !body.confirmPassword){
    exit 0, '请填写完整的密码信息'
}

// 验证两次密码一致
if(body.newPassword != body.confirmPassword){
    exit 0, '两次输入的密码不一致'
}

// 确保密码是字符串类型
var newPassword = body.newPassword + ''

// 验证密码长度
if(newPassword.length < 8 || newPassword.length > 32){
    exit 0, '密码长度必须在8-32位之间'
}

// 获取当前密码
var oldPassword = db.selectValue("""
    select password from sys_user where id = #{userId}
""")

if(!oldPassword){
    exit 0, '用户不存在'
}

// 验证旧密码
if(oldPassword != SaSecureUtil.sha256(body.oldPassword)){
    exit 0, '原密码不正确'
}

// 更新密码
return db.table('sys_user').primary('id').update({
    id: userId,
    password: SaSecureUtil.sha256(newPassword)
}, true)
