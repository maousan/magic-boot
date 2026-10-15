{
  "properties" : { },
  "id" : "83acbd071b2542dd9f519a1b211a57c4",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "删除",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/delete",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\"id\":\"<角色id>\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "删除角色（软删）。内置管理员(id=1)不可删；仍有用户绑定时拒绝，需先在成员管理移除；同时清理该角色的菜单权限配置行。需登录且仅 admin 可调。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import log

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
if (roleId == '') {
    exit 400, '角色ID不能为空'
}
if (roleId == '1') {
    exit 400, '内置管理员角色不可删除'
}

var role = db.selectOne("select id, code from sys_role where id = #{id} and is_del = 0", { id: roleId })
if (role == null) {
    exit 404, '角色不存在'
}

var boundUsers = db.selectValue("select count(1) from sys_user_role where role_id = #{id}", { id: roleId })
if (boundUsers > 0) {
    exit 400, '该角色下仍有 ' + boundUsers + ' 个用户，请先在成员管理中移除'
}

db.update("""
    update sys_role set is_del = 1, update_by = #{updateBy}, update_date = now()
    where id = #{id}
""", { id: roleId, updateBy: userId })

if (role.code != null && role.code != '') {
    db.update("delete from t_admin_menu_config where role_code = #{code}", { code: role.code })
}

log.info('role delete: id=' + roleId + ', code=' + role.code + ', operator=' + userId)

return { success: true }
