{
  "properties": {},
  "id": "cd054084a75d44b09bab844ebf98c7b1",
  "script": null,
  "groupId": "1713a9b35b73415da26919dd54682bdc",
  "name": "验证token",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/validateToken",
  "method": "GET",
  "parameters": [],
  "options": [
    {
      "name": "require_login",
      "value": "false",
      "description": "该接口需要登录才允许访问",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": "校验 token 并返回当前用户信息（含角色）"
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.json.JSONUtil

if (!StpUtil.isLogin()) {
    exit 402, '凭证已过期'
}
var userId = StpUtil.getLoginId() + ''
var user = db.selectOne("""
    select id, username, name
    from sys_user
    where id = #{userId} and is_del = 0
""", { userId: userId })
if (user == null) {
    StpUtil.logout()
    exit 402, '用户不存在或已删除'
}
var adminCount = db.selectValue("""
    select count(1)
    from sys_user_role sur
    join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.code = 'admin' and r.is_del = 0
""", { userId: userId })

// 2026-10-15 角色管理重构：多角色菜单并集（与 登录.ms 保持同步）
// ---- 旧单角色逻辑（注释保留，回滚取消注释） ----
// var role = (userId == '1' || adminCount > 0) ? 'admin' : 'ops'
// var opsMenus = ['Dashboard','WarehouseLocation','LedMapping','LabelMapping','InventoryQuery','LedDevice','LedColor','ZintisNetty','BatchEpc','RfidServer','PickingUpload','UserLightColor']
// var menuConfigRow = db.selectOne("select menus from t_admin_menu_config where role_code = 'ops'")
// if (menuConfigRow != null && menuConfigRow.menus != null && menuRow.menus != '') {
//     opsMenus = JSONUtil.parseArray(menuConfigRow.menus)
// }

var DEFAULT_OPS_MENUS = ['Dashboard','WarehouseLocation','LedMapping','LabelMapping','InventoryQuery','LedDevice','LedColor','ZintisNetty','BatchEpc','RfidServer','PickingUpload','UserLightColor']

var roleCodeList = []
var roleCodeRows = db.select("""
    select r.code
    from sys_user_role sur
    inner join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.is_del = 0
""", { userId: userId })
if (roleCodeRows != null) {
    for (rcRow in roleCodeRows) {
        if (rcRow.code != null && rcRow.code != '') {
            roleCodeList.add(rcRow.code)
        }
    }
}
if (roleCodeList.size() == 0) {
    roleCodeList.add('ops')
}
var isAdmin = (userId == '1' || roleCodeList.contains('admin'))

var menus = []
var buttons = []
var seenMenuMap = {}
var seenButtonMap = {}
var buttonsAll = false
var roleDisplay = 'admin'
if (isAdmin) {
    menus = ['*']
    buttons = ['*']
} else {
    for (rc in roleCodeList) {
        if (rc == 'admin') { continue }
        var menuRow = db.selectOne("select menus, buttons from t_admin_menu_config where role_code = #{rc}", { rc: rc })
        if (menuRow != null && menuRow.menus != null && menuRow.menus != '') {
            var menuArr = JSONUtil.parseArray(menuRow.menus)
            for (m in menuArr) {
                if (seenMenuMap[m] == null) {
                    seenMenuMap[m] = 1
                    menus.add(m)
                }
            }
        } else if (rc == 'ops') {
            for (m in DEFAULT_OPS_MENUS) {
                if (seenMenuMap[m] == null) {
                    seenMenuMap[m] = 1
                    menus.add(m)
                }
            }
        }
        // 2026-10-15 按钮权限并集：任一绑定角色未配置 buttons（NULL/空）即视为全量，兼容存量角色
        if (menuRow == null || menuRow.buttons == null || menuRow.buttons == '') {
            buttonsAll = true
        } else {
            var btnArr = JSONUtil.parseArray(menuRow.buttons)
            for (b in btnArr) {
                if (seenButtonMap[b] == null) {
                    seenButtonMap[b] = 1
                    buttons.add(b)
                }
            }
        }
    }
    if (menus.size() == 0) {
        menus = ['Dashboard']
    }
    if (buttonsAll) {
        buttons = ['*']
    }
    var firstRoleRow = db.selectOne("""
        select r.name as role_name
        from sys_user_role sur
        inner join sys_role r on r.id = sur.role_id
        where sur.user_id = #{userId} and r.is_del = 0
        order by r.create_date
        limit 1
    """, { userId: userId })
    roleDisplay = '运营'
    if (firstRoleRow != null) {
        let rn = firstRoleRow.roleName
        if (rn == null) { rn = firstRoleRow.role_name }
        if (rn != null && rn != '') { roleDisplay = rn }
    }
}
var role = roleDisplay

return {
    id: user.id,
    username: user.username,
    name: user.name,
    role: role,
    menus: menus,
    buttons: buttons
}
