{
  "properties" : { },
  "id" : "inv202605220001abcdefghijklmnopqrstuv",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "手动录入库存",
  "createTime" : null,
  "updateTime" : 1779421533246,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/inventory/add",
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
  "requestBody" : "{\n  \"warehouseId\": \"WH01\",\n  \"locationId\": \"A-01-01\",\n  \"sku\": \"SKU001\",\n  \"lotAtt09\": \"LOT001\",\n  \"qty\": 100\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "手动录入单条库存记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let warehouseId = body.warehouseId
let locationId = body.locationId
let sku = body.sku
let lotAtt09 = body.lotAtt09
let qty = body.qty
let qtyAllocated = body.qtyAllocated
let qtyPa = body.qtyPa
let userDefine1 = body.userDefine1
let userDefine2 = body.userDefine2
let userDefine3 = body.userDefine3
let userDefine4 = body.userDefine4
let userDefine5 = body.userDefine5

if (warehouseId == null || (warehouseId + '').trim() == '') {
    exit 0, '仓库ID不能为空'
}
if (locationId == null || (locationId + '').trim() == '') {
    exit 0, '库位码不能为空'
}
if (sku == null || (sku + '').trim() == '') {
    exit 0, '产品代码不能为空'
}

warehouseId = (warehouseId + '').trim()
locationId = (locationId + '').trim()
sku = (sku + '').trim()
if (lotAtt09 != null) {
    lotAtt09 = (lotAtt09 + '').trim()
}
if (qty == null) {
    qty = 0
}
if (qtyAllocated == null) {
    qtyAllocated = 0
}
if (qtyPa == null) {
    qtyPa = 0
}

let exists = db.selectValue("""
    select count(1) from t_inventory
    where warehouse_id = #{warehouseId} and location_id = #{locationId} and lot_att09 = #{lotAtt09}
""", {
    warehouseId: warehouseId,
    locationId: locationId,
    lotAtt09: lotAtt09
})

if (exists > 0) {
    exit 0, '该仓库+库位+批次的库存记录已存在'
}

db.update("""
    insert into t_inventory (warehouse_id, location_id, sku, lot_att09, qty, qty_allocated, qty_pa, edit_time, user_define1, user_define2, user_define3, user_define4, user_define5, update_time)
    values (#{warehouseId}, #{locationId}, #{sku}, #{lotAtt09}, #{qty}, #{qtyAllocated}, #{qtyPa}, now(), #{userDefine1}, #{userDefine2}, #{userDefine3}, #{userDefine4}, #{userDefine5}, now())
""", {
    warehouseId: warehouseId,
    locationId: locationId,
    sku: sku,
    lotAtt09: lotAtt09,
    qty: qty,
    qtyAllocated: qtyAllocated,
    qtyPa: qtyPa,
    userDefine1: userDefine1,
    userDefine2: userDefine2,
    userDefine3: userDefine3,
    userDefine4: userDefine4,
    userDefine5: userDefine5
})

return '录入成功'