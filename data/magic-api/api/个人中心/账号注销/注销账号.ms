{
  "properties" : { },
  "id" : "i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4",
  "script" : null,
  "groupId" : "profilecancel001",
  "name" : "注销账号",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/cancel",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"confirm\": \"确认注销\",\r\n    \"reason\": \"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "注销当前用户账号（警告：此操作不可逆）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

var userId = StpUtil.getLoginId()

// 验证确认文本
if(body.confirm != '确认注销'){
    exit 0, '请输入正确的确认文本'
}

// 逻辑删除用户
db.table('sys_user').primary('id').update({
    id: userId,
    is_del: 1
}, true)

// 注销所有会话
StpUtil.logout()

return '账号注销成功'
