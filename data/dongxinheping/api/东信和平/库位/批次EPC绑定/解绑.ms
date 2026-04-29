{
  "properties" : { },
  "id" : "c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "解绑",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/unbind",
  "method" : "POST",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "",
    "description" : "业务批次ID（与epc至少传一个）",
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
    "description" : "RFID标签EPC（与batchId至少传一个）",
    "required" : false,
    "dataType" : "String",
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
  "responseBody" : "{\n  \"success\": true,\n  \"message\": \"解绑成功\"\n}",
  "description" : "解除批次与EPC的绑定关系。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let batchId = body.batchId
let epc = body.epc

batchId = batchId != null ? (batchId + '').trim() : ''
epc = epc != null ? (epc + '').trim() : ''

if(batchId == '' && epc == ''){
    exit 400, 'batchId和epc至少传一个'
}

var sql = "select id, batch_id, epc from t_batch_epc where status = 1"
var params = {}

if(batchId != ''){
    sql = sql + " and batch_id = #{batchId}"
    params.batchId = batchId
}
if(epc != ''){
    sql = sql + " and epc = #{epc}"
    params.epc = epc
}

let record = db.selectOne(sql, params)

if(record == null){
    exit 404, '未找到活跃绑定记录'
}

db.update("""
    update t_batch_epc set status = 0, unbind_time = now() where id = #{id}
""", {id: record.id})

log.info('batch-epc unbind: batchId=' + record.batch_id + ', epc=' + record.epc)

return {
    success: true,
    message: '解绑成功'
}