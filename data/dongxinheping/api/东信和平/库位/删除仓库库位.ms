{
  "properties" : { },
  "id" : "wl202605260001deletewarehouseabc",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除仓库库位",
  "createTime" : null,
  "updateTime" : 1789610649516,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/warehouse-location/delete",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "id",
    "value" : null,
    "description" : "库位记录ID（与库位码二选一，传入时优先）",
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
    "value" : null,
    "description" : "库位码（与id二选一，按库位删除记录）",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": \"删除成功\"\n}",
  "description" : "删除仓库库位记录：id 优先，否则按库位码 locationCode 删除",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
// id 优先；否则按库位码删（locationCode）
let deleteId = id == null ? '' : (id + '').trim()
let deleteLocationCode = locationCode == null ? '' : (locationCode + '').trim()

if(deleteId == '' && deleteLocationCode == ''){
    exit 0, '库位记录ID不能为空'
}

let rows = 0
if(deleteId != ''){
    rows = db.update("""
        delete from t_location_warehouse
        where id = #{id}
    """, {
        id: deleteId
    })
}else{
    rows = db.update("""
        delete from t_location_warehouse
        where location_id = #{locationId}
    """, {
        locationId: deleteLocationCode
    })
}

if(rows == 0){
    exit 0, '库位记录不存在'
}

return '删除成功'
