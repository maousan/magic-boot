{
  "properties" : { },
  "id" : "8c4a1e7d2b5f4a9c8d6e1f3b2a7c5e4d",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "解绑库位码和标签码",
  "createTime" : 1775542800000,
  "updateTime" : 1779875970650,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label-mapping",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "id",
    "value" : "",
    "description" : "绑定记录ID（与库位码二选一，传入时优先）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "labelCode",
    "value" : "",
    "description" : "标签码（与id二选一，按标签解绑，App端使用）",
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
    "description" : "api access requires login",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": 1,\n  \"timestamp\": 1775542800000,\n  \"executeTime\": 20\n}",
  "description" : "解绑库位码和标签码（调 AIMS 解绑并删本地记录）：id 优先，否则按标签码 labelCode 解绑（query/body 均可）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import '@/aims/labels/unlinkArticleAndLabel' as unlinkArticleAndLabel


// id 优先；否则按标签码解绑（labelCode，query/body 均可）
let deleteId = id == null ? '' : (id + '').trim()
let deleteLabelCode = labelCode == null ? '' : (labelCode + '').trim()
if(deleteLabelCode == '' && body != null && body.labelCode != null){
    deleteLabelCode = (body.labelCode + '').trim()
}

if(deleteId == '' && deleteLabelCode == ''){
    exit 400, 'id与labelCode至少传一个'
}

// 按标签码查出绑定记录，逐条调 AIMS 解绑并删除本地记录
let records = []
if(deleteId != ''){
    let record = db.selectOne("""
        select id, label_code from t_location_label where id = #{id}
    """, { id: deleteId })
    if(record != null){
        records.add(record)
    }
}else{
    let rows = db.select("""
        select id, label_code from t_location_label where label_code = #{labelCode}
    """, { labelCode: deleteLabelCode })
    if(rows != null){
        records = rows
    }
}

if(records.size() == 0){
    exit 404, '绑定记录不存在'
}

let unlinkedCount = 0
for(record in records){
    let labelCode = record.labelCode
    if(labelCode == null || labelCode == ''){
        labelCode = record.label_code
    }
    const { success, message } = unlinkArticleAndLabel(labelCode)
    if (!success) {
        exit 500, message
    }
    db.table("t_location_label").where().eq("id", record.id).delete()
    log.info('unlink location-label mapping success, id=' + record.id + ', labelCode=' + labelCode)
    unlinkedCount = unlinkedCount + 1
}

return unlinkedCount
