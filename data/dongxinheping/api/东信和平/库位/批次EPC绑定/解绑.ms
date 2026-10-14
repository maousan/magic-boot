{
  "properties" : { },
  "id" : "c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "解绑",
  "createTime" : null,
  "updateTime" : 1791886332534,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/unbind",
  "method" : "POST",
  "parameters" : [ {
    "name" : "id",
    "value" : "",
    "description" : "绑定记录ID（与epc至少传一个，传入时优先）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : null
  }, {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC（与id至少传一个，id 为空时按 epc 匹配；活跃行 EPC 唯一）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
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
  "requestBody" : "{\"id\":\"\",\"epc\":\"E28068940000000000000001\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"message\": \"解绑成功\"\n}",
  "description" : "按 id（优先）或 epc 解除批次与EPC的绑定关系。2026-10-13 起 batchId 退役，不允许作为解绑条件。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

// 2026-10-13 batchId 退役：解绑按 id（优先）或 epc 精确定位活跃记录，
// 旧 batchId/epc 双参数逻辑注释保留，回滚取消注释即可
// let batchId = body.batchId
// let epc = body.epc
// batchId = batchId != null ? (batchId + '').trim() : ''
// epc = epc != null ? (epc + '').trim() : ''
// if(batchId == '' && epc == ''){
//     exit 400, 'batchId和epc至少传一个'
// }
// var sql = "select id, batch_id, epc from t_batch_epc where status = 1"
// var params = {}
// if(batchId != ''){
//     sql = sql + " and batch_id = #{batchId}"
//     params.batchId = batchId
// }
// if(epc != ''){
//     sql = sql + " and epc = #{epc}"
//     params.epc = epc
// }
// let record = db.selectOne(sql, params)

let id = body.id
id = id != null ? (id + '').trim() : ''
let epc = body.epc
epc = epc != null ? (epc + '').trim() : ''

if(id == '' && epc == ''){
    exit 400, 'id与epc至少传一个'
}

let record = null
if(id != ''){
    record = db.selectOne("""
        select id, batch_id, epc from t_batch_epc
        where status = 1 and id = #{id}
    """, {id: id})
}else{
    record = db.selectOne("""
        select id, batch_id, epc from t_batch_epc
        where status = 1 and epc = #{epc}
    """, {epc: epc})
}

if(record == null){
    exit 404, '未找到活跃绑定记录'
}

db.update("""
    update t_batch_epc set status = 0, unbind_time = now() where id = #{id}
""", {id: record.id})

log.info('batch-epc unbind: id=' + record.id + ', batchId=' + record.batch_id + ', epc=' + record.epc)

return {
    success: true,
    message: '解绑成功'
}