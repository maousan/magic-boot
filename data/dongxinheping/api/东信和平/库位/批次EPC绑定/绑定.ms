{
  "properties" : { },
  "id" : "b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "绑定",
  "createTime" : null,
  "updateTime" : 1780477311521,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/bind",
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
  "requestBody" : "{\"locationId\":\"A001\",\"batchId\":\"外箱二维码内容\",\"epc\":\"1\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "绑定业务批次ID与RFID EPC的一对一关系。batchId为外箱二维码原始内容，后端自动解析MtlCode和Lot，与locationId拼接为最终batchId。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import cn.hutool.core.util.IdUtil
import '@/utils/parseLot' as parseLot

let locationId = body.locationId
let raw = body.batchId
let epc = body.epc

if(locationId == null || (locationId + '').trim() == ''){
    exit 400, '库位号不能为空'
}
if(raw == null || (raw + '').trim() == ''){
    exit 400, '外箱二维码不能为空'
}
if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}

locationId = (locationId + '').trim()
raw = (raw + '').trim()
epc = (epc + '').trim()

let parsed = parseLot(raw)
let sku = parsed.MtlCode
let lotNo = parsed.Lot

if(sku == null || (sku + '').trim() == ''){
    exit 400, '二维码中未找到MtlCode'
}
if(lotNo == null || (lotNo + '').trim() == ''){
    exit 400, '二维码中未找到Lot'
}

sku = (sku + '').trim()
lotNo = (lotNo + '').trim()
let batchId = locationId + '_' + sku + '_' + lotNo

let existing = db.select("""
    select batch_id, epc from t_batch_epc where status = 1 and (batch_id = #{batchId} or epc = #{epc})
""", {batchId: batchId, epc: epc})

if(existing != null && existing.size() > 0){
    let conflict = existing.get(0)
    if(conflict.batchId == batchId){
        exit 400, '批次ID已绑定EPC: ' + conflict.epc
    }
    log.info("{}", conflict)
    exit 400, 'EPC已绑定批次: ' + conflict.batchId
}

let id = IdUtil.fastSimpleUUID()
db.update("""
    insert into t_batch_epc (id, batch_id, epc, status, bind_time)
    values (#{id}, #{batchId}, #{epc}, 1, now())
""", {id: id, batchId: batchId, epc: epc})

log.info('batch-epc bind: batchId=' + batchId + ', epc=' + epc)

return {
    id,
    batchId,
    epc,
    bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: id})
}