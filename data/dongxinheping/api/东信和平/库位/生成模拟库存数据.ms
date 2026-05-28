{
  "properties" : { },
  "id" : "inv20260527mock001abcdefghijklmnopqrstuv",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "生成模拟库存数据",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/inventory/mock",
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
  "requestBody" : "{\n  \"recordsPerLocation\": 2\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": {\n    \"totalLocations\": 10,\n    \"totalRecords\": 20,\n    \"successCount\": 20,\n    \"failCount\": 0\n  }\n}",
  "description" : "基于t_location_warehouse库位列表批量生成模拟库存数据",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import java.util.Random

let recordsPerLocation = body.recordsPerLocation
if(recordsPerLocation == null || recordsPerLocation < 1){
    recordsPerLocation = 1
}
if(recordsPerLocation > 10){
    recordsPerLocation = 10
}

let locations = db.select("""
    select warehouse_code, location_id from t_location_warehouse
""")

if(locations == null || locations.size() == 0){
    exit 0, '库位数据为空，请先导入库位'
}

let totalLocations = locations.size()
let totalRecords = 0
let successCount = 0
let failCount = 0
let random = new Random()

for(location in locations){
    let warehouseCode = location.warehouse_code
    if(warehouseCode == null || warehouseCode == ''){
        warehouseCode = location.warehouseCode
    }
    let locationId = location.location_id
    if(locationId == null || locationId == ''){
        locationId = location.locationId
    }
    if(warehouseCode == null || warehouseCode == '' || locationId == null || locationId == ''){
        continue
    }

    var i = 0
    while(i < recordsPerLocation){
        i = i + 1
        totalRecords = totalRecords + 1
        let suffix = (random.nextInt(9000) + 1000) + ''
        let sku = 'SKU-' + suffix
        let lotAtt09 = 'LOT-' + Long.toHexString(System.currentTimeMillis() + random.nextInt(99999)).toUpperCase()
        let qty = random.nextInt(200) + 1
        let qtyAllocated = random.nextInt(qty + 1)
        let qtyPa = random.nextInt(qty - qtyAllocated + 1)

        try {
            db.update("""
                insert into t_inventory (warehouse_id, location_id, sku, lot_att09, qty, qty_allocated, qty_pa, edit_time, update_time)
                values (#{warehouseCode}, #{locationId}, #{sku}, #{lotAtt09}, #{qty}, #{qtyAllocated}, #{qtyPa}, now(), now())
            """, {
                warehouseCode: warehouseCode,
                locationId: locationId,
                sku: sku,
                lotAtt09: lotAtt09,
                qty: qty,
                qtyAllocated: qtyAllocated,
                qtyPa: qtyPa
            })
            successCount = successCount + 1
        } catch(dbErr) {
            failCount = failCount + 1
            log.warn('Mock inventory insert failed: ' + dbErr.message)
        }
    }
}

log.info('Mock inventory generated, totalLocations=' + totalLocations + ', totalRecords=' + totalRecords + ', successCount=' + successCount + ', failCount=' + failCount)

return {
    totalLocations: totalLocations,
    totalRecords: totalRecords,
    successCount: successCount,
    failCount: failCount
}
