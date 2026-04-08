{
  "properties" : { },
  "id" : "2e1c86a565084d5e976d8325002ecb9d",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除库位码和巷道灯的绑定关系",
  "createTime" : 1775201582139,
  "updateTime" : 1775300478447,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led-mapping",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "lotNo",
    "value" : "20260113_2025122365-1 01(1)_YS12_YS-JK-001_002",
    "description" : null,
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
  "requestBody" : "{\r\n    \"ledId\": \"LED01\",\r\n    \"lotNo\":\"20260113_2025122365-1 01(1)_YS12_YS-JK-001_002\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": 0,\n    \"timestamp\": 1775201619941,\n    \"executeTime\": 8\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
return db.table("t_location_led").where().eq("location_code", lotNo).delete()
