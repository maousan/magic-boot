{
  "properties" : { },
  "id" : "g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2",
  "script" : null,
  "groupId" : "profiledevice001",
  "name" : "踢下线设备",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/device/kickout",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"deviceId\": \"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "将指定设备踢下线",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

var currentToken = StpUtil.getTokenValue()

// 验证参数
if(!body.deviceId){
    exit 0, '请指定设备ID'
}

// 不能踢下线当前设备
if(body.deviceId == currentToken){
    exit 0, '不能踢下线当前登录设备'
}

// 踢下线指定设备
StpUtil.logoutByTokenValue(body.deviceId)

return '踢下线成功'
