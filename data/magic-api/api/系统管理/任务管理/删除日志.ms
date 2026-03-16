{
  "properties" : { },
  "id" : "bc08592656a940059197117c1b15e860",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "删除日志",
  "createTime" : 1773393259317,
  "updateTime" : 1773394706984,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/log",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "days",
    "value" : null,
    "description" : "最近天数",
    "required" : false,
    "dataType" : "Integer",
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
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================

let _days = days

if (!_days) {
    _days = 7
}

return anyline.execute("delete from magic_job_log where create_time < NOW() - INTERVAL {days} DAY", `days:${_days}`);