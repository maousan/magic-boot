{
  "properties" : { },
  "id" : "504b4f23f8c146679c1d7240d79ffe16",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "角色列表（含每角色绑定用户数）。需登录且仅 admin 可调（服务端校验，非 admin 返回 403）。配套管理后台「角色管理」页。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

// 角色管理接口均要求 admin（同菜单权限配置，不能只靠前端隐藏）
if (!StpUtil.isLogin()) {
    exit 402, '凭证已过期'
}
var userId = StpUtil.getLoginId() + ''
var adminCount = db.selectValue("""
    select count(1)
    from sys_user_role sur
    join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.code = 'admin' and r.is_del = 0
""", { userId: userId })
if (userId != '1' && !(adminCount > 0)) {
    exit 403, '仅管理员可管理角色'
}

return db.select("""
    select r.id, r.name, r.code, r.create_date,
           (select count(1) from sys_user_role sur where sur.role_id = r.id) as user_count
    from sys_role r
    where r.is_del = 0
    order by r.create_date
""")
