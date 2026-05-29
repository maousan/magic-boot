{
  "properties" : { },
  "id" : "saveappver00000000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "新增版本",
  "createTime" : null,
  "updateTime" : 1779947885302,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ {
    "name" : "versionCode",
    "value" : null,
    "description" : "版本号（整数，用于版本比对）",
    "required" : true,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "versionName",
    "value" : null,
    "description" : "版本名称（如 1.1.0）",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "forceUpdate",
    "value" : null,
    "description" : "是否强制更新（默认 false）",
    "required" : false,
    "dataType" : "Boolean",
    "type" : null,
    "defaultValue" : "false",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "description",
    "value" : null,
    "description" : "更新说明",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "上传APK并新增版本记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.IdUtil
import org.ssssssss.magicboot.utils.FileUtils
import java.text.DecimalFormat

var apkFile = request.getFile('file')
if(apkFile == null || apkFile.isEmpty()){
    exit 0, '请上传APK文件'
}

if(!versionCode){
    exit 0, '版本号不能为空'
}

if(!versionName){
    exit 0, '版本名称不能为空'
}

var apkUrl = FileUtils.saveFile(apkFile)
var df = new DecimalFormat("#.#")
var apkSizeStr = df.format(apkFile.getSize() / 1024.0 / 1024.0) + "MB"

var id = IdUtil.simpleUUID()

db.table('app_version').primary('id').save({
    id: id,
    version_code: versionCode,
    version_name: versionName,
    apk_url: apkUrl,
    apk_size: apkSizeStr,
    force_update: forceUpdate ? 1 : 0,
    description: description ?: '',
    is_del: 0
})