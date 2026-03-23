{
  "properties" : { },
  "id" : "filemgrupload001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "上传文件",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/upload",
  "method" : "POST",
  "parameters" : [ {
    "name" : "uploadPath",
    "value" : "/",
    "description" : "上传目标路径",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "/",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "storageKey",
    "value" : null,
    "description" : "存储源标识（可选）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
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
  "description" : "上传文件到指定路径",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import request
import file

// 获取路径参数
if(StrUtil.isBlank(uploadPath)){
    uploadPath = '/'
}

// 确保路径以 / 结尾
if(!uploadPath.endsWith('/')){
    uploadPath = uploadPath + '/'
}

// 获取上传的文件
var uploadFile = request.getFile('file')
if(uploadFile == null || uploadFile.isEmpty()){
    exit 0, '请选择要上传的文件'
}

// 选择存储源
var fileModule = file
if(StrUtil.isNotBlank(storageKey)){
    fileModule = file.use(storageKey)
}

// 使用 file 模块上传文件
var result = fileModule.upload(uploadPath, uploadFile)

if(result == null){
    exit 0, '上传失败'
}

return {
    id: result.getId(),
    fileName: result.getFileName(),
    filePath: result.getFilePath(),
    fileSize: result.getFileSize(),
    contentType: result.getContentType(),
    url: result.getUrl()
}
