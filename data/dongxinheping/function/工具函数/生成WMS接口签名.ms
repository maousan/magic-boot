{
  "properties" : { },
  "id" : "e3c3e1ffb8e243eebc51b57e883d2197",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "生成WMS接口签名",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/buildWmsInterfaceSign",
  "description" : "生成WMS三方接口签名：MD5+Base64+大写+URL编码",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "appSecret",
    "value" : null,
    "description" : "签名密钥",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "dataText",
    "value" : null,
    "description" : "待签名业务报文字符串",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import java.util.Base64
import java.security.MessageDigest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

if(appSecret == null || appSecret == ''){
    return {
        success: false,
        message: 'appSecret不能为空'
    }
}
if(dataText == null || dataText == ''){
    return {
        success: false,
        message: 'dataText不能为空'
    }
}

let md5Hex = (text) => {
    let digest = MessageDigest.getInstance('MD5').digest((text + '').getBytes(StandardCharsets.UTF_8))
    let hex = ''
    for(item in digest){
        let part = Integer.toHexString(item & 0xff)
        if(part.length() == 1){
            hex = hex + '0'
        }
        hex = hex + part
    }
    return hex
}

let md5Text = md5Hex(appSecret + dataText + appSecret)
let base64Text = Base64.getEncoder().encodeToString(md5Text.getBytes(StandardCharsets.UTF_8))
let sign = URLEncoder.encode(base64Text.toUpperCase(), StandardCharsets.UTF_8.name())

return {
    success: true,
    sign: sign,
    md5: md5Text
}
