{
  "properties" : { },
  "id" : "6888bcaeee7b4bf2b4dc689e38170f4a",
  "script" : null,
  "groupId" : "bb5ffce7c9a74a6a962e38a8e12b7262",
  "name" : "详情",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/detail",
  "method" : "GET",
  "parameters" : [ {
    "name" : "id",
    "value" : null,
    "description" : "记录ID",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "查访问日志单条完整记录（含参数全文 request_params）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let row = db.selectOne("""
    select id, request_path, request_method, request_params, user_id, ip_address, execute_time, state, exception_message, create_time
    from magic_access_log
    where id = #{id}
""", {id: id})

if(row == null){
    exit 404, '记录不存在'
}

return row
