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
  "options" : [ ],
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
import '@/sys-config/getValue' as getSysConfig

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
// 2026-10-19 生产实测：操作员把外箱二维码扫进 EPC 输入框导致 Data too long，专拦二维码特征输入
if(epc.indexOf(':"') >= 0 || epc.indexOf(';') >= 0 || (epc + '').length() > 64){
    exit 400, 'EPC不能是外箱二维码，请扫描RFID标签'
}

id = (id + '').trim()
batchId = (batchId + '').trim()
epc = (epc + '').trim()

let record = db.selectOne("select id, batch_id, epc from t_batch_epc where id = #{id}", {id: id})

if(record == null){
    exit 404, '记录不存在'
}

// ---- 旧冲突检查（注释保留，回滚取消注释） ----
// 缺陷：conflict.batch_id 从 camel 转换后的行里读下划线键恒为 null，batchId 冲突分支是死代码，
// 且"同箱多标"场景会误落到 EPC 冲突分支报"EPC已绑定批次: null"
// let existing = db.select("""
//     select batch_id, epc from t_batch_epc where status = 1 and id != #{id} and (batch_id = #{batchId} or epc = #{epc})
// """, {id: id, batchId: batchId, epc: epc})
//
// if(existing != null && existing.size() > 0){
//     let conflict = existing.get(0)
//     if(conflict.batch_id == batchId){
//         exit 400, '批次ID已绑定EPC: ' + conflict.epc
//     }
//     exit 400, 'EPC已绑定批次: ' + conflict.batch_id
// }
// 2026-10-16 改因：按业务口径"1箱可多标"去掉 batchId 冲突拦截，仅保留 EPC 冲突（一个标签同时只能挂一条活跃记录）
let epcConflict = db.select("""
    select id from t_batch_epc where status = 1 and id != #{id} and epc = #{epc}
""", {id: id, epc: epc})
if(epcConflict != null && epcConflict.size() > 0){
    exit 400, 'EPC已绑定其他记录'
}

// 2026-10-21 新增：严格1箱1标配置存 sys_configure（key: batch-epc.strict-one-label，值 true/false，无记录视为 false=一箱多标）
// 开启后编辑落到的批次已有其它记录（非本条）时拒绝
// 2026-10-21 改因：配置读取改走 getSysConfig 函数（进程内 60s TTL 缓存，系统配置页保存后即时刷新），避免每次编辑直查配置表
// let strictOne = db.selectValue("select configure_value from sys_configure where configure_key = 'batch-epc.strict-one-label' and is_del = 0")
let strictOne = getSysConfig('batch-epc.strict-one-label', false)
if((strictOne + '') == 'true'){
    let boxOthers = db.select("""
        select id, epc from t_batch_epc where status = 1 and batch_id = #{batchId} and epc != #{epc} and id != #{id}
    """, { batchId: batchId, epc: epc, id: id })
    if(boxOthers != null && boxOthers.size() > 0){
        exit 400, '严格1箱1标模式：该批次已绑定其它EPC ' + boxOthers.get(0).epc
    }
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
