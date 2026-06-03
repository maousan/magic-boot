{
  "properties" : { },
  "id" : "e5f6a7b8c9d0e1f2a3b4c5d6e7f80123",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "编辑",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/update",
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
  "requestBody" : "{\"id\":\"\",\"batchId\":\"1\",\"epc\":\"1\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "编辑批次EPC绑定记录，更新批次ID或EPC。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let id = body.id
let batchId = body.batchId
let epc = body.epc

if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}
if(batchId == null || (batchId + '').trim() == ''){
    exit 400, '批次ID不能为空'
}
if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}

id = (id + '').trim()
batchId = (batchId + '').trim()
epc = (epc + '').trim()

let record = db.selectOne("select id, batch_id, epc from t_batch_epc where id = #{id}", {id: id})

if(record == null){
    exit 404, '记录不存在'
}

let existing = db.select("""
    select batch_id, epc from t_batch_epc where status = 1 and id != #{id} and (batch_id = #{batchId} or epc = #{epc})
""", {id: id, batchId: batchId, epc: epc})

if(existing != null && existing.size() > 0){
    let conflict = existing.get(0)
    if(conflict.batch_id == batchId){
        exit 400, '批次ID已绑定EPC: ' + conflict.epc
    }
    exit 400, 'EPC已绑定批次: ' + conflict.batch_id
}

db.update("""
    update t_batch_epc set batch_id = #{batchId}, epc = #{epc} where id = #{id}
""", {id: id, batchId: batchId, epc: epc})

log.info('batch-epc update: id=' + id + ', batchId=' + batchId + ', epc=' + epc)

return {
    id,
    batchId,
    epc
}
