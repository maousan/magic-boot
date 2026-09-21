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

// batchId 格式为 locationId_sku_lotNo（locationId、sku 不含下划线），同步维护三个拆分列；
// 手工拼的 batchId 若不足两段下划线则置空，避免写脏数据
let firstUnderscore = batchId.indexOf('_')
let secondUnderscore = firstUnderscore >= 0 ? batchId.indexOf('_', firstUnderscore + 1) : -1

if(secondUnderscore > 0){
    let locationIdVal = batchId.substring(0, firstUnderscore)
    let skuVal = batchId.substring(firstUnderscore + 1, secondUnderscore)
    let lotNoVal = batchId.substring(secondUnderscore + 1)
    db.update("""
        update t_batch_epc set batch_id = #{batchId}, epc = #{epc}, location_id = #{locationId}, sku = #{sku}, lot_no = #{lotNo} where id = #{id}
    """, {id: id, batchId: batchId, epc: epc, locationId: locationIdVal, sku: skuVal, lotNo: lotNoVal})
}else{
    db.update("""
        update t_batch_epc set batch_id = #{batchId}, epc = #{epc}, location_id = null, sku = null, lot_no = null where id = #{id}
    """, {id: id, batchId: batchId, epc: epc})
}

log.info('batch-epc update: id=' + id + ', batchId=' + batchId + ', epc=' + epc)

return {
    id,
    batchId,
    epc
}
