{
  "properties" : { },
  "id" : "filemgrdownload001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "下载",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/download",
  "method" : "GET",
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
  "description" : "下载指定文件",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import java.net.URLEncoder
import cn.hutool.core.util.StrUtil
import response
import file

// 验证路径参数
if(StrUtil.isBlank(filePath)){
    exit 0, '文件路径不能为空'
}

// 不能下载目录
if(filePath.endsWith('/')){
    exit 0, '不支持下载文件夹'
}

// 检查文件是否存在
if(!file.exists(filePath)){
    exit 0, '文件不存在'
}

// 获取文件信息
var fileInfo = file.info(filePath)
var fileName = fileInfo != null ? fileInfo.getFilename() : filePath
var lastSlash = filePath.lastIndexOf('/')
if(lastSlash >= 0 && StrUtil.isBlank(fileName)){
    fileName = filePath.substring(lastSlash + 1)
}

var contentType = fileInfo != null ? fileInfo.getContentType() : 'application/octet-stream'

// 使用 file 模块下载文件
var bytes = file.download(filePath)

if(bytes == null || bytes.length == 0){
    exit 0, '下载失败，文件内容为空'
}

// 设置响应头
response.setHeader("Content-Type", contentType)
response.setHeader("Content-Disposition", `attachment;filename="${URLEncoder.encode(fileName, "UTF-8")}"`)
response.setHeader("Content-Length", bytes.length)
response.setHeader("Cache-Control", "no-cache")

// 写入响应流
response.getOutputStream().write(bytes)

return response.end()
