{
  "properties" : { },
  "id" : "75b498b325f6475abc3ab17871088333",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "删除全部日志",
  "createTime" : 1773411914176,
  "updateTime" : 1773412027249,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/deleteAll",
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
import cn.dev33.satoken.stp.StpUtil

StpUtil.checkSafe("deleteAllJobLogs")

db.table("magic_job_log").delete()