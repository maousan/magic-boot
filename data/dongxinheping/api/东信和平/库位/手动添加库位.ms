{
  "properties" : { },
  "id" : "wl202405220002bcdefghijk12345678901",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "手动添加库位",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/warehouse-location/add",
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
  "requestBody" : "{\n  \"warehouseCode\": \"WH01\",\n  \"locationId\": \"A-01-01\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "手动添加单条库位记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import java.util.UUID

let warehouseCode = body.warehouseCode
let locationId = body.locationId

if(warehouseCode == null || (warehouseCode + '').trim() == ''){
    exit 0, '仓库编码不能为空'
}
if(locationId == null || (locationId + '').trim() == ''){
    exit 0, '库位ID不能为空'
}

warehouseCode = (warehouseCode + '').trim()
locationId = (locationId + '').trim()

let exists = db.selectValue("""
    select count(1) from t_location_warehouse
    where warehouse_code = #{warehouseCode} and location_id = #{locationId}
""", { warehouseCode: warehouseCode, locationId: locationId })

if(exists > 0){
    exit 0, '该仓库编码和库位ID已存在'
}

db.update("""
    insert into t_location_warehouse (id, warehouse_code, location_id, create_time, update_time)
    values (#{id}, #{warehouseCode}, #{locationId}, now(), now())
""", {
    id: UUID.randomUUID().toString().replace('-', ''),
    warehouseCode: warehouseCode,
    locationId: locationId
})

return '添加成功'
