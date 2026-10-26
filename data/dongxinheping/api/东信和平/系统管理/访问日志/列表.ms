{
  "properties" : { },
  "id" : "68a459ded74b4232840fd4a2e6993070",
  "script" : null,
  "groupId" : "bb5ffce7c9a74a6a962e38a8e12b7262",
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
    "description" : "请求路径关键字（模糊），可选",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "state",
    "value" : null,
    "description" : "状态过滤：ok / error，可选",
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
  "description" : "分页查询接口访问日志（magic_access_log，写操作+异常请求，AccessLogInterceptor 异步写入）；列表不含参数全文，详情接口查",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let sql = """
    select id, request_path, request_method, user_id, ip_address, execute_time, state, exception_message, create_time
    from magic_access_log
    <where>
        <if test="keyword != null and keyword != ''">
            and request_path like #{keyword}
        </if>
        <if test="state != null and state != ''">
            and state = #{state}
        </if>
    </where>
    order by create_time desc
"""

return db.page(sql)
