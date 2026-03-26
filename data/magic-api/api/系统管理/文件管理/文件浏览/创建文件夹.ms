{
  "properties" : { },
  "id" : "filemgrmkdir001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "创建文件夹",
  "createTime" : null,
  "updateTime" : 1774488709815,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/mkdir",
  "method" : "POST",
  "parameters" : [ {
    "name" : "parentPath",
    "value" : "/111/",
    "description" : "父目录路径",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "/",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "folderName",
    "value" : "2222",
    "description" : "文件夹名称",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "文件夹名称不能为空",
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
  "description" : "创建文件夹（仅落库逻辑目录）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file
import request

if(StrUtil.isBlank(folderName)){
    exit 0, '文件夹名称不能为空'
}

if(StrUtil.isBlank(parentPath)){
    parentPath = '/'
}

if(!parentPath.endsWith('/')){
    parentPath = parentPath + '/'
}

var invalidChars = ['/', '\\', ':', '*', '?', '"', '<', '>', '|']
for(char in invalidChars){
    if(folderName.contains(char)){
        exit 0, '文件夹名称包含非法字符: ' + char
    }
}

var folderPath = parentPath + folderName + '/'
var storageKeyValues = request.getValues('storageKey')
var resolvedStorageKey = (storageKeyValues != null && storageKeyValues.size() > 0) ? storageKeyValues[0] : null
var module = StrUtil.isNotBlank(resolvedStorageKey) ? file.use(resolvedStorageKey) : file

if(module.exists(folderPath)){
    exit 0, '文件夹已存在'
}

var success = module.mkdir(parentPath, folderName)
if(!success){
    exit 0, '创建文件夹失败'
}

return {
    name: folderName,
    folderPath: folderPath,
    parentPath: parentPath,
    isDirectory: true
}