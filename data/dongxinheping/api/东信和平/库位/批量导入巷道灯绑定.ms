{
  "properties" : { },
  "id" : "ledimport01a2b3c4d5e6f7a8b9c0d1e2f",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "批量导入巷道灯绑定",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led-mapping/import",
  "method" : "POST",
  "parameters" : [ {
    "name" : "file",
    "value" : { },
    "description" : "Excel(.xlsx/.xls)或CSV(.csv)文件，表头需包含库位码、设备ID、颜色",
    "required" : false,
    "dataType" : "MultipartFile",
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
  "responseBody" : "{\n  \"totalRows\": 2,\n  \"successCount\": 2,\n  \"failCount\": 0,\n  \"errors\": []\n}",
  "description" : "上传Excel/CSV批量导入巷道灯绑定到 t_location_led",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import request
import java.io.ByteArrayInputStream
import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.text.csv.CsvReadConfig

let fail = (message) => {
    return {
        totalRows: 0,
        successCount: 0,
        failCount: 0,
        errors: [{ rowNo: 0, message: message }]
    }
}

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let normalizeKey = (key) => {
    if(key == null){
        return ''
    }
    return (key + '').trim().toLowerCase().replace('_', '')
}

let firstValue = (row, keys) => {
    let normalizedKeys = []
    for(key in keys){
        normalizedKeys.add(normalizeKey(key))
    }
    for(key in row.keySet()){
        let nk = normalizeKey(key)
        for(nk2 in normalizedKeys){
            if(nk == nk2){
                let value = row.get(key)
                if(value != null && (value + '').trim() != ''){
                    return (value + '').trim()
                }
            }
        }
    }
    return ''
}

let validColors = ['RED', 'YELLOW', 'GREEN']

let uploadedFile = request.getFile('file')
if(uploadedFile == null || uploadedFile.isEmpty()){
    return fail('请上传文件')
}

let fileName = uploadedFile.getOriginalFilename()
if(fileName == null){
    return fail('无法获取文件名')
}
fileName = fileName.toLowerCase()

let rows = []
if(fileName.endsWith('.csv')){
    let csvConfig = CsvReadConfig.defaultConfig()
    csvConfig.setContainsHeader(true)
    let bytes = uploadedFile.getBytes()
    let csvContent
    if(bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF){
        csvContent = new String(bytes, 3, bytes.length - 3, 'UTF-8')
    }else{
        csvContent = new String(bytes, 'GBK')
    }
    let csvData = CsvUtil.getReader(csvConfig).readFromStr(csvContent)
    let headerList = csvData.getHeader()
    let cleanedHeaders = []
    if(headerList != null){
        for(h in headerList){
            cleanedHeaders.add(normalizeText(h))
        }
    }
    for(csvRow in csvData.getRows()){
        let fieldMap = csvRow.getFieldMap()
        let cleanedMap = {}
        if(fieldMap != null){
            for(key in fieldMap.keySet()){
                let cleanKey = normalizeText(key)
                cleanedMap[cleanKey] = fieldMap.get(key)
            }
        }
        rows.add(cleanedMap)
    }
}else{
    rows = ExcelUtil.getReader(new ByteArrayInputStream(uploadedFile.getBytes())).readAll()
}

if(rows == null || rows.size() == 0){
    return fail('文件数据为空')
}

let locationKeys = ['location_code', 'locationCode', '库位码', '库位编码']
let ledIdKeys = ['led_id', 'ledId', '设备ID', '设备Id', '设备id', 'LED ID']
let colorKeys = ['color', '颜色', '灯色']

let totalRows = rows.size()
let successCount = 0
let failCount = 0
let errors = []
let rowNo = 1

for(row in rows){
    rowNo = rowNo + 1
    let locationCode = normalizeText(firstValue(row, locationKeys))
    let ledId = normalizeText(firstValue(row, ledIdKeys))
    let color = normalizeText(firstValue(row, colorKeys)).toUpperCase()

    if(locationCode == '' || ledId == ''){
        failCount = failCount + 1
        errors.add({
            rowNo: rowNo,
            message: '库位码或设备ID为空'
        })
        continue
    }

    if(color == ''){
        color = 'RED'
    }

    let colorValid = false
    for(c in validColors){
        if(c == color){
            colorValid = true
            break
        }
    }
    if(!colorValid){
        failCount = failCount + 1
        errors.add({
            rowNo: rowNo,
            message: '颜色值无效: ' + color + '，仅支持 RED/YELLOW/GREEN'
        })
        continue
    }

    try {
        db.update("""
            insert into t_location_led (location_code, led_id, color, status, create_time)
            values (#{locationCode}, #{ledId}, #{color}, 0, now())
            on duplicate key update
                status = 0
        """, {
            locationCode: locationCode,
            ledId: ledId,
            color: color
        })
        successCount = successCount + 1
    } catch(dbErr) {
        failCount = failCount + 1
        errors.add({
            rowNo: rowNo,
            message: dbErr.message
        })
        log.warn('LED mapping import row failed, rowNo=' + rowNo + ', message=' + dbErr.message)
    }
}

log.info('LED mapping import done, totalRows=' + totalRows + ', successCount=' + successCount + ', failCount=' + failCount)

return {
    totalRows: totalRows,
    successCount: successCount,
    failCount: failCount,
    errors: errors
}
