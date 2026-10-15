{
  "properties" : { },
  "id" : "a2041ae7e8554d9b9248d13388c8cd9b",
  "script" : null,
  "groupId" : "0f3dda371580477dac97198d4d0451cd",
  "name" : "成员保存",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/save-members",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\"roleId\":\"<角色id>\",\"userIds\":[\"<用户id>\"]}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "整组覆盖角色的用户绑定（userIds 为该角色的最终成员清单，空数组=清空）。只影响本角色的绑定，不动用户在其他角色的绑定。需登录且仅 admin 可调。",
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

let roleId = body.roleId == null ? '' : (body.roleId + '').trim()
let userIds = body.userIds

if (roleId == '') {
    exit 400, 'roleId不能为空'
}
if (userIds == null) {
    userIds = []
}

var roleExists = db.selectValue("select count(1) from sys_role where id = #{id} and is_del = 0", { id: roleId })
if (roleExists == 0) {
    exit 404, '角色不存在'
}

let cleanUserIds = []
let seen = {}
for(uid in userIds){
    let u = uid == null ? '' : (uid + '').trim()
    if(u == '' || seen[u] != null){
        continue
    }
    seen[u] = 1
    cleanUserIds.add(u)
}

// 整组覆盖：先清本角色绑定，再按清单写入
db.update("delete from sys_user_role where role_id = #{roleId}", { roleId: roleId })
for(uid in cleanUserIds){
    db.update("""
        insert into sys_user_role (user_id, role_id) values (#{userId}, #{roleId})
    """, { userId: uid, roleId: roleId })
}

log.info('role save-members: roleId=' + roleId + ', count=' + cleanUserIds.size() + ', operator=' + userId)

return { success: true, count: cleanUserIds.size() }
