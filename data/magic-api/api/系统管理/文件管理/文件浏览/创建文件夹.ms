{
  "properties" : { },
  "id" : "filemgrmkdir001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "创建文件夹",
  "createTime" : null,
  "updateTime" : 1774259930878,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/mkdir",
  "method" : "POST",
  "parameters" : [ {
    "name" : "parentPath",
    "value" : "/",
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
    "value" : "testmaou",
    "description" : "文件夹名称",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "文件夹名称不能为空",
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"name\": \"testmaou\",\n        \"folderPath\": \"/testmaou/\",\n        \"parentPath\": \"/\",\n        \"isDirectory\": true\n    },\n    \"timestamp\": 1774256618646,\n    \"executeTime\": 31317\n}",
  "description" : "创建文件夹",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : {
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
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "success",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "name",
        "value" : "testmaou",
        "description" : "",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "folderPath",
        "value" : "/testmaou/",
        "description" : "",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "parentPath",
        "value" : "/",
        "description" : "",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "isDirectory",
        "value" : "true",
        "description" : "",
        "required" : false,
        "dataType" : "Boolean",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1774256618646",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "31317",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import cn.hutool.core.util.StrUtil
import file

// 验证文件夹名称
if(StrUtil.isBlank(folderName)){
    exit 0, '文件夹名称不能为空'
}

// 获取父路径参数
if(StrUtil.isBlank(parentPath)){
    parentPath = '/'
}

// 确保父路径以 / 结尾
if(!parentPath.endsWith('/')){
    parentPath = parentPath + '/'
}

// 检查文件夹名称是否合法
var invalidChars = ['/', '\\', ':', '*', '?', '"', '<', '>', '|']
for(char in invalidChars){
    if(folderName.contains(char)){
        exit 0, '文件夹名称包含非法字符: ' + char
    }
}

// 构建文件夹路径（以 / 结尾表示目录）
var folderPath = parentPath + folderName + '/'

// 检查是否已存在
if(file.exists(folderPath)){
    exit 0, '文件夹已存在'
}

// 使用 file.mkdir 方法创建文件夹
var result = file.mkdir(parentPath, folderName)

return {
    name: folderName,
    folderPath: folderPath,
    parentPath: parentPath,
    isDirectory: result
}
