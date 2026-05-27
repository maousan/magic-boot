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
    "description" : "绑定记录ID",
    "required" : true,
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
  "description" : "call AIMS unlink by labelCode only, no local db operation",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import '@/aims/labels/unlinkArticleAndLabel' as unlinkArticleAndLabel


if(id == null || id == ''){
    exit 400, 'id不能为空'
}

let record = db.selectOne("""
    select label_code from t_location_label where id = #{id}
""", { id: id })

if(record == null){
    exit 404, '绑定记录不存在'
}

const { success, message } = unlinkArticleAndLabel(record.labelCode)

if (!success) {
    exit 500, message
}

if (success) {
    db.table("t_location_label").where().eq("id", id).delete()
    log.info('unlink location-label mapping success, labelCode=' + labelCode + ', status=' + unlinkStatus)
}


return success
