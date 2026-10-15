{
  "properties": {},
  "id": "2e4911aa22134a51bf8770917c740575",
  "script": null,
  "groupId": "1713a9b35b73415da26919dd54682bdc",
  "name": "登录",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/login",
  "method": "POST",
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
  "description": "dongxinheping 管理后台登录，返回 Sa-Token 与角色"
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.json.JSONUtil
import log

let username = body.username
let password = body.password
if (username == null || (username + '').trim() == '') {
    exit 400, '请输入用户名'
}
if (password == null || (password + '').trim() == '') {
    exit 400, '请输入密码'
}
username = (username + '').trim()

// 前端传输 sha256(明文)，库中 password 列同样存 sha256(明文)
var user = db.selectOne("""
    select id, username, name, password, is_login
    from sys_user
    where username = #{username} and is_del = 0
""", { username: username })

if (user == null || password != user.password) {
    exit 400, '用户名或密码错误'
}
if (user.isLogin == 1) {
    exit 400, '该账号已被禁用，请联系管理员'
}

StpUtil.login(user.id)
var token = StpUtil.getTokenValue()

var userId = user.id + ''
var adminCount = db.selectValue("""
    select count(1)
    from sys_user_role sur
    join sys_role r on r.id = sur.role_id
    where sur.user_id = #{userId} and r.code = 'admin' and r.is_del = 0
""", { userId: userId })
// 2026-10-15 角色管理重构：多角色菜单并集——绑定 admin 角色（含平台超管 id=1）全量；
// 否则取用户所绑角色的菜单并集（无绑定视为运营 ops；并集为空仅保 Dashboard）。
// 显示名取第一个绑定角色名（兜底"运营"）；须与 验证token.ms 保持同步
// ---- 旧单角色逻辑（注释保留，回滚取消注释） ----
// var role = (userId == '1' || adminCount > 0) ? 'admin' : 'ops'
// var opsMenus = ['Dashboard','WarehouseLocation','LedMapping','LabelMapping','InventoryQuery','LedDevice','LedColor','ZintisNetty','BatchEpc','RfidServer','PickingUpload','UserLightColor']
// var menuConfigRow = db.selectOne("select menus from t_admin_menu_config where role_code = 'ops'")
// if (menuConfigRow != null && menuConfigRow.menus != null && menuConfigRow.menus != '') {
//     opsMenus = JSONUtil.parseArray(menuConfigRow.menus)
// }
// var menus = role == 'admin' ? ['*'] : opsMenus

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
            // 运营角色无配置行时（如 DDL 未执行）用默认清单兜底，避免登录后无菜单可用
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

try {
    db.update("""
        insert into sys_login_log (username, type, token, create_date)
        values (#{username}, 'success', #{token}, now())
    """, { username: username, token: token })
} catch (err) {
    log.warn('登录日志写入失败: {}', err)
}

return {
    token: token,
    username: user.username,
    name: user.name,
    role: role,
    menus: menus,
    buttons: buttons
}
