{
  "properties" : { },
  "id" : "46d49f48026243e595ce2753ca8c9908",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "批量查询绑定",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/query-batch",
  "method" : "POST",
  "parameters" : [ ],
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
  "requestBody" : "{\"keywords\":[\"20260507_2026041752-1+02(1)_PK01_JRPK-GRH-028_001\"],\"boxNo\":\"\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "批次绑定批量查询：keywords 每项按批次号(lot_no)或EPC精确匹配，取并集返回全部活跃绑定记录（status=1，活跃行EPC唯一故无需去重）；boxNo 预留字段忽略。返回行含 id 与 epc，供 /unbind 优先按记录ID解绑、无 id 时按 epc。PDA App「批次绑定」页扫外箱二维码调用。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

// POST /query-batch：App 扫外箱二维码批量查询绑定记录（App 解析二维码取 Lot 值作 keyword）
let keywords = body.keywords

if(keywords == null){
    exit 400, 'keywords不能为空'
}

let keywordList = []
let keywordSet = {}
for(keyword in keywords){
    let kw = keyword == null ? '' : (keyword + '').trim()
    if(kw == ''){
        continue
    }
    if(keywordSet[kw] == null){
        keywordSet[kw] = 1
        keywordList.add(kw)
    }
}

if(keywordList.size() == 0){
    return []
}

let inSql = ""
let params = {}
let idx = 0
for(kw in keywordList){
    let key = 'kw' + idx
    params[key] = kw
    if(idx > 0){
        inSql = inSql + ','
    }
    inSql = inSql + '#{' + key + '}'
    idx = idx + 1
}

let rows = db.select("""
    select id, batch_id, epc, bind_time, location_id, sku, lot_no
    from t_batch_epc
    where status = 1
      and (lot_no in (""" + inSql + """) or epc in (""" + inSql + """))
""", params)

let list = []
if(rows != null){
    for(row in rows){
        list.add({
            id: row.id,
            batchId: row.batchId,
            locationId: row.locationId,
            sku: row.sku,
            lotNo: row.lotNo,
            epc: row.epc,
            bindTime: row.bindTime
        })
    }
}
return list
