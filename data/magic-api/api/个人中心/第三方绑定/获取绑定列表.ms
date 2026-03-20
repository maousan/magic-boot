{
  "properties" : { },
  "id" : "c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8",
  "script" : null,
  "groupId" : "profilebinding001",
  "name" : "获取绑定列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/bindings",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "获取当前用户已绑定的第三方账号列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

var userId = StpUtil.getLoginId()

// 定义支持的平台
var platforms = ['wechat', 'dingtalk', 'feishu', 'github']
var platformNames = {
    wechat: '微信',
    dingtalk: '钉钉',
    feishu: '飞书',
    github: 'GitHub'
}

// 查询已绑定的账号
var bindings = db.select("""
    select platform, nickname, bind_time
    from sys_user_binding
    where user_id = #{userId} and is_del = 0
""")

// 组装返回结果
var result = platforms.map(platform => {
    var binding = bindings.find(b => b.platform == platform)
    return {
        platform: platform,
        platformName: platformNames[platform],
        bound: binding != null,
        nickname: binding?.nickname,
        bindTime: binding?.bind_time
    }
})

return result
