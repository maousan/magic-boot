{
  "properties" : { },
  "id" : "4f44fe7934484ca3b09bc66ea9f329aa",
  "script" : null,
  "groupId" : "7953230a18d442449d373a48b0ff3e35",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "keyword",
    "value" : null,
    "description" : "请求路径/异常类关键字（模糊），可选",
    "required" : false,
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
  "description" : "分页查询异常错误日志（magic_error_log，ExceptionResultProvider 异常时落库）；参数/请求头/堆栈全文走详情接口",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let sql = """
    select id, request_path, request_method, user_id, ip_address, exception_class, exception_message, create_time
    from magic_error_log
    <where>
        <if test="keyword != null and keyword != ''">
            and (request_path like #{keyword} or exception_class like #{keyword})
        </if>
    </where>
    order by create_time desc
"""

return db.page(sql)
