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
import '@/sys-config/getValue' as getSysConfig

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
// 2026-10-19 生产实测：操作员把外箱二维码扫进 EPC 输入框（350+字符）导致 Data too long for column 'epc'，
// 专拦二维码特征输入，把 500 变成可操作的提示
if(epc.indexOf(':"') >= 0 || epc.indexOf(';') >= 0 || (epc + '').length() > 64){
    exit 400, 'EPC不能是外箱二维码，请扫描RFID标签'
}

locationId = (locationId + '').trim()
raw = (raw + '').trim()
epc = (epc + '').trim()

let parsed = parseLot(raw)
let sku = parsed.MtlCode
let lotNo = parsed.Lot
// 2026-10-21 新增：记录物料名称（外箱二维码 MtlDesc 键，parseLot 通用解析直接取）
let materialName = parsed.MtlDesc == null ? null : (parsed.MtlDesc + '').trim()

if(sku == null || (sku + '').trim() == ''){
    exit 400, '二维码中未找到MtlCode'
}
if(lotNo == null || (lotNo + '').trim() == ''){
    exit 400, '二维码中未找到Lot'
}

sku = (sku + '').trim()
lotNo = (lotNo + '').trim()
let batchId = locationId + '_' + sku + '_' + lotNo

// 2026-10-16 改因：①驼峰别名（batch_id as batchId）经全局 camel 转换变全小写，row.batchId 恒 null，
// "批次ID已绑定EPC"拦截与幂等判断自 09-21(cb5d893) 起是死代码；②按业务口径固化"1标1箱（活跃）、1箱多标"：
// 查重只按 EPC——同 EPC 同箱幂等、同 EPC 异箱改绑、不同 EPC 同箱直接插新行
// ---- 旧查询与死掉的 400 分支（注释保留，回滚取消注释） ----
// let existing = db.select("""
//     select id, batch_id as batchId, epc, location_id as locationId, sku, lot_no as lotNo from t_batch_epc where status = 1 and (batch_id = #{batchId} or epc = #{epc})
// """, {batchId: batchId, epc: epc})
//
// let epcRow = null
// if(existing != null && existing.size() > 0){
//     for(row in existing){
//         if(row.epc == epc){
//             epcRow = row
//         }else if(row.batchId == batchId){
//             exit 400, '批次ID已绑定EPC: ' + row.epc
//         }
//     }
// }
let existing = db.select("""
    select id, batch_id, epc, location_id, sku, lot_no, material_name from t_batch_epc where status = 1 and epc = #{epc}
""", { epc: epc })

let epcRow = null
if(existing != null && existing.size() > 0){
    epcRow = existing.get(0)
}

// 覆盖更新：EPC 已绑定其他批次时，将该记录改绑到新 batchId
if(epcRow != null && epcRow.batchId == batchId){
    log.info('batch-epc bind idempotent: batchId=' + batchId + ', epc=' + epc)
    return {
        id: epcRow.id,
        batchId,
        epc,
        locationId: epcRow.locationId,
        sku: epcRow.sku,
        lotNo: epcRow.lotNo,
        materialName: epcRow.materialName,
        bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: epcRow.id})
    }
}

// 2026-10-21 新增：严格1箱1标配置存 sys_configure（key: batch-epc.strict-one-label，值 true/false，无记录视为 false=一箱多标）
// 开启后该批次已绑其它活跃 EPC 时拒绝——插新行与 EPC 改绑到该批次共用此拦截
// 2026-10-21 改因：配置读取改走 getSysConfig 函数（进程内 60s TTL 缓存，系统配置页保存后即时刷新），避免每次绑定直查配置表
// let strictOne = db.selectValue("select configure_value from sys_configure where configure_key = 'batch-epc.strict-one-label' and is_del = 0")
let strictOne = getSysConfig('batch-epc.strict-one-label', false)
if((strictOne + '') == 'true'){
    let boxOthers = db.select("""
        select id, epc from t_batch_epc where status = 1 and batch_id = #{batchId} and epc != #{epc}
    """, { batchId: batchId, epc: epc })
    if(boxOthers != null && boxOthers.size() > 0){
        exit 400, '严格1箱1标模式：该批次已绑定其它EPC ' + boxOthers.get(0).epc
    }
}

if(epcRow != null){
    db.update("""
        update t_batch_epc set batch_id = #{batchId}, location_id = #{locationId}, sku = #{sku}, lot_no = #{lotNo}, material_name = #{materialName}, bind_time = now() where id = #{id}
    """, {id: epcRow.id, batchId: batchId, locationId: locationId, sku: sku, lotNo: lotNo, materialName: materialName})
    log.info('batch-epc rebind: id=' + epcRow.id + ', oldBatchId=' + epcRow.batchId + ', newBatchId=' + batchId + ', epc=' + epc)
    return {
        id: epcRow.id,
        batchId,
        epc,
        locationId,
        sku,
        lotNo,
        materialName,
        bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: epcRow.id})
    }
}

let id = IdUtil.fastSimpleUUID()
db.update("""
    insert into t_batch_epc (id, batch_id, epc, status, bind_time, location_id, sku, lot_no, material_name)
    values (#{id}, #{batchId}, #{epc}, 1, now(), #{locationId}, #{sku}, #{lotNo}, #{materialName})
""", {id: id, batchId: batchId, epc: epc, locationId: locationId, sku: sku, lotNo: lotNo, materialName: materialName})

log.info('batch-epc bind: batchId=' + batchId + ', epc=' + epc)

return {
    id,
    batchId,
    epc,
    locationId,
    sku,
    lotNo,
    materialName,
    bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: id})
}