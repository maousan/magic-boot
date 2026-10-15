{
  "properties" : { },
  "id" : "eb3db3fb5086419a8330452776cef99e",
  "script" : null,
  "groupId" : "142df2ab28784e5b8b00145468e6f9be",
  "name" : "保存菜单配置",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\"menus\":[\"Dashboard\",\"LedDevice\",\"LedColor\"]}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "保存 ops 角色可见菜单（路由名数组，须含 Dashboard）。需登录且仅 admin 可调（服务端校验，非 admin 返回 403）。保存后 ops 下次登录或刷新页面生效。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.json.JSONUtil
import log

// 菜单权限配置保存：本接口管理 ops 可见菜单，必须服务端拦角色，不能只靠前端隐藏
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

// 2026-10-15 角色管理配套：按 roleCode 保存（默认 ops；admin 恒全量不允许配置；角色须已存在）
let roleCode = body.roleCode == null ? '' : (body.roleCode + '').trim()
if (roleCode == '') {
    roleCode = 'ops'
}
if (roleCode == 'admin') {
    exit 400, 'admin 角色恒为全量，无需配置菜单'
}
var roleExists = db.selectValue("""
    select count(1) from sys_role where code = #{code} and is_del = 0
""", { code: roleCode })
if (roleExists == 0) {
    exit 400, '角色不存在: ' + roleCode
}

var menus = body.menus
if (menus == null || menus.size() == 0) {
    exit 400, 'menus不能为空'
}
if (!menus.contains('Dashboard')) {
    exit 400, 'menus必须包含Dashboard（默认首页）'
}
for (item in menus) {
    if (item == null || (item + '').trim() == '') {
        exit 400, 'menus包含空项'
    }
}

// 2026-10-15 按钮权限配套：buttons 为 `路由:动作` 权限点数组；null=未配置（视为全量，兼容旧调用方），[]=无操作权限
var buttons = body.buttons
if (buttons != null) {
    for (b in buttons) {
        if (b == null || (b + '').trim() == '') {
            exit 400, 'buttons包含空项'
        }
    }
}
var buttonsJson = buttons == null ? null : JSONUtil.toJsonStr(buttons)

var operatorName = db.selectValue("select name from sys_user where id = #{userId}", { userId: userId })

db.update("""
    insert into t_admin_menu_config (role_code, menus, buttons, update_by)
    values (#{roleCode}, #{menusJson}, #{buttonsJson}, #{updateBy})
    on duplicate key update
        menus = values(menus),
        buttons = values(buttons),
        update_by = values(update_by)
""", { roleCode: roleCode, menusJson: JSONUtil.toJsonStr(menus), buttonsJson: buttonsJson, updateBy: operatorName })

log.info('menu-config save: role=' + roleCode + ', operator=' + operatorName + ', menus=' + menus.size() + ', buttons=' + (buttons == null ? 'null' : buttons.size()))

return {
    success: true,
    message: '保存成功'
}
