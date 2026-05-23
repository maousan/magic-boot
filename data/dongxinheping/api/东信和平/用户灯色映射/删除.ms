{
  "properties" : { },
  "id" : "ulc20260522001del1234567890abcdefg",
  "script" : null,
  "groupId" : "ulc20260522001mgmt1234567890abcdef",
  "name" : "删除",
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
  "requestBody" : "{\n  \"id\": 1\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "删除用户灯光颜色映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null){
    exit 400, 'id不能为空'
}

db.update("delete from t_user_light_color where id = #{id}", { id: id })

return '删除成功'
