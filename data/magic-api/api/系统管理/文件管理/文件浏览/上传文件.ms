{
  "properties" : { },
  "id" : "filemgrupload001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "上传文件",
  "createTime" : null,
  "updateTime" : 1774532112235,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/upload",
  "method" : "POST",
  "parameters" : [ {
    "name" : "uploadPath",
    "value" : "/",
    "description" : "Upload target directory",
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
    "description" : "Storage key",
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
  "description" : "Upload file to target path",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil
import request
import file

if(StrUtil.isBlank(uploadPath)){
    uploadPath = '/'
}

if(!uploadPath.endsWith('/')){
    uploadPath = uploadPath + '/'
}

var uploadFile = request.getFile('file')
if(uploadFile == null || uploadFile.isEmpty()){
    exit 0, '请选择要上传的文件'
}

var fileModule = file
if(StrUtil.isNotBlank(storageKey)){
    fileModule = file.use(storageKey)
}

var originalFilename = uploadFile.getOriginalFilename()
if(StrUtil.isBlank(originalFilename)){
    originalFilename = 'upload.bin'
}

var result = fileModule.upload(uploadPath, uploadFile.getInputStream(), originalFilename)

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