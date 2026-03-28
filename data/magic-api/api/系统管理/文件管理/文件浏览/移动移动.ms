{
  "properties" : { },
  "id" : "filemgrmove001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "移动移动",
  "createTime" : null,
  "updateTime" : 1774532134560,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/move",
  "method" : "POST",
  "parameters" : [ {
    "name" : "sourcePath",
    "value" : "",
    "description" : "源文件或目录路径",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "源路径不能为空",
    "expression" : null,
    "children" : null
  }, {
    "name" : "targetDirPath",
    "value" : "",
    "description" : "目标目录路径",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "目标目录不能为空",
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
  "description" : "移动文件或目录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file
import request

if(StrUtil.isBlank(sourcePath)){
    exit 0, '源路径不能为空'
}
if(StrUtil.isBlank(targetDirPath)){
    exit 0, '目标目录不能为空'
}

var storageKeyValues = request.getValues('storageKey')
var resolvedStorageKey = (storageKeyValues != null && storageKeyValues.size() > 0) ? storageKeyValues[0] : null
var module = StrUtil.isNotBlank(resolvedStorageKey) ? file.use(resolvedStorageKey) : file
if(!module.exists(sourcePath)){
    exit 0, '源文件不存在'
}

if(!targetDirPath.endsWith('/')){
    targetDirPath = targetDirPath + '/'
}

var fileName = sourcePath
var lastSlash = sourcePath.lastIndexOf('/')
if(lastSlash >= 0){
    fileName = sourcePath.substring(lastSlash + 1)
}

var targetFilePath = targetDirPath + fileName
if(sourcePath.endsWith('/')){
    targetFilePath = targetFilePath + '/'
}

if(module.exists(targetFilePath)){
    exit 0, '目标位置已存在同名文件'
}

module.move(sourcePath, targetFilePath)

return {
    sourcePath: sourcePath,
    targetDirPath: targetDirPath,
    targetFilePath: targetFilePath
}