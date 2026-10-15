{
  "properties" : { },
  "id" : "eb307617f9134a2db94d2cd5cb96a912",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "新增",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/create",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\"name\":\"仓管\",\"code\":\"warehouse\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "新增角色（name + code，code 全局唯一且不可为 admin）。需登录且仅 admin 可调。角色创建后需在「菜单权限」页配置可见菜单。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.IdUtil

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

let name = body.name == null ? '' : (body.name + '').trim()
let code = body.code == null ? '' : (body.code + '').trim()

if (name == '') {
    exit 400, '角色名称不能为空'
}
if (code == '') {
    exit 400, '角色编码不能为空'
}
if (!(code).matches('[a-z0-9_]{2,32}')) {
    exit 400, '角色编码仅允许小写字母/数字/下划线，长度2-32'
}
if (code == 'admin') {
    exit 400, 'admin 为保留编码，不允许使用'
}

var exists = db.selectValue("""
    select count(1) from sys_role where code = #{code} and is_del = 0
""", { code: code })
if (exists > 0) {
    exit 400, '角色编码已存在: ' + code
}

let roleId = IdUtil.fastSimpleUUID()
db.update("""
    insert into sys_role (id, name, code, is_del, create_by, create_date)
    values (#{id}, #{name}, #{code}, 0, #{createBy}, now())
""", { id: roleId, name: name, code: code, createBy: userId })

return { success: true, id: roleId }
