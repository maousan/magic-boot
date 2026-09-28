{
  "properties" : { },
  "id" : "d9234bf2fa9b4462b6a7db0668dbf64b",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "查询绑定",
  "createTime" : null,
  "updateTime" : 1790127047434,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/query",
  "method" : "GET",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "B2-11-02-01-02_2.01.26.01.20306_20260113_2025122365-1+01(1)_YS12_YS-JK-001_002",
    "description" : "业务批次ID（与epc二选一，传入时优先）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC（与batchId二选一）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1790127045612,\n    \"executeTime\": null\n}",
  "description" : "查询批次EPC绑定关系：batchId 与 epc 二选一（batchId 优先），未绑定返回 data=null。整合原 按批次查询(/query-by-batch) 与 按EPC查询(/query-by-epc)",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
// batchId 与 epc 二选一，batchId 优先（原 /query-by-batch 与 /query-by-epc 合并）
let queryBatchId = batchId == null ? '' : (batchId + '').trim()
let queryEpc = epc == null ? '' : (epc + '').trim()

if(queryBatchId == '' && queryEpc == ''){
    exit 400, 'batchId与epc至少传一个'
}

let record = null
if(queryBatchId != ''){
    record = db.selectOne("""
        select id, batch_id, epc, bind_time, location_id, sku, lot_no from t_batch_epc
        where status = 1 and batch_id = #{batchId}
    """, {batchId: queryBatchId})
}else{
    record = db.selectOne("""
        select id, batch_id, epc, bind_time, location_id, sku, lot_no from t_batch_epc
        where status = 1 and epc = #{epc}
    """, {epc: queryEpc})
}

if(record == null){
    return {success: true, data: null}
}

return {
    success: true,
    data: {
        id: record.id,
        batchId: record.batchId,
        epc: record.epc,
        bindTime: record.bindTime,
        locationId: record.locationId,
        sku: record.sku,
        lotNo: record.lotNo
    }
}