{
  "properties" : { },
  "id" : "e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
  "script" : null,
  "groupId" : "profilebinding001",
  "name" : "解绑账号",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/unbind",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"platform\": \"wechat\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "解绑指定的第三方平台账号",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

var userId = StpUtil.getLoginId()

// 验证参数
if(!body.platform){
    exit 0, '请指定要解绑的平台'
}

// 查询绑定记录
var binding = db.selectOne("""
    select id from sys_user_binding
    where user_id = #{userId} and platform = #{body.platform} and is_del = 0
""")

if(!binding){
    exit 0, '该账号未绑定'
}

// 逻辑删除绑定记录
return db.table('sys_user_binding').primary('id').update({
    id: binding.id,
    is_del: 1
}, true)
