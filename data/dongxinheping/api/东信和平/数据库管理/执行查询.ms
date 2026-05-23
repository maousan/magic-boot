{
  "properties" : { },
  "id" : "1b8d0f3a5c7e42d2b64f0893e01c7d22",
  "script" : null,
  "groupId" : "f2a9c31d6e8547b99a0d4c2e8f1357aa",
  "name" : "执行查询",
  "createTime" : 1779504000000,
  "updateTime" : 1779504000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/query",
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
  "requestBody" : "{\n  \"sql\": \"SELECT * FROM t_inventory LIMIT 10\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\"list\":[],\"rowCount\":0,\"duration_ms\":10}",
  "description" : "执行 SELECT 查询并返回结果列表及耗时",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
var sql = body.sql
if (sql == null || sql.trim() == '') {
    exit 0, 'SQL 不能为空'
}

var startTime = System.currentTimeMillis()
var list = db.select(sql)
var duration = System.currentTimeMillis() - startTime

return {
    list: list,
    rowCount: list.size(),
    hasMore: false,
    duration_ms: duration
}
