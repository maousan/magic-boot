{
  "properties" : { },
  "id" : "dellabelmap202606030001abcdefghi",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除标签绑定记录",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label-mapping/delete",
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
  "requestBody" : "{\n  \"id\": \"\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"message\": \"删除成功\"\n}",
  "description" : "仅删除本地库位-标签绑定记录，不调用AIMS解绑接口",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let id = body.id

if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

id = (id + '').trim()

let record = db.selectOne("select id, location_code, label_code from t_location_label where id = #{id}", {id: id})

if(record == null){
    exit 404, '绑定记录不存在'
}

db.update("delete from t_location_label where id = #{id}", {id: id})

log.info('delete location-label mapping only in db: id=' + id + ', locationCode=' + record.location_code + ', labelCode=' + record.label_code)

return {
    success: true,
    message: '删除成功'
}
