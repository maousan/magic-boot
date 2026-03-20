{
  "properties" : { },
  "id" : "d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9",
  "script" : null,
  "groupId" : "profilebinding001",
  "name" : "绑定账号",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/bind",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"platform\": \"wechat\",\r\n    \"authCode\": \"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "绑定指定的第三方平台账号",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.IdUtil

var userId = StpUtil.getLoginId()

// 验证平台类型
var validPlatforms = ['wechat', 'dingtalk', 'feishu', 'github']
if(!body.platform || !validPlatforms.includes(body.platform)){
    exit 0, '不支持的平台类型'
}

// 检查是否已绑定
var existing = db.selectOne("""
    select id from sys_user_binding
    where user_id = #{userId} and platform = #{body.platform} and is_del = 0
""")

if(existing){
    exit 0, '该平台已绑定'
}

// TODO: 调用第三方 OAuth API 获取用户信息
// 当前为占位符实现，实际需要集成真实的 OAuth2 流程
var platformUserId = 'PLATFORM_USER_' + IdUtil.simpleUUID()
var nickname = 'ThirdParty User'

// 保存绑定记录
return db.table('sys_user_binding').primary('id').save({
    id: IdUtil.simpleUUID(),
    user_id: userId,
    platform: body.platform,
    platform_user_id: platformUserId,
    nickname: nickname,
    bind_time: new Date(),
    is_del: 0
})
