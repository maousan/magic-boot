{
  "properties" : { },
  "id" : "filemgrrename001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "重命名文件",
  "createTime" : null,
  "updateTime" : 1774532140489,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/rename",
  "method" : "POST",
  "parameters" : [ {
    "name" : "filePath",
    "value" : "",
    "description" : "原文件或目录路径",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "文件路径不能为空",
    "expression" : null,
    "children" : null
  }, {
    "name" : "newName",
    "value" : "",
    "description" : "新名称",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "新名称不能为空",
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
  "description" : "重命名文件或目录",
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
if(StrUtil.isBlank(newName)){
    exit 0, '新名称不能为空'
}

var invalidChars = ['/', '\\', ':', '*', '?', '"', '<', '>', '|']
for(char in invalidChars){
    if(newName.contains(char)){
        exit 0, '名称包含非法字符: ' + char
    }
}

var storageKeyValues = request.getValues('storageKey')
var resolvedStorageKey = (storageKeyValues != null && storageKeyValues.size() > 0) ? storageKeyValues[0] : null
var module = StrUtil.isNotBlank(resolvedStorageKey) ? file.use(resolvedStorageKey) : file
if(!module.exists(filePath)){
    exit 0, '文件不存在'
}

var lastSlash = filePath.lastIndexOf('/')
var parentPath = lastSlash >= 0 ? filePath.substring(0, lastSlash + 1) : '/'
var newFilePath = parentPath + newName
if(filePath.endsWith('/')){
    newFilePath = newFilePath + '/'
}

if(module.exists(newFilePath)){
    exit 0, '已存在同名文件'
}

module.move(filePath, newFilePath)

return {
    oldFilePath: filePath,
    newFilePath: newFilePath,
    newName: newName
}