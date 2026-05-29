{
  "properties" : { },
  "id" : "getlatestver000000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "获取最新版本",
  "createTime" : null,
  "updateTime" : 1779947879327,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/latest",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": {\n    \"versionCode\": 2,\n    \"versionName\": \"1.1.0\",\n    \"apkUrl\": \"http://192.168.12.111:8090/apk/rfid-reader-1.1.0.apk\",\n    \"apkSize\": \"7.7MB\",\n    \"forceUpdate\": false,\n    \"description\": \"1. 修复 RFID 扫描偶发崩溃\"\n  }\n}",
  "description" : "查询App最新版本信息，供PDA启动时检查更新使用",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
var list = db.select("""
    select version_code, version_name, apk_url, apk_size, force_update, description
    from app_version
    where is_del = 0
    order by version_code desc
    limit 1
""")

if(list == null || list.size() == 0){
    return null
}

var v = list[0]

return {
    versionCode: v.version_code,
    versionName: v.version_name,
    apkUrl: v.apk_url,
    apkSize: v.apk_size,
    forceUpdate: v.force_update != null && v.force_update == 1,
    description: v.description
}