{
  "properties" : { },
  "id" : "67c510fcb7164d269dda8af6c0f2ec8c",
  "script" : null,
  "groupId" : "ba5ca17f802b4860999fd18363034a6b",
  "name" : "简单导出",
  "createTime" : 1772760736497,
  "updateTime" : 1772780257435,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/export1",
  "method" : "GET",
  "parameters" : [ ],
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
  "description" : "测试接口",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import excel
import response

const bytes = excel.write([
    {
        'a': 1,
        'b': 2
    },
    {
        'a': 3,
        'b': 4
    }
])

return response.download(bytes, `test_${now().format('yyyyMMddHHmmssSSS')}.xlsx`)