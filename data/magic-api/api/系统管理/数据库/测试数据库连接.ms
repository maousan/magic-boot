{
  "properties" : { },
  "id" : "4a19023b3a804a0b8d158f268c4964dd",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "测试数据库连接",
  "createTime" : 1773054859761,
  "updateTime" : 1773482192920,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/test/{name}",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ {
    "name" : "name",
    "value" : "bamboo",
    "description" : null,
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1773078239018,\n    \"executeTime\": null\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================

const row = anyline.query("select * from sys_database", `name:custom_${name}`)

try {
    if (!row) {
        return anyline.test(name)
    }
    return anyline.test(row)
} catch(err) {
    exit 0, err
}