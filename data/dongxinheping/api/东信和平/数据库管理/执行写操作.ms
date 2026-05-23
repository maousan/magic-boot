{
  "properties" : { },
  "id" : "2c9e104b6d8f43e3c75a1904f12d8e33",
  "script" : null,
  "groupId" : "f2a9c31d6e8547b99a0d4c2e8f1357aa",
  "name" : "执行写操作",
  "createTime" : 1779504000000,
  "updateTime" : 1779504000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/execute",
  "method" : "POST",
  "parameters" : [ ],
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
  "requestBody" : "{\n  \"sql\": \"UPDATE t_inventory SET qty = 0 WHERE id = '1'\",\n  \"confirmed\": false\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\"preview\":true,\"message\":\"确认执行此写操作？\"}",
  "description" : "执行 INSERT/UPDATE/DELETE 写操作，支持两步确认（confirmed=false 预览，confirmed=true 执行）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
var sql = body.sql
var confirmed = body.confirmed

if (sql == null || sql.trim() == '') {
    exit 0, 'SQL 不能为空'
}

if (confirmed != true) {
    return {
        preview: true,
        message: '确认执行此写操作？影响行数需执行后可知。'
    }
}

var startTime = System.currentTimeMillis()
var affected = db.update(sql)
var duration = System.currentTimeMillis() - startTime

return {
    affected: affected,
    duration_ms: duration
}
