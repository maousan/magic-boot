{
  "properties" : { },
  "id" : "2e1c86a565084d5e976d8325002ecb9d",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除库位码和巷道灯的绑定关系",
  "createTime" : 1775201582139,
  "updateTime" : 1779420247444,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led-mapping",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "id",
    "value" : "",
    "description" : "绑定记录ID（与locationCode二选一，传入时优先）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "locationCode",
    "value" : "",
    "description" : "库位码（与id二选一，按库位删除绑定关系）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "lotNo",
    "value" : "",
    "description" : "库位码（App端参数名，等同locationCode）",
    "required" : false,
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": 1,\n    \"timestamp\": 1779419053508,\n    \"executeTime\": 101\n}",
  "description" : "删除库位码和巷道灯的绑定关系：id 优先，否则按库位码删（locationCode 或 App 端参数名 lotNo，query/body 均可）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
// id 优先；否则按库位码删（locationCode，或 App 端参数名 lotNo，query/body 均可）
let deleteId = id == null ? '' : (id + '').trim()
let deleteLocationCode = locationCode == null ? '' : (locationCode + '').trim()
if(deleteLocationCode == '' && lotNo != null){
    deleteLocationCode = (lotNo + '').trim()
}
if(deleteLocationCode == '' && body != null && body.lotNo != null){
    deleteLocationCode = (body.lotNo + '').trim()
}

if(deleteId == '' && deleteLocationCode == ''){
    exit 400, 'id与locationCode至少传一个'
}

let deleted = 0
if(deleteId != ''){
    deleted = db.table("t_location_led").where().eq("id", deleteId).delete()
}else{
    deleted = db.table("t_location_led").where().eq("location_code", deleteLocationCode).delete()
}

if(deleted == null || deleted == 0){
    exit 404, '未找到对应的绑定记录'
}
return deleted
