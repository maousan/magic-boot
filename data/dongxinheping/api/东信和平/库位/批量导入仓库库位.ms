{
  "properties" : { },
  "id" : "f0d7a1b9c3e84f62ab5d7902468ce351",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "批量导入仓库库位",
  "createTime" : 1777392000000,
  "updateTime" : 1777392000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/warehouse-location/import",
  "method" : "POST",
  "parameters" : [ {
    "name" : "file",
    "value" : { },
    "description" : "Excel(.xlsx/.xls)或CSV(.csv)文件，表头需包含仓库编码和库位ID",
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
    "description" : "该接口需要登录才允许访问",
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
  "responseBody" : "{\n  \"success\": true,\n  \"totalRows\": 2,\n  \"successCount\": 2,\n  \"failCount\": 0,\n  \"errors\": []\n}",
  "description" : "上传Excel批量导入仓库编码和库位ID到 t_location_warehouse。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import request
import java.util.UUID
import java.io.ByteArrayInputStream
import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.text.csv.CsvReadConfig

let fail = (message) => {
    return {
        success: false,
        message: message
    }
}

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let firstValue = (row, keys) => {
    for(key in keys){
        let value = row[key]
        if(value != null && (value + '').trim() != ''){
            return (value + '').trim()
        }
    }
    return ''
}

let newId = () => {
    return UUID.randomUUID().toString().replace('-', '')
}

let uploadedFile = request.getFile('file')
if(uploadedFile == null || uploadedFile.isEmpty()){
    return fail('请上传文件')
}

let fileName = uploadedFile.getOriginalFilename()
if(fileName == null){
    return fail('无法获取文件名')
}
fileName = fileName.toLowerCase()

db.update("""
    create table if not exists t_location_warehouse (
        id varchar(32) not null primary key comment '主键ID',
        warehouse_code varchar(64) not null comment '仓库编码',
        location_id varchar(128) not null comment '库位ID',
        create_time datetime not null default current_timestamp comment '创建时间',
        update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
        unique key uk_location_warehouse_code_location (warehouse_code, location_id)
    ) comment='仓库库位导入数据'
""")

let rows
if(fileName.endsWith('.csv')){
    let csvConfig = CsvReadConfig.defaultConfig()
    csvConfig.setContainsHeader(true)
    let csvData = CsvUtil.getReader(csvConfig).readFromStr(new String(uploadedFile.getBytes(), 'UTF-8'))
    rows = []
    for(csvRow in csvData.getRows()){
        rows.add(csvRow.getFieldMap())
    }
}else{
    rows = ExcelUtil.getReader(new ByteArrayInputStream(uploadedFile.getBytes())).readAll()
}

if(rows == null || rows.size() == 0){
    return fail('文件数据为空')
}

let warehouseKeys = ['warehouse_code', 'warehouseCode', '仓库编码']
let locationKeys = ['location_id', 'locationId', '库位ID', '库位id']
let totalRows = rows.size()
let successCount = 0
let failCount = 0
let errors = []
let rowNo = 1

for(row in rows){
    rowNo = rowNo + 1
    let warehouseCode = normalizeText(firstValue(row, warehouseKeys))
    let locationId = normalizeText(firstValue(row, locationKeys))

    if(warehouseCode == '' || locationId == ''){
        failCount = failCount + 1
        errors.add({
            rowNo: rowNo,
            message: '仓库编码或库位ID为空'
        })
        continue
    }

    db.update("""
        insert into t_location_warehouse (
            id,
            warehouse_code,
            location_id,
            create_time,
            update_time
        ) values (
            #{id},
            #{warehouseCode},
            #{locationId},
            now(),
            now()
        )
        on duplicate key update
            update_time = now()
    """, {
        id: newId(),
        warehouseCode: warehouseCode,
        locationId: locationId
    })
    successCount = successCount + 1
}

log.info('warehouse location import done, totalRows=' + totalRows + ', successCount=' + successCount + ', failCount=' + failCount)

return {
    success: failCount == 0,
    totalRows: totalRows,
    successCount: successCount,
    failCount: failCount,
    errors: errors
}
