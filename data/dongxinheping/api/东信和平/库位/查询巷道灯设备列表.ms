{
  "properties" : { },
  "id" : "9c6f2cf09e1f4bb6af4d51f8ce24d541",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "查询巷道灯设备列表",
  "createTime" : 1777000000000,
  "updateTime" : 1776927734655,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led-devices",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "该接口需要登录才允许访问",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": null,\n    \"timestamp\": 1776927732269,\n    \"executeTime\": 130\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
const rows = db.select("""
    select
        mac_address,
        ip,
        remark
    from t_led_device
""")
return rows
