{
  "properties" : { },
  "id" : "listappver00000000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "版本列表",
  "createTime" : null,
  "updateTime" : 1779947854564,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "keyword",
    "value" : null,
    "description" : "搜索关键词（匹配版本名称或更新说明）",
    "required" : false,
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
    "description" : "该接口需要登录才允许访问",
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
  "responseBody" : null,
  "description" : "分页查询版本列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, version_code, version_name, apk_url, apk_size,
           force_update, description, create_date
    from app_version
    <where>
        is_del = 0
        <if test="keyword != null and keyword != ''">
            and (version_name like concat('%', #{keyword}, '%')
                 or description like concat('%', #{keyword}, '%'))
        </if>
    </where>
    order by version_code desc
"""

return db.page(sql)