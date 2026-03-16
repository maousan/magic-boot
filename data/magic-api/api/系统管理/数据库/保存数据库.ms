{
  "properties" : { },
  "id" : "fd1a16c61fd54ac8bb10cccf8a6c8a24",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "保存数据库",
  "createTime" : 1773053388148,
  "updateTime" : 1773482243589,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.pinyin.PinyinUtil

if (body.id !== null and body.id !== '') {
    return anyline.update("sys_database", body)
} else {
    body.aliasName = body.name
    body.name = PinyinUtil.getPinyin(body.aliasName)
    if (name.indexOf("custom_") >= 0) {
        exit 0, "非法的连接名"
    }
    return anyline.insert("sys_database", body)
}