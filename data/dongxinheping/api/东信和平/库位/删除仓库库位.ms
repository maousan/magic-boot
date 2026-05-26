{
  "properties" : { },
  "id" : "wl202605260001deletewarehouseabc",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除仓库库位",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/warehouse-location/delete",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "id",
    "value" : null,
    "description" : "库位记录ID",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": \"删除成功\"\n}",
  "description" : "根据ID删除仓库库位记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(id == null || (id + '').trim() == ''){
    exit 0, '库位记录ID不能为空'
}

id = (id + '').trim()

let rows = db.update("""
    delete from t_location_warehouse
    where id = #{id}
""", {
    id: id
})

if(rows == 0){
    exit 0, '库位记录不存在'
}

return '删除成功'
