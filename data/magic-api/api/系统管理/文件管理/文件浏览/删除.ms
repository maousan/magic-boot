{
  "properties" : { },
  "id" : "filemgrdelete001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "删除",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/delete",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "filePath",
    "value" : "",
    "description" : "文件路径",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "文件路径不能为空",
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "删除指定文件或文件夹",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import file

// 验证路径参数
if(StrUtil.isBlank(filePath)){
    exit 0, '文件路径不能为空'
}

// 检查文件是否存在
if(!file.exists(filePath)){
    exit 0, '文件不存在'
}

// 获取文件信息
var fileInfo = file.info(filePath)
var fileName = fileInfo != null ? fileInfo.getFilename() : filePath

// 使用 file 模块删除文件
var success = file.delete(filePath)

if(!success){
    exit 0, '删除失败'
}

// 如果是目录，尝试删除目录下的所有文件
if(filePath.endsWith('/')){
    var fileList = file.list(filePath)
    for(item in fileList){
        file.delete(filePath + item.getFilename())
    }
}

return {
    filePath: filePath,
    name: fileName
}
