{
  "properties" : { },
  "id" : "996d1cb2909b44b9ac7c17061cd6b7e8",
  "script" : null,
  "groupId" : "644d198b34704daeae5152e00e6da12b",
  "name" : "定时同步库存接口数据",
  "createTime" : 1778046005112,
  "updateTime" : 1779728706101,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/fetch-inventory",
  "cron" : "0 0 */1 * * ?",
  "enabled" : true,
  "description" : "每小时运行一次",
  "params" : "",
  "misfirePolicy" : "SMART",
  "concurrent" : false,
  "dependsOn" : null,
  "maxRetry" : 0,
  "timeout" : 0,
  "jobType" : "script",
  "clazz" : null
}
================================
import log
import java.util.List
import '@/utils/queryInventoryByLocations' as queryInventoryByLocations

const pageSize = 50

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

let addUnique = (list, setMap, value) => {
    let text = normalizeText(value)
    if(text == ''){
        return
    }
    if(setMap[text] == null){
        setMap[text] = 1
        list.add(text)
    }
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

let queryWarehouseCodes = () => {
    let warehouseCodes = []
    let warehouseCodeSet = {}
    let rows = db.select("""
        select warehouse_code
          from t_location_warehouse
         where ifnull(warehouse_code, '') <> ''
         group by warehouse_code
         order by warehouse_code
    """)
    if(rows != null){
        for(row in rows){
            let warehouseCode = row.warehouse_code
            if(warehouseCode == null || warehouseCode == ''){
                warehouseCode = row.warehouseCode
            }
            addUnique(warehouseCodes, warehouseCodeSet, warehouseCode)
        }
    }
    return warehouseCodes
}

let queryLocationPage = (warehouseCode, offset) => {
    let locationIds = []
    let locationIdSet = {}
    let rows = db.select("""
        select location_id
          from t_location_warehouse
         where warehouse_code = #{warehouseCode}
           and ifnull(location_id, '') <> ''
         order by location_id, id
         limit #{pageSize} offset #{offset}
    """, {
        warehouseCode: warehouseCode,
        pageSize: pageSize,
        offset: offset
    })
    if(rows != null){
        for(row in rows){
            let locationId = row.location_id
            if(locationId == null || locationId == ''){
                locationId = row.locationId
            }
            addUnique(locationIds, locationIdSet, locationId)
        }
    }
    return locationIds
}

let upsertInventoryItems = (items, defaultWarehouseCode) => {
    let normalizedItems = asList(items)
    if(normalizedItems.size() == 0){
        return {
            savedCount: 0,
            skippedCount: 0
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

    for(item in normalizedItems){
        let warehouseId = normalizeText(item.warehouseId)
        if(warehouseId == ''){
            warehouseId = defaultWarehouseCode
        }
        let locationId = normalizeText(item.locationId)
        let sku = normalizeText(item.sku)
        if(warehouseId == '' || locationId == '' || sku == ''){
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

        params["warehouseId" + suffix] = warehouseId
        params["locationId" + suffix] = locationId
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

    if(rowIndex == 0){
        return {
            savedCount: 0,
            skippedCount: skippedCount
        }
    }

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

    return {
        savedCount: rowIndex,
        skippedCount: skippedCount
    }
}

let startedAt = new Date()
let warehouseCount = 0
let locationCount = 0
let requestPages = 0
let savedItems = 0
let skippedItems = 0
let failedPages = []

try {
    let warehouseCodes = queryWarehouseCodes()
    warehouseCount = warehouseCodes.size()

    for(warehouseCode in warehouseCodes){
        let offset = 0
        while(true){
            let locationIds = queryLocationPage(warehouseCode, offset)
            if(locationIds.size() == 0){
                break
            }

            locationCount = locationCount + locationIds.size()
            requestPages = requestPages + 1

            try {
                let result = queryInventoryByLocations(warehouseCode, locationIds)
                if(result == null || result.success == false){
                    let message = '调用库存接口失败'
                    if(result != null && result.message != null && result.message != ''){
                        message = result.message
                    }
                    failedPages.add({
                        warehouseCode: warehouseCode,
                        offset: offset,
                        locationCount: locationIds.size(),
                        message: message
                    })
                } else {
                    let saveResult = upsertInventoryItems(result.items, warehouseCode)
                    savedItems = savedItems + saveResult.savedCount
                    skippedItems = skippedItems + saveResult.skippedCount
                }
            } catch (pageErr) {
                failedPages.add({
                    warehouseCode: warehouseCode,
                    offset: offset,
                    locationCount: locationIds.size(),
                    message: pageErr.message
                })
                log.warn('WMS inventory sync page failed, warehouseCode=' + warehouseCode + ', offset=' + offset + ', message=' + pageErr.message)
            }

            if(locationIds.size() < pageSize){
                break
            }
            offset = offset + pageSize
        }
    }

    let success = failedPages.size() == 0
    log.info('WMS inventory sync done, success=' + success + ', warehouseCount=' + warehouseCount + ', locationCount=' + locationCount + ', requestPages=' + requestPages + ', savedItems=' + savedItems + ', skippedItems=' + skippedItems + ', failedPages=' + failedPages.size())

    return {
        success: success,
        startedAt: startedAt,
        finishedAt: new Date(),
        warehouseCount: warehouseCount,
        locationCount: locationCount,
        requestPages: requestPages,
        savedItems: savedItems,
        skippedItems: skippedItems,
        failedPages: failedPages
    }
} catch (e) {
    log.error('WMS inventory sync failed, message={}', e)
    return {
        success: false,
        startedAt: startedAt,
        finishedAt: new Date(),
        warehouseCount: warehouseCount,
        locationCount: locationCount,
        requestPages: requestPages,
        savedItems: savedItems,
        skippedItems: skippedItems,
        failedPages: failedPages,
        message: '定时同步库存接口数据失败: ' + e.message
    }
}
