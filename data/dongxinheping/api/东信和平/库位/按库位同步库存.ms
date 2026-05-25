{
  "properties" : { },
  "id" : "syncinv001abc45def78901234567890ab",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "按库位同步库存",
  "createTime" : null,
  "updateTime" : 1779731046681,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/warehouse-location/sync-inventory",
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
  "requestBody" : "{\r\n    \"warehouseCode\":\"\",\r\n    \"locationId\":\"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "按单个库位从WMS同步库存数据",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import '@/utils/queryInventoryByLocations' as queryInventoryByLocations

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let decimalOrZero = (value) => {
    let text = normalizeText(value)
    if(text == ''){
        return '0'
    }
    return text
}

let asList = (value) => {
    let result = []
    if(value == null){
        return result
    }
    if(value instanceof List || value.getClass().isArray()){
        for(item in value){
            result.add(item)
        }
        return result
    }
    result.add(value)
    return result
}

let {
    warehouseCode,
    locationId
} = body

if(warehouseCode == null || (warehouseCode + '').trim() == ''){
    exit 400, '仓库编码不能为空'
}
if(locationId == null || (locationId + '').trim() == ''){
    exit 400, '库位ID不能为空'
}

warehouseCode = (warehouseCode + '').trim()
locationId = (locationId + '').trim()

let result = queryInventoryByLocations(warehouseCode, locationId)
if(result == null || result.success == false){
    let msg = '查询WMS库存失败'
    if(result != null && result.message != null){
        msg = result.message
    }
    exit 500, msg
}

let items = asList(result.items)
if(items.size() == 0){
    return {
        success: true,
        savedCount: 0,
        message: 'WMS返回空数据'
    }
}

let upsertSql = """
    insert into t_inventory (
        warehouse_id,
        location_id,
        sku,
        lot_att09,
        qty,
        qty_allocated,
        qty_pa,
        edit_time,
        user_define1,
        user_define2,
        user_define3,
        user_define4,
        user_define5,
        create_time,
        update_time
    ) values
"""
let valuesSql = ""
let params = {}
let rowIndex = 0
let skippedCount = 0

for(item in items){
    let wId = normalizeText(item.warehouseId)
    if(wId == ''){
        wId = warehouseCode
    }
    let locId = normalizeText(item.locationId)
    let sku = normalizeText(item.sku)
    if(wId == '' || locId == '' || sku == ''){
        skippedCount = skippedCount + 1
        continue
    }

    if(rowIndex > 0){
        valuesSql = valuesSql + ","
    }
    let suffix = "_" + rowIndex
    valuesSql = valuesSql + "("
        + "#{warehouseId" + suffix + "},"
        + "#{locationId" + suffix + "},"
        + "#{sku" + suffix + "},"
        + "#{lotAtt09" + suffix + "},"
        + "#{qty" + suffix + "},"
        + "#{qtyAllocated" + suffix + "},"
        + "#{qtyPa" + suffix + "},"
        + "str_to_date(nullif(#{editTime" + suffix + "}, ''), '%Y-%m-%d %H:%i:%s'),"
        + "#{userDefine1" + suffix + "},"
        + "#{userDefine2" + suffix + "},"
        + "#{userDefine3" + suffix + "},"
        + "#{userDefine4" + suffix + "},"
        + "#{userDefine5" + suffix + "},"
        + "now(),"
        + "now())"

    params["warehouseId" + suffix] = wId
    params["locationId" + suffix] = locId
    params["sku" + suffix] = sku
    params["lotAtt09" + suffix] = normalizeText(item.lotAtt09)
    params["qty" + suffix] = decimalOrZero(item.qty)
    params["qtyAllocated" + suffix] = decimalOrZero(item.qtyAllocated)
    params["qtyPa" + suffix] = decimalOrZero(item.qtyPa)
    params["editTime" + suffix] = normalizeText(item.editTime)
    params["userDefine1" + suffix] = normalizeText(item.userDefine1)
    params["userDefine2" + suffix] = normalizeText(item.userDefine2)
    params["userDefine3" + suffix] = normalizeText(item.userDefine3)
    params["userDefine4" + suffix] = normalizeText(item.userDefine4)
    params["userDefine5" + suffix] = normalizeText(item.userDefine5)
    rowIndex = rowIndex + 1
}

if(rowIndex > 0){
    db.update(upsertSql + valuesSql + """
        on duplicate key update
            sku = values(sku),
            lot_att09 = values(lot_att09),
            qty = values(qty),
            qty_allocated = values(qty_allocated),
            qty_pa = values(qty_pa),
            edit_time = values(edit_time),
            user_define1 = values(user_define1),
            user_define2 = values(user_define2),
            user_define3 = values(user_define3),
            user_define4 = values(user_define4),
            user_define5 = values(user_define5),
            update_time = now()
    """, params)
}

return {
    success: true,
    savedCount: rowIndex,
    skippedCount: skippedCount
}
