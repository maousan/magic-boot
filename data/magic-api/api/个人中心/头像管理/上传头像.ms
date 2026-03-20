{
  "properties" : { },
  "id" : "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
  "script" : null,
  "groupId" : "profileavatar001",
  "name" : "上传头像",
  "createTime" : null,
  "updateTime" : 1773989461522,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/avatar/update",
  "method" : "POST",
  "parameters" : [ {
    "name" : "file",
    "value" : null,
    "description" : null,
    "required" : false,
    "dataType" : "MultipartFile",
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
  "responseBody" : "{\n    \"code\": 0,\n    \"message\": \"请选择要上传的头像文件\",\n    \"data\": null,\n    \"timestamp\": 1773989150505,\n    \"executeTime\": 14\n}",
  "description" : "上传用户头像",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil
import request
import java.util.Base64
import java.io.IOException

var userId = StpUtil.getLoginId()

// 获取上传的文件
var file = request.getFile('file')
if(!file){
    exit 0, '请选择要上传的头像文件'
}

// 验证文件大小（限制为 2MB）
if(file.getSize() > 2 * 1024 * 1024){
    exit 0, '头像文件大小不能超过 2MB'
}

// 获取文件内容并转换为 base64
var bytes
try {
    bytes = file.getBytes()
} catch(e) {
    exit 0, '读取文件失败：' + e.message
}

// 转换为 base64 编码
var base64Image = Base64.getEncoder().encodeToString(bytes)

// 获取文件 MIME 类型（默认为 image/jpeg）
var contentType = file.getContentType()
if(!contentType || !contentType.startsWith('image/')){
    contentType = 'image/jpeg'
}

// 组装完整的 base64 数据 URL
var avatarData = 'data:' + contentType + ';base64,' + base64Image

// 更新用户头像（保存 base64 编码）
db.table("sys_user").primary("id").update({
    id: userId,
    avatar: avatarData
}, true)

return avatarData
