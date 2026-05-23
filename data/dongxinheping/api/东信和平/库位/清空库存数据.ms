{
  "properties" : { },
  "id" : "inv202605220003cdefghijklmnopqrst",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "清空库存数据",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/inventory/clear",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "confirm",
    "value" : null,
    "description" : "确认标识，必须传入 CONFIRM 才会执行",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "清空 t_inventory 表全部数据，需 confirm=CONFIRM",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if (confirm == null || confirm != 'CONFIRM') {
    exit 0, '请确认清空操作'
}

db.update("truncate table t_inventory")

return '清空成功'
