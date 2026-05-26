{
  "properties" : { },
  "id" : "delepc001a2b3c4d5e6f7a8b9c0d1e2f3",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "删除",
  "createTime" : null,
  "updateTime" : 1779772699216,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/delete",
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
  "requestBody" : "{\r\n    \"id\":\"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "删除批次EPC绑定记录",
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

let record = db.selectOne("select id, batch_id, epc from t_batch_epc where id = #{id}", {id: id})

if(record == null){
    exit 404, '记录不存在'
}

db.update("delete from t_batch_epc where id = #{id}", {id: id})

log.info('batch-epc delete: id=' + id + ', batchId=' + record.batch_id + ', epc=' + record.epc)

return {
    success: true,
    message: '删除成功'
}
