{
  "properties" : { },
  "id" : "f9a5956afdfd4492966b1a3c04dbadf6",
  "script" : null,
  "groupId" : "89130d496f6f467c88b22ae4a7f688eb",
  "name" : "保存",
  "createTime" : null,
  "updateTime" : 1772965237159,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "role:save",
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
import '@get:/system/role/cache/delete' as cacheDelete

var role = body

const menus = role.menus

const offices = role.offices

role.put("menus", null)
role.put("offices", null)

let roleId = role.id

const state = anyline.beginTrans()

try {
    if(roleId){
        //删除角色菜单
        anyline.deletes("sys_role_menu", "role_id", roleId)
        //更新角色信息
        anyline.update("sys_role", role);
    } else {
        const dataRow = anyline.insert("sys_role", role, true);
        roleId = dataRow.getString("id")
    }
    if (menus) {
        for(menuId in menus.split(',')){
            //插入角色菜单关系
            anyline.insert("sys_role_menu", { menuId, roleId })
        }   
    }

    //
    anyline.deletes("sys_role_office", "role_id", roleId)
    if(offices && permission == 1){
        for(officeId in offices.split(',')){
            anyline.insert("sys_role_office", {officeId, roleId})
        }
    }
    if(id){
        // 先删除数据库 后删除缓存 不然拦截器会请求查询数据库 查到的还是老数据
        cacheDelete()
    }
    anyline.commitTrans(state)
} catch(err) {
    anyline.rollbackTrans(state)
}
