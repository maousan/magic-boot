{
  "properties" : { },
  "id" : "f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1",
  "script" : null,
  "groupId" : "profiledevice001",
  "name" : "获取设备列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/devices",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "获取当前用户所有登录设备的信息",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.http.useragent.UserAgentUtil

var userId = StpUtil.getLoginId()
var currentToken = StpUtil.getTokenValue()

// 获取当前用户所有活跃的 tokens
var tokens = StpUtil.searchTokenValue("", 0, -1, true)
    .filter(it => it.startsWith('Authorization:login:token:'))
    .map(it => it.replace('Authorization:login:token:', ''))

// 如果没有活跃会话，返回空数组
if(!tokens || tokens.length == 0){
    return []
}

// 查询登录日志获取设备信息
var devices = db.select("""
    select
        token,
        ip,
        browser,
        os,
        address,
        create_date as loginTime
    from sys_login_log
    where username = (select username from sys_user where id = #{userId})
    and type = '成功'
    and token in (
        <foreach collection="tokens" item="token" separator=",">
            #{token}
        </foreach>
    )
    order by create_date desc
""")

// 组装返回结果
var result = devices.map(device => {
    var ua = UserAgentUtil.parse(device.browser + ' ' + device.os)
    var deviceType = 'pc'
    if(ua.isMobile){
        deviceType = 'mobile'
    }else if(ua.isTablet){
        deviceType = 'tablet'
    }
    return {
        id: device.token,
        deviceName: device.browser + ' on ' + device.os,
        deviceType: deviceType,
        browser: device.browser,
        os: device.os,
        ip: device.ip,
        address: device.address,
        loginTime: device.loginTime,
        lastActiveTime: device.loginTime,
        isCurrent: device.token == currentToken
    }
})

return result
