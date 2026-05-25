{
  "properties" : { },
  "id" : "f8476380035841abb4a1153398ade316",
  "script" : null,
  "groupId" : "999006442fb54aebbb2015e4a23bdba8",
  "name" : "定时同步article数据",
  "createTime" : 1776664436822,
  "updateTime" : 1779728713979,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/articles",
  "cron" : "0 */30 * * * ?",
  "enabled" : true,
  "description" : "每30分钟运行一次",
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
import com.dtflys.forest.Forest
import '@/aims/articles/updateArticleInfo' as updateArticleInfo

const stationCode = Forest.config().getVariableValue("stationCode")

const batchSize = 20
const maxLocationPages = 3

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let valueOf = (row, snakeName, camelName) => {
    if(row == null){
        return null
    }
    let value = row[snakeName]
    if(value == null && camelName != null && camelName != ''){
        value = row[camelName]
    }
    return value
}

let formatValue = (value) => {
    if(value == null){
        return ''
    }
    return value + ''
}

let putIndexedField = (data, fieldName, pageIndex, rowIndex, value) => {
    if(value == null){
        return
    }
    let text = formatValue(value)
    if(text == ''){
        return
    }
    let globalRowIndex = ((pageIndex - 1) * batchSize) + rowIndex
    data[fieldName + '_' + globalRowIndex] = text
}

let queryLocationBatch = (offset) => {
    let locationItems = []
    let rows = db.select("""
        select
            i.location_id,
            (
                select ii.sku
                  from t_inventory ii
                 where ii.location_id = i.location_id
                   and ifnull(ii.sku, '') <> ''
                 order by ii.edit_time desc, ii.sku
                 limit 1
            ) as sku
          from t_inventory i
         where ifnull(i.location_id, '') <> ''
         group by i.location_id
         order by i.location_id
         limit #{batchSize} offset #{offset}
    """, {
        batchSize: batchSize,
        offset: offset
    })
    if(rows != null){
        for(row in rows){
            let locationId = valueOf(row, 'location_id', 'locationId')
            locationId = normalizeText(locationId)
            if(locationId != ''){
                locationItems.add({
                    locationId: locationId,
                    sku: normalizeText(valueOf(row, 'sku', 'sku'))
                })
            }
        }
    }
    return locationItems
}

let queryInventoryRowsByLocationPage = (locationId, pageOffset) => {
    if(locationId == null || locationId == ''){
        return []
    }
    return db.select("""
        select
            warehouse_id,
            location_id,
            sku,
            lot_att09,
            qty,
            qty_allocated,
            qty_pa,
            date_format(edit_time, '%Y-%m-%d %H:%i:%s') as edit_time,
            concat(
                date_format(edit_time, '%Y-%m-%dT%H:%i:%s.'),
                left(date_format(edit_time, '%f'), 3),
                '+0000'
            ) as modified_date,
            user_define1,
            user_define2,
            user_define3,
            user_define4,
            user_define5
          from t_inventory
         where location_id = #{locationId}
         order by edit_time desc
         limit #{batchSize} offset #{pageOffset}
    """, {
        locationId: locationId,
        batchSize: batchSize,
        pageOffset: pageOffset
    })
}

let appendInventoryRowsToArticleData = (articleItem, inventoryRows, pageIndex) => {
    if(inventoryRows == null || inventoryRows.size() == 0){
        return 0
    }

    let appendedCount = 0
    let rowIndex = 1
    for(row in inventoryRows){
        let rowLocationId = normalizeText(valueOf(row, 'location_id', 'locationId'))
        if(rowLocationId == ''){
            continue
        }
        if(articleItem.modifiedDate == null || articleItem.modifiedDate == ''){
            articleItem.modifiedDate = valueOf(row, 'modified_date', 'modifiedDate')
        }
        putIndexedField(articleItem.data, 'warehouseId', pageIndex, rowIndex, valueOf(row, 'warehouse_id', 'warehouseId'))
        putIndexedField(articleItem.data, 'locationId', pageIndex, rowIndex, rowLocationId)
        putIndexedField(articleItem.data, 'sku', pageIndex, rowIndex, valueOf(row, 'sku', 'sku'))
        putIndexedField(articleItem.data, 'lotAtt09', pageIndex, rowIndex, valueOf(row, 'lot_att09', 'lotAtt09'))
        putIndexedField(articleItem.data, 'qty', pageIndex, rowIndex, valueOf(row, 'qty', 'qty'))
        putIndexedField(articleItem.data, 'qtyAllocated', pageIndex, rowIndex, valueOf(row, 'qty_allocated', 'qtyAllocated'))
        putIndexedField(articleItem.data, 'qtyPa', pageIndex, rowIndex, valueOf(row, 'qty_pa', 'qtyPa'))
        putIndexedField(articleItem.data, 'editTime', pageIndex, rowIndex, valueOf(row, 'edit_time', 'editTime'))
        putIndexedField(articleItem.data, 'userDefine1', pageIndex, rowIndex, valueOf(row, 'user_define1', 'userDefine1'))
        putIndexedField(articleItem.data, 'userDefine2', pageIndex, rowIndex, valueOf(row, 'user_define2', 'userDefine2'))
        putIndexedField(articleItem.data, 'userDefine3', pageIndex, rowIndex, valueOf(row, 'user_define3', 'userDefine3'))
        putIndexedField(articleItem.data, 'userDefine4', pageIndex, rowIndex, valueOf(row, 'user_define4', 'userDefine4'))
        putIndexedField(articleItem.data, 'userDefine5', pageIndex, rowIndex, valueOf(row, 'user_define5', 'userDefine5'))
        rowIndex = rowIndex + 1
        appendedCount = appendedCount + 1
    }

    return appendedCount
}

let buildArticleDataItem = (locationId, sku) => {
    let articleItem = {
        stationCode,
        id: locationId,
        name: normalizeText(sku),
        eans: [locationId],
        data: {},
        modifiedDate: null
    }
    let totalRows = 0
    let pageIndex = 1
    while(pageIndex <= maxLocationPages){
        let pageOffset = (pageIndex - 1) * batchSize
        let inventoryRows = queryInventoryRowsByLocationPage(locationId, pageOffset)
        if(inventoryRows == null || inventoryRows.size() == 0){
            break
        }
        totalRows = totalRows + appendInventoryRowsToArticleData(articleItem, inventoryRows, pageIndex)
        if(inventoryRows.size() < batchSize){
            break
        }
        pageIndex = pageIndex + 1
    }
    return {
        item: articleItem,
        rowCount: totalRows
    }
}

let startedAt = new Date()
let batchCount = 0
let readLocations = 0
let readInventoryRows = 0
let updatedArticles = 0
let failedBatches = []

try {
    let offset = 0
    while(true){
        let locationItems = queryLocationBatch(offset)
        if(locationItems.size() == 0){
            break
        }

        batchCount = batchCount + 1
        readLocations = readLocations + locationItems.size()

        let dataList = []
        for(locationItem in locationItems){
            let articleData = buildArticleDataItem(locationItem.locationId, locationItem.sku)
            if(articleData.rowCount > 0){
                readInventoryRows = readInventoryRows + articleData.rowCount
                dataList.add(articleData.item)
            }
        }

        if(dataList.size() > 0){
            try {
                let result = updateArticleInfo(dataList)
                if(result == null || result.success == false){
                    let message = '调用AIMS更新article失败'
                    if(result != null && result.message != null && result.message != ''){
                        message = result.message
                    }
                    failedBatches.add({
                        offset: offset,
                        locationCount: locationItems.size(),
                        articleCount: dataList.size(),
                        message: message
                    })
                } else {
                    updatedArticles = updatedArticles + dataList.size()
                }
            } catch (batchErr) {
                failedBatches.add({
                    offset: offset,
                    locationCount: locationItems.size(),
                    articleCount: dataList.size(),
                    message: batchErr.message
                })
                log.warn('AIMS article inventory sync batch failed, offset=' + offset + ', message=' + batchErr.message)
            }
        }

        if(locationItems.size() < batchSize){
            break
        }
        offset = offset + batchSize
    }

    let success = failedBatches.size() == 0
    log.info('AIMS article inventory sync done, success=' + success + ', batchCount=' + batchCount + ', readLocations=' + readLocations + ', readInventoryRows=' + readInventoryRows + ', updatedArticles=' + updatedArticles + ', failedBatches=' + failedBatches.size())

    return {
        success: success,
        startedAt: startedAt,
        finishedAt: new Date(),
        batchCount: batchCount,
        readLocations: readLocations,
        readInventoryRows: readInventoryRows,
        updatedArticles: updatedArticles,
        failedBatches: failedBatches
    }
} catch (e) {
    log.error('AIMS article inventory sync failed, message={}', e)
    return {
        success: false,
        startedAt: startedAt,
        finishedAt: new Date(),
        batchCount: batchCount,
        readLocations: readLocations,
        readInventoryRows: readInventoryRows,
        updatedArticles: updatedArticles,
        failedBatches: failedBatches,
        message: '定时同步article数据失败: ' + e.message
    }
}
