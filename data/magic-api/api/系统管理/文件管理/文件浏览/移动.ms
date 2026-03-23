{
  "properties" : { },
  "id" : "filemgrmove001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "移动",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/move",
  "method" : "POST",
  "parameters" : [ {
    "name" : "sourcePath",
    "value" : "",
    "description" : "源文件路径",
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
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "移动文件到新位置",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file

// 验证参数
if(StrUtil.isBlank(sourcePath)){
    exit 0, '源路径不能为空'
}
if(StrUtil.isBlank(targetDirPath)){
    exit 0, '目标目录不能为空'
}

// 检查源文件是否存在
if(!file.exists(sourcePath)){
    exit 0, '源文件不存在'
}

// 确保目标路径以 / 结尾
if(!targetDirPath.endsWith('/')){
    targetDirPath = targetDirPath + '/'
}

// 获取文件名
var fileName = sourcePath
var lastSlash = sourcePath.lastIndexOf('/')
if(lastSlash >= 0){
    fileName = sourcePath.substring(lastSlash + 1)
}

// 构建目标文件路径
var targetFilePath = targetDirPath + fileName

// 如果是目录，保持目录标记
if(sourcePath.endsWith('/')){
    targetFilePath = targetFilePath + '/'
}

// 检查目标是否已存在
if(file.exists(targetFilePath)){
    exit 0, '目标位置已存在同名文件'
}

// 使用 file 模块移动文件
var result = file.move(sourcePath, targetFilePath)

if(result == null){
    exit 0, '移动失败'
}

return {
    sourcePath: sourcePath,
    targetDirPath: targetDirPath,
    targetFilePath: targetFilePath,
    url: result.getUrl()
}
