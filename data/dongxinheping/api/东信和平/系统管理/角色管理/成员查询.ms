{
  "properties" : { },
  "id" : "1c0e422a70d1419a8886eaa81d758f7c",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "成员查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/members",
  "method" : "GET",
  "parameters" : [ {
    "name" : "roleId",
    "value" : "",
    "description" : "角色ID（必填）",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "查询角色下绑定的用户列表。需登录且仅 admin 可调。",
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

// 2026-10-15 修复：roleId 已在接口参数声明，局部 let 同名会遮蔽注入参数（读到 null），
// 局部变量改用 rid 引用参数本体
let rid = roleId == null ? '' : (roleId + '').trim()
if (rid == '') {
    exit 400, 'roleId不能为空'
}

return db.select("""
    select u.id, u.username, u.name
    from sys_user u
    inner join sys_user_role sur on sur.user_id = u.id
    where sur.role_id = #{roleId} and u.is_del = 0
    order by u.username
""", { roleId: rid })
