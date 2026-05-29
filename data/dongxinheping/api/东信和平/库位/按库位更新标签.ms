{
  "properties" : { },
  "id" : "updateart001def78ghi901234567890cd",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "按库位更新标签",
  "createTime" : null,
  "updateTime" : 1779966603347,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/warehouse-location/update-article",
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
  "requestBody" : "{\r\n    \"locationId\":\"B2-10-05-01-02\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"success\": true,\n        \"rowCount\": 7\n    },\n    \"timestamp\": 1779959655198,\n    \"executeTime\": 90\n}",
  "description" : "按单个库位从库存数据构建article并推送到AIMS",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import '@/aims/articles/updateArticleInfo' as updateArticleInfo
import '@/aims/articles/deleteArticleInfo' as deleteArticleInfo

const stationCode = Forest.config().getVariableValue("stationCode")
const maxPages = 3
const batchSize = 20

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let formatValue = (value) => {
    if(value == null){
        return ''
    }
    return value + ''
}

const placeholderData = deleteArticleInfo()

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

let {
    locationId
} = body

if(locationId == null || (locationId + '').trim() == ''){
    exit 400, '库位ID不能为空'
}
locationId = (locationId + '').trim()

// 查询该库位的SKU
let skuRow = db.selectOne("""
    select sku
      from t_inventory
     where location_id = #{locationId}
       and ifnull(sku, '') <> ''
     order by edit_time desc
     limit 1
""", { locationId: locationId })
let sku = skuRow != null ? normalizeText(skuRow.sku) : ''

// 查询该库位的库存数据
let queryInventoryPage = (pageOffset) => {
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

// 构建article数据
placeholderData.locationId = locationId

let articleItem = {
    stationCode,
    id: locationId,
    name: sku,
    eans: [locationId],
    data: placeholderData,
    modifiedDate: null
}
let totalRows = 0
let pageIndex = 1

while(pageIndex <= maxPages){
    let pageOffset = (pageIndex - 1) * batchSize
    let inventoryRows = queryInventoryPage(pageOffset)
    if(inventoryRows == null || inventoryRows.size() == 0){
        break
    }
    let rowIndex = 1
    for(row in inventoryRows){
        if(articleItem.modifiedDate == null || articleItem.modifiedDate == ''){
            articleItem.modifiedDate = row.updateTime
        }
        articleItem.data = {...placeholderData, ...articleItem.data}
        putIndexedField(articleItem.data, 'warehouseId', pageIndex, rowIndex, row.warehouseId)
        // putIndexedField(articleItem.data, 'locationId', pageIndex, rowIndex, normalizeText(row.locationId))
        putIndexedField(articleItem.data, 'sku', pageIndex, rowIndex, row.sku)
        putIndexedField(articleItem.data, 'lotAtt09', pageIndex, rowIndex, row.lotAtt09)
        putIndexedField(articleItem.data, 'qty', pageIndex, rowIndex, row.qty)
        putIndexedField(articleItem.data, 'qtyAllocated', pageIndex, rowIndex, row.qtyAllocated)
        putIndexedField(articleItem.data, 'qtyPa', pageIndex, rowIndex, row.qtyPa)
        putIndexedField(articleItem.data, 'editTime', pageIndex, rowIndex, row.editTime)
        // putIndexedField(articleItem.data, 'userDefine1', pageIndex, rowIndex, row.userDefine1)
        // putIndexedField(articleItem.data, 'userDefine2', pageIndex, rowIndex, row.userDefine2)
        // putIndexedField(articleItem.data, 'userDefine3', pageIndex, rowIndex, row.userDefine3)
        // putIndexedField(articleItem.data, 'userDefine4', pageIndex, rowIndex, row.userDefine4)
        // putIndexedField(articleItem.data, 'userDefine5', pageIndex, rowIndex, row.userDefine5)
        rowIndex = rowIndex + 1
        totalRows = totalRows + 1
    }
    if(inventoryRows.size() < batchSize){
        break
    }
    pageIndex = pageIndex + 1
}

// if(totalRows == 0){
//     let delResult = deleteArticleInfo(locationId)
//     return {
//         success: true,
//         rowCount: 0,
//         deleted: delResult.success,
//         message: delResult.success ? '该库位无库存数据，已从AIMS删除article' : '该库位无库存数据，从AIMS删除article失败: status=' + delResult.status
//     }
// }

// 推送到AIMS
const { success, message } = updateArticleInfo([articleItem])
if(!success){
    let msg = message || '推送AIMS失败'
    exit 500, msg
}

return {
    success: true,
    rowCount: totalRows
}
