{
  "properties" : { },
  "id" : "77b5ab56b4bd4e04a8824bf17d58e44a",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "解析lot",
  "createTime" : 1775200271549,
  "updateTime" : 1780477809116,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/parseLot",
  "description" : "",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "raw",
    "value" : null,
    "description" : null,
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
import java.util.regex.Pattern
import java.util.LinkedHashMap
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import org.ssssssss.magicapi.utils.JsonUtils

if(!raw){
    exit 400, 'raw不能为空'
}

var decoded = raw
// var decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8.name())
var normalized = decoded == null ? '' : decoded.trim()

var result = new LinkedHashMap()
if(!normalized.startsWith('"')){
    return {
        Lot: normalized
    }
}

// 匹配 "key":"value"
var pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
var matcher = pattern.matcher(normalized)

while (matcher.find()) {
    var key = matcher.group(1)
    var value = matcher.group(2)

    // 处理常见转义
    value = value
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")

    result.put(key, value)
}

// 返回 JSON 字符串
return result
