{
  "properties" : { },
  "id" : "wl202605290001updtewarehouseabcdefg",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "编辑仓库库位",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/warehouse-location/update",
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
  "requestBody" : "{\n  \"id\": \"xxx\",\n  \"warehouseCode\": \"WH01\",\n  \"locationId\": \"A-01-01\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": \"编辑成功\"\n}",
  "description" : "根据ID编辑仓库库位记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
let warehouseCode = body.warehouseCode
let locationId = body.locationId

if(id == null || (id + '').trim() == ''){
    exit 0, '库位记录ID不能为空'
}
if(warehouseCode == null || (warehouseCode + '').trim() == ''){
    exit 0, '仓库编码不能为空'
}
if(locationId == null || (locationId + '').trim() == ''){
    exit 0, '库位ID不能为空'
}

id = (id + '').trim()
warehouseCode = (warehouseCode + '').trim()
locationId = (locationId + '').trim()

let exists = db.selectValue("""
    select count(1) from t_location_warehouse
    where warehouse_code = #{warehouseCode} and location_id = #{locationId} and id != #{id}
""", { warehouseCode: warehouseCode, locationId: locationId, id: id })

if(exists > 0){
    exit 0, '该仓库编码和库位ID已存在'
}

let rows = db.update("""
    update t_location_warehouse
    set warehouse_code = #{warehouseCode}, location_id = #{locationId}, update_time = now()
    where id = #{id}
""", {
    id: id,
    warehouseCode: warehouseCode,
    locationId: locationId
})

if(rows == 0){
    exit 0, '库位记录不存在'
}

return '编辑成功'
