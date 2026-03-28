{
  "properties" : { },
  "id" : "filemgrdelete001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "删除文件",
  "createTime" : null,
  "updateTime" : 1774532127578,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/delete",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "filePath",
    "value" : "",
    "description" : "文件或目录路径",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "文件路径不能为空",
    "expression" : null,
    "children" : null
  }, {
    "name" : "storageKey",
    "value" : "",
    "description" : "存储源标识（可选）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "删除指定文件或目录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file
import request

if(StrUtil.isBlank(filePath)){
    exit 0, '文件路径不能为空'
}

var storageKeyValues = request.getValues('storageKey')
var resolvedStorageKey = (storageKeyValues != null && storageKeyValues.size() > 0) ? storageKeyValues[0] : null
var module = StrUtil.isNotBlank(resolvedStorageKey) ? file.use(resolvedStorageKey) : file
if(!module.exists(filePath)){
    exit 0, '文件不存在'
}

var success = module.delete(filePath)
if(!success){
    exit 0, '删除失败'
}

return {
    filePath: filePath,
    deleted: true
}