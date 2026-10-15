{
  "properties" : { },
  "id" : "8861cf4e9f66453baeb3023da9d12573",
  "script" : null,
  "groupId" : "142df2ab28784e5b8b00145468e6f9be",
  "name" : "查询菜单配置",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/detail",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "查询 ops 角色当前可见菜单（路由名数组）。需登录且仅 admin 可调（服务端校验，非 admin 返回 403）。配套管理后台「菜单权限」设置页。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.json.JSONUtil
import log

// 菜单权限配置查询：本接口管理各角色可见菜单，必须服务端拦角色，不能只靠前端隐藏
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
    exit 403, '仅管理员可管理菜单权限'
}

// 2026-10-15 角色管理配套：按 roleCode 查询（默认 ops，admin 恒全量不允许配置）
// let roleCode = roleCode == null ? '' : (roleCode + '').trim()
// 2026-10-15 改因：同名 let 在自身初始化里读到的是遮蔽后的 null，roleCode 永远落默认 'ops'，导致切换角色查询不刷新；返回值 role 原硬编码 'ops' 一并修正
let rc = roleCode == null ? '' : (roleCode + '').trim()
if (rc == '') {
    rc = 'ops'
}
if (rc == 'admin') {
    exit 400, 'admin 角色恒为全量，无需配置菜单'
}

var row = db.selectOne("""
    select menus, buttons, update_by, update_time
    from t_admin_menu_config
    where role_code = #{roleCode}
""", { roleCode: rc })

if (row == null) {
    return { role: rc, menus: null, buttons: null, updateBy: null, updateTime: null }
}

let rowMenus = row.menus
let updateBy = row.updateBy
if(updateBy == null){
    updateBy = row.update_by
}
let updateTime = row.updateTime
if(updateTime == null){
    updateTime = row.update_time
}
// 2026-10-15 按钮权限配套：buttons 未配置（null）= 全量，由前端按注册表全勾展示
var buttons = null
if (row.buttons != null) {
    buttons = JSONUtil.parseArray(row.buttons)
}

return {
    role: rc,
    menus: JSONUtil.parseArray(rowMenus),
    buttons: buttons,
    updateBy: updateBy,
    updateTime: updateTime
}
