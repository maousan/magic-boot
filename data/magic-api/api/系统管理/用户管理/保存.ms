{
  "properties" : { },
  "id" : "877918736c764253a85d0780cbd5f763",
  "script" : null,
  "groupId" : "4f0230049d7e4f39b1e0897cc0f46f9a",
  "name" : "保存",
  "createTime" : null,
  "updateTime" : 1772967494763,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "user:save",
    "description" : "允许拥有该权限的访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ ]
  },
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.secure.SaSecureUtil;
import org.ssssssss.magicapi.modules.db.cache.SqlCache

var user = body

let userId = user.id

if(userId){
    SqlCache.delete(`permissions:${id}`)
    // db.update("""
    //     delete from sys_user_role where user_id = #{id}
    // """)
    anyline.deletes("sys_user_role", "user_id", userId)
    anyline.update("sys_user", user)
} else {
    user.password = user.password ? SaSecureUtil.sha256(user.password) : ''
    const dataRow = anyline.insert("sys_user", user, true)

    userId = dataRow.getString("id")
}
if (user.roles) {
    for(roleId in user.roles.split(',')){
        anyline.insert("sys_user_role", {userId, roleId})
        // db.table("sys_user_role").column('user_id', id).column("role_id", roleId).insert()
    }
}