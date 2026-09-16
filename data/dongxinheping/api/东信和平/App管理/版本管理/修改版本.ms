{
  "properties" : { },
  "id" : "updappver000000000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "修改版本",
  "createTime" : null,
  "updateTime" : 1779947887527,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/update",
  "method" : "POST",
  "parameters" : [ {
    "name" : "id",
    "value" : null,
    "description" : "版本记录ID",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "versionCode",
    "value" : null,
    "description" : "版本号",
    "required" : false,
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
    "description" : "版本名称",
    "required" : false,
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
    "description" : "是否强制更新",
    "required" : false,
    "dataType" : "Boolean",
    "type" : null,
    "defaultValue" : null,
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
  "description" : "修改版本信息，可选择同时替换APK文件",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.HexUtil
import org.ssssssss.magicboot.utils.FileUtils
import java.security.MessageDigest
import java.text.DecimalFormat

if(!id){
    exit 0, '版本ID不能为空'
}

var data = {id: id}

if(versionCode != null){
    data.version_code = versionCode
}
if(versionName != null && versionName != ''){
    data.version_name = versionName
}
if(forceUpdate != null){
    data.force_update = forceUpdate ? 1 : 0
}
if(description != null){
    data.description = description
}

var apkFile = request.getFile('file')
if(apkFile != null && !apkFile.isEmpty()){
    data.apk_url = FileUtils.saveFile(apkFile).url
    var df = new DecimalFormat("#.#")
    data.apk_size = df.format(apkFile.getSize() / 1024.0 / 1024.0) + "MB"
    var apkBytes = apkFile.getBytes()
    data.sha256 = HexUtil.encodeHexStr(MessageDigest.getInstance('SHA-256').digest(apkBytes), false)
    data.md5 = HexUtil.encodeHexStr(MessageDigest.getInstance('MD5').digest(apkBytes), false)
}

db.table('t_app_version').primary('id').update(data, true)