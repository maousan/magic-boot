{
  "properties" : { },
  "id" : "420d8cd8df8d4b1aaea3ef9adc853419",
  "script" : null,
  "groupId" : "ba5ca17f802b4860999fd18363034a6b",
  "name" : "指定列标题",
  "createTime" : 1772768890224,
  "updateTime" : 1773470717142,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/export2",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
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
], { 'a': '标题1', 'b': '标题2'})

return response.download(bytes, `test_${now().format('yyyyMMddHHmmssSSS')}.xlsx`)