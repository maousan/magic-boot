{
  "properties" : { },
  "id" : "pku202605220005del1234567890abcdef05",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "删除单据",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
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
  "requestBody" : "{\n  \"id\": \"xxx\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "删除拣货单主表及其明细（级联删除）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

db.update("delete from t_picking_upload_detail where bill_id = #{id}", { id: id })
db.update("delete from t_picking_upload where id = #{id}", { id: id })

return '删除成功'
