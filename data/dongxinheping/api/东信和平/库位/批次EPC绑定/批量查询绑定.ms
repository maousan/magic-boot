{
  "properties" : { },
  "id" : "46d49f48026243e595ce2753ca8c9908",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "批量查询绑定",
  "createTime" : null,
  "updateTime" : 1792379016461,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
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
  "requestBody" : "{\"keyword\": \"\\\"OrdNo\\\":\\\"2026090105-1\\\";\\\"Sup\\\":\\\"\\\";\\\"SupCode\\\":\\\"701\\\";\\\"Mtl\\\":\\\"2.01.00.01.10953\\\";\\\"MtlCode\\\":\\\"2.01.00.01.10953\\\";\\\"MtlDesc\\\":\\\"YL借记标准IC金卡(S)(C2789)250410确认版  两面半哑光 YL借记卡签名条 芯片:HD80D100G_6E18H/HD80D100G_6E17M【客供卡基】\\\";\\\"Lot\\\":\\\"GRH20260913\\\";\\\"Qty\\\":\\\"2000\\\";\\\"PD\\\":\\\"2026-09-13\\\";\\\"Exp\\\":\\\"\\\"\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "批次绑定批量查询：App 扫码原文透传 keyword（外箱二维码全文/EPC）。后端识别分号分隔的 \"键\":\"值\" 外箱二维码并解析 Lot+MtlCode（物料，缺失用 Mtl）按 lot_no+sku 双条件匹配；Lot 无结果时按 OrdNo（同带 sku）兜底；非二维码输入仅按 epc 精确匹配（批次号查询统一走外箱二维码分支）；结果按 epc 去重返回全部活跃绑定记录（status=1）。返回行含 id 供 /unbind 按记录ID解绑。PDA App「批次绑定」页调用。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import java.util.regex.Pattern

// POST /query-batch：App 扫码原文透传 keyword，后端识别外箱二维码并解析匹配
// 2026-10-16 契约变更（docs/批次绑定批量查询接口.md）：App 不再解析二维码传 keywords[]，
// 改为原文透传 keyword 单串，二维码识别/Lot 解析/OrdNo 兜底/epc 去重均由后端负责
// ---- 旧 keywords[] 版（注释保留，回滚取消注释） ----
// let keywords = body.keywords
// if(keywords == null){
//     exit 400, 'keywords不能为空'
// }
// let keywordList = []
// let keywordSet = {}
// for(keyword in keywords){
//     let kw = keyword == null ? '' : (keyword + '').trim()
//     if(kw == ''){
//         continue
//     }
//     if(keywordSet[kw] == null){
//         keywordSet[kw] = 1
//         keywordList.add(kw)
//     }
// }
// if(keywordList.size() == 0){
//     return []
// }
// let inSql = ""
// let params = {}
// let idx = 0
// for(kw in keywordList){
//     let key = 'kw' + idx
//     params[key] = kw
//     if(idx > 0){
//         inSql = inSql + ','
//     }
//     inSql = inSql + '#{' + key + '}'
//     idx = idx + 1
// }
// let rows = db.select("""
//     select id, batch_id, epc, bind_time, location_id, sku, lot_no
//     from t_batch_epc
//     where status = 1
//       and (lot_no in (""" + inSql + """) or epc in (""" + inSql + """))
// """, params)

let keyword = body.keyword
if(keyword == null || (keyword + '').trim() == ''){
    exit 400, 'keyword不能为空'
}
keyword = (keyword + '').trim()

// 外箱二维码识别：分号分隔的 "键":"值" 文本 → 取 Lot（匹配键）/MtlCode（物料，缺失用 Mtl）/OrdNo（无结果时兜底）
let lot = null
let ordNo = null
let qrSku = null
if(keyword.indexOf(';') >= 0 && keyword.indexOf(':"') >= 0){
    let lotMatcher = Pattern.compile('"Lot":"([^"]+)"').matcher(keyword)
    if(lotMatcher.find()){
        lot = (lotMatcher.group(1) + '').trim()
    }
    let ordMatcher = Pattern.compile('"OrdNo":"([^"]+)"').matcher(keyword)
    if(ordMatcher.find()){
        ordNo = (ordMatcher.group(1) + '').trim()
    }
    let mtlCodeMatcher = Pattern.compile('"MtlCode":"([^"]+)"').matcher(keyword)
    if(mtlCodeMatcher.find()){
        qrSku = (mtlCodeMatcher.group(1) + '').trim()
    }
    if(qrSku == null || qrSku == ''){
        let mtlMatcher = Pattern.compile('"Mtl":"([^"]+)"').matcher(keyword)
        if(mtlMatcher.find()){
            qrSku = (mtlMatcher.group(1) + '').trim()
        }
    }
}

let rows = null
if(lot != null && lot != ''){
    // 2026-10-16 用户口径：外箱二维码按 lot_no + sku 双条件匹配，仅 lot_no 会跨物料误查；OrdNo 兜底同带 sku
    rows = db.select("""
        select id, batch_id, epc, bind_time, location_id, sku, lot_no
        from t_batch_epc
        where status = 1 and lot_no = #{lot}
        <if test="sku != null">
          and sku = #{sku}
        </if>
    """, { lot: lot, sku: qrSku })
    if((rows == null || rows.size() == 0) && ordNo != null && ordNo != ''){
        rows = db.select("""
            select id, batch_id, epc, bind_time, location_id, sku, lot_no
            from t_batch_epc
            where status = 1 and lot_no = #{lot}
            <if test="sku != null">
              and sku = #{sku}
            </if>
        """, { lot: ordNo, sku: qrSku })
    }
} else {
    // 2026-10-16 用户口径：非二维码输入只按 EPC 精确匹配——批次号查询统一走外箱二维码分支（Lot 解析）
    // rows = db.select("""
    //     select id, batch_id, epc, bind_time, location_id, sku, lot_no
    //     from t_batch_epc
    //     where status = 1 and (lot_no = #{keyword} or epc = #{keyword})
    // """, { keyword: keyword })
    rows = db.select("""
        select id, batch_id, epc, bind_time, location_id, sku, lot_no
        from t_batch_epc
        where status = 1 and epc = #{keyword}
    """, { keyword: keyword })
}

let list = []
let epcSet = {}
if(rows != null){
    for(row in rows){
        if(epcSet[row.epc] != null){
            continue
        }
        epcSet[row.epc] = 1
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
