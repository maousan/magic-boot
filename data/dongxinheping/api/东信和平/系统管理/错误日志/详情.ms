{
  "properties" : { },
  "id" : "2a2c260043c74ff9a02588f133b675a8",
  "script" : null,
  "groupId" : "7953230a18d442449d373a48b0ff3e35",
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
  "description" : "查错误日志单条完整记录（含参数、请求头、堆栈全文）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let row = db.selectOne("""
    select id, request_path, request_method, request_params, request_headers, user_id, ip_address, exception_class, exception_message, stack_trace, create_time
    from magic_error_log
    where id = #{id}
""", {id: id})

if(row == null){
    exit 404, '记录不存在'
}

return row
