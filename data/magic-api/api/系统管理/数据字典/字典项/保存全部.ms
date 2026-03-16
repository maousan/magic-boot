{
  "properties" : { },
  "id" : "17a045af44234948a4324694c3b6b6bb",
  "script" : null,
  "groupId" : "376f26eb43a44a3daeafd27020a96f48",
  "name" : "保存全部",
  "createTime" : 1773568016228,
  "updateTime" : 1773568048316,
  "lock" : "0",
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/save-all",
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
    "value" : "dict:items:save",
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
  "requestBody" : "{\n\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1773048731742,\n    \"executeTime\": null\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

if (body.id) {
    return anyline.update("sys_dict_items", body)
} else {
    return anyline.insert("sys_dict_items", body, true)
}

const state = anyline.beginTrans()

anyline.deletes("sys_dict_tiems", "dict_id", body.dictId)

anyline.commitTrans(state)