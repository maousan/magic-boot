{
  "properties" : { },
  "id" : "57143855dbf049368fdce9dee68351ec",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "修改",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/update",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\"id\":\"<角色id>\",\"name\":\"仓管组长\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "修改角色名称（code 创建后不可改，菜单权限与用户绑定均按 code 关联）。需登录且仅 admin 可调。",
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

let roleId = body.id == null ? '' : (body.id + '').trim()
let name = body.name == null ? '' : (body.name + '').trim()

if (roleId == '') {
    exit 400, '角色ID不能为空'
}
if (name == '') {
    exit 400, '角色名称不能为空'
}

var exists = db.selectValue("select count(1) from sys_role where id = #{id} and is_del = 0", { id: roleId })
if (exists == 0) {
    exit 404, '角色不存在'
}

db.update("""
    update sys_role set name = #{name}, update_by = #{updateBy}, update_date = now()
    where id = #{id}
""", { id: roleId, name: name, updateBy: userId })

return { success: true }
