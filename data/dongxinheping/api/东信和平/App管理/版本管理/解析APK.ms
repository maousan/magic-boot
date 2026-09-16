{
  "properties" : { },
  "id" : "parseapkfile00000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "解析APK",
  "createTime" : null,
  "updateTime" : 1784704489316,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/parse-apk",
  "method" : "POST",
  "parameters" : [ ],
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
  "description" : "解析APK的manifest，返回versionCode/versionName/packageName，供前端上传后自动填充（参考litepos OssController）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import net.dongliu.apk.parser.ApkFile
import java.io.File
import java.text.DecimalFormat

var file = request.getFile('file')
if(file == null || file.isEmpty()){
    exit 1, '请上传APK文件'
}

var tmp = File.createTempFile('parse-apk-', '.apk')
try {
    file.transferTo(tmp)

    var meta
    try {
        var parser = new ApkFile(tmp)
        try {
            meta = parser.getApkMeta()
        } finally {
            parser.close()
        }
    } catch (e) {
        exit 1, '无法解析APK，文件可能损坏或不是有效APK'
    }

    if(meta == null || meta.getVersionCode() == null){
        exit 1, 'APK缺少versionCode，文件异常'
    }

    var df = new DecimalFormat("#.#")
    return {
        versionCode: meta.getVersionCode(),
        versionName: meta.getVersionName() ?: '',
        packageName: meta.getPackageName() ?: '',
        apkSize: df.format(file.getSize() / 1024.0 / 1024.0) + 'MB'
    }
} finally {
    tmp.delete()
}
