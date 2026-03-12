{
  "properties" : { },
  "id" : "03c2c9614e5b47f48f61f4f7759fe353",
  "script" : null,
  "groupId" : "8295fc13678d4144bf7363c465247a50",
  "name" : "保存",
  "createTime" : null,
  "updateTime" : 1773054567842,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "wrap_request_parameter",
    "value" : "data",
    "description" : "包装请求参数到一个变量中",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "permission",
    "value" : "office:save",
    "description" : "允许拥有该权限的访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1772884876861,\n    \"executeTime\": null\n}",
  "description" : "",
  "requestBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ ]
  },
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.spring.SpringUtil
import org.anyline.entity.DataRow

if (body.id !== null and body.id !== '') {
    if (body.id === body.pid) {
        exit 0, '不能设置上级部门为自己'
    }
    const dataRow = anyline.query("sys_office", `id:${body.id}`)
    dataRow.putAll(body)
    dataRow.put("update_date", now())
    return anyline.update("sys_office", dataRow, "pid", "type", "status", "name", "remark")
} else {
    if (body.pid === null || body.pid === '') {
        body.pid = '0'    
    }
    const dataRow = new DataRow()
    dataRow.putAll(body)
    dataRow.put("create_date", now())
    
    return anyline.insert("sys_office", dataRow, true)
}