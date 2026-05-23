{
  "properties" : { },
  "id" : "lc20260522004bcdefghijklmnopqrsw",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除LED颜色",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-color",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "id",
    "value" : null,
    "description" : "记录ID",
    "required" : true,
    "dataType" : "Long",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "根据ID删除LED颜色映射记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if (id == null) {
    exit 0, 'ID不能为空'
}

let rows = db.update("""
    delete from t_led_color where id = #{id}
""", { id: id })

if (rows == 0) {
    exit 0, '记录不存在'
}

return '删除成功'