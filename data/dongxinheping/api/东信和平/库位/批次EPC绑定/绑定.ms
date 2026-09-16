{
  "properties" : { },
  "id" : "b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "绑定",
  "createTime" : null,
  "updateTime" : 1784704489316,
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
raw = '"OrdNo":"2025122365-1+01";"Sup":"Ep刷";"SupCode":"印刷";"Mtl":"2.01.26.01.20306";"MtlCode":"2.01.26.01.20306";"MtIDesc":"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R-C49";"Lot":"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002";"Qty":"170";"PD":"2026-01-13 07:09:13";"Exp":"2036-01-11 07:09:13"'
epc = (epc + '').trim()
epc = 'E281D0122000975658773D8C'

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
    select id, batch_id, epc from t_batch_epc where status = 1 and (batch_id = #{batchId} or epc = #{epc})
""", {batchId: batchId, epc: epc})

let epcRow = null
if(existing != null && existing.size() > 0){
    for(row in existing){
        if(row.epc == epc){
            epcRow = row
        }else if(row.batchId == batchId){
            exit 400, '批次ID已绑定EPC: ' + row.epc
        }
    }
}

// 覆盖更新：EPC 已绑定其他批次时，将该记录改绑到新 batchId
if(epcRow != null && epcRow.batchId == batchId){
    log.info('batch-epc bind idempotent: batchId=' + batchId + ', epc=' + epc)
    return {
        id: epcRow.id,
        batchId,
        epc,
        bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: epcRow.id})
    }
}

if(epcRow != null){
    db.update("""
        update t_batch_epc set batch_id = #{batchId}, bind_time = now() where id = #{id}
    """, {id: epcRow.id, batchId: batchId})
    log.info('batch-epc rebind: id=' + epcRow.id + ', oldBatchId=' + epcRow.batchId + ', newBatchId=' + batchId + ', epc=' + epc)
    return {
        id: epcRow.id,
        batchId,
        epc,
        bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: epcRow.id})
    }
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