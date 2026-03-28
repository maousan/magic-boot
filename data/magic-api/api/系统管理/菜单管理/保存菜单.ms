{
  "properties" : { },
  "id" : "d6d05df1e8b14a6ebc270113b2cdd1ed",
  "script" : null,
  "groupId" : "67b2ce258e24491194b74992958c74aa",
  "name" : "保存菜单",
  "createTime" : null,
  "updateTime" : 1774692650745,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "wrap_request_parameter",
    "value" : "data",
    "description" : "包装请求参数到一个变量中",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "permission",
    "value" : "menu:save",
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
  "requestBody" : "{}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误\",\n    \"data\": null,\n    \"timestamp\": 1773494064352,\n    \"executeTime\": 21\n}",
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
import cn.hutool.extra.pinyin.PinyinUtil
import org.ssssssss.magicapi.utils.JsonUtils
import org.ssssssss.magicapi.modules.db.cache.SqlCache;
import '@get:/system/menu/cache/delete' as cacheDelete;
import log

const menu = body

let menuId = menu.id

if (menu.meta) {
    menu.meta['title'] = menu.title
} else {
    menu.meta = {
        title: menu.title
    }
}

if (!menu.meta.order) {
    menu.meta.order = menu.sort || 1
}

if (menu.type === 'embedded') {
    menu.component = 'IFrameView'
}

// 判断是否为动态组件
if (menu.componentId) {
    if (!menu.component) {
        menu.component = '/lowcode/page'
    }
    menu.meta.query = {
        id: menu.componentId
    }
}

if (!menu.name) {
    menu.name = PinyinUtil.getPinyin(menu.title).replaceAll(" ", "")
}
if (!menu.path) {
    menu.path = `/${menu.name}`
}

if(body.permission){
    var permissionCount = db.selectInt("select count(1) from sys_menu where is_del = 0 and permission = #{body.permission} ?{body.id, and id != #{body.id}}")
    if(permissionCount > 0){
        exit 0,'权限标识已存在'
    }
}
// 转成json字符串
menu.meta = JsonUtils.toJsonString(menu.meta)

if (menuId) {
    anyline.update("sys_menu", menu)
    SqlCache.delete('permissions:1')
    cacheDelete();
} else {
    const dataRow = anyline.insert("sys_menu", menu, true)
    menuId = dataRow.getString("id")
}

return result