{
  "properties" : { },
  "id" : "filemgrrename001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "重命名",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/rename",
  "method" : "POST",
  "parameters" : [ {
    "name" : "filePath",
    "value" : "",
    "description" : "原文件路径",
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
    "description" : "新文件名",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "新名称不能为空",
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "重命名文件或文件夹",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file

// 验证参数
if(StrUtil.isBlank(filePath)){
    exit 0, '文件路径不能为空'
}
if(StrUtil.isBlank(newName)){
    exit 0, '新名称不能为空'
}

// 检查文件是否存在
if(!file.exists(filePath)){
    exit 0, '文件不存在'
}

// 检查新名称是否合法
var invalidChars = ['/', '\\', ':', '*', '?', '"', '<', '>', '|']
for(char in invalidChars){
    if(newName.contains(char)){
        exit 0, '名称包含非法字符: ' + char
    }
}

// 构建新路径
var lastSlash = filePath.lastIndexOf('/')
var parentPath = lastSlash >= 0 ? filePath.substring(0, lastSlash + 1) : '/'
var newFilePath = parentPath + newName

// 如果是目录，保持目录标记
if(filePath.endsWith('/')){
    newFilePath = newFilePath + '/'
}

// 检查目标是否已存在
if(file.exists(newFilePath)){
    exit 0, '已存在同名文件'
}

// 使用 file 模块移动文件（重命名）
var result = file.move(filePath, newFilePath)

if(result == null){
    exit 0, '重命名失败'
}

return {
    oldFilePath: filePath,
    newFilePath: newFilePath,
    newName: newName,
    url: result.getUrl()
}
