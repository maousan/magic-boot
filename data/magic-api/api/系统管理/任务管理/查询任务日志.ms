{
  "properties" : { },
  "id" : "4db2926a913c48b780475bd1a77716bc",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "查询任务日志",
  "createTime" : 1773389351895,
  "updateTime" : 1774599560511,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/log/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 75,\n        \"list\": [\n            {\n                \"id\": 71,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": \"import log\\r\\nimport redis\\r\\n\\r\\nlog.info(\\\"hello world\\\")\\r\\n\\r\\nlog.info(student)\\r\\n\\r\\n// redis.local.putString(\\\"magic-api\\\", { \\\"result\\\": 1 })\\r\\n// redis.local.remove(\\\"magic-api\\\")\\r\\nredis.default.putObject('magic-api', { \\\"version\\\": \\\"1.0.0\\\" })\\r\\n\\r\\n\\r\\nvar object = redis.default.getObject(\\\"magic-api\\\", Map.class)\\r\\nlog.info(\\\"magic-api: {}\\\", object[\\\"version\\\"])\\r\\n\\r\\n\\r\\nreturn 'Hello magic-api-job'\",\n                \"startTime\": \"2026-03-13 16:19:00\",\n                \"endTime\": null,\n                \"duration\": null,\n                \"status\": \"RUNNING\",\n                \"result\": null,\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": \"CRON\",\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:18:57\"\n            },\n            {\n                \"id\": 72,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": null,\n                \"startTime\": \"2026-03-13 16:19:00\",\n                \"endTime\": \"2026-03-13 16:19:00\",\n                \"duration\": 0,\n                \"status\": \"SUCCESS\",\n                \"result\": \"Hello magic-api-job\",\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": null,\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:18:57\"\n            },\n            {\n                \"id\": 73,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": \"import log\\r\\nimport redis\\r\\n\\r\\nlog.info(\\\"hello world\\\")\\r\\n\\r\\nlog.info(student)\\r\\n\\r\\n// redis.local.putString(\\\"magic-api\\\", { \\\"result\\\": 1 })\\r\\n// redis.local.remove(\\\"magic-api\\\")\\r\\nredis.default.putObject('magic-api', { \\\"version\\\": \\\"1.0.0\\\" })\\r\\n\\r\\n\\r\\nvar object = redis.default.getObject(\\\"magic-api\\\", Map.class)\\r\\nlog.info(\\\"magic-api: {}\\\", object[\\\"version\\\"])\\r\\n\\r\\n\\r\\nreturn 'Hello magic-api-job'\",\n                \"startTime\": \"2026-03-13 16:20:00\",\n                \"endTime\": null,\n                \"duration\": null,\n                \"status\": \"RUNNING\",\n                \"result\": null,\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": \"CRON\",\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:19:57\"\n            },\n            {\n                \"id\": 74,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": null,\n                \"startTime\": \"2026-03-13 16:20:00\",\n                \"endTime\": \"2026-03-13 16:20:00\",\n                \"duration\": 0,\n                \"status\": \"SUCCESS\",\n                \"result\": \"Hello magic-api-job\",\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": null,\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:19:57\"\n            },\n            {\n                \"id\": 75,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": \"import log\\r\\nimport redis\\r\\n\\r\\nlog.info(\\\"hello world\\\")\\r\\n\\r\\nlog.info(student)\\r\\n\\r\\n// redis.local.putString(\\\"magic-api\\\", { \\\"result\\\": 1 })\\r\\n// redis.local.remove(\\\"magic-api\\\")\\r\\nredis.default.putObject('magic-api', { \\\"version\\\": \\\"1.0.0\\\" })\\r\\n\\r\\n\\r\\nvar object = redis.default.getObject(\\\"magic-api\\\", Map.class)\\r\\nlog.info(\\\"magic-api: {}\\\", object[\\\"version\\\"])\\r\\n\\r\\n\\r\\nreturn 'Hello magic-api-job'\",\n                \"startTime\": \"2026-03-13 16:21:00\",\n                \"endTime\": null,\n                \"duration\": null,\n                \"status\": \"RUNNING\",\n                \"result\": null,\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": \"CRON\",\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:20:57\"\n            },\n            {\n                \"id\": 76,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": null,\n                \"startTime\": \"2026-03-13 16:21:00\",\n                \"endTime\": \"2026-03-13 16:21:00\",\n                \"duration\": 0,\n                \"status\": \"SUCCESS\",\n                \"result\": \"Hello magic-api-job\",\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": null,\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:20:57\"\n            },\n            {\n                \"id\": 77,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": \"import log\\r\\nimport redis\\r\\n\\r\\nlog.info(\\\"hello world\\\")\\r\\n\\r\\nlog.info(student)\\r\\n\\r\\n// redis.local.putString(\\\"magic-api\\\", { \\\"result\\\": 1 })\\r\\n// redis.local.remove(\\\"magic-api\\\")\\r\\nredis.default.putObject('magic-api', { \\\"version\\\": \\\"1.0.0\\\" })\\r\\n\\r\\n\\r\\nvar object = redis.default.getObject(\\\"magic-api\\\", Map.class)\\r\\nlog.info(\\\"magic-api: {}\\\", object[\\\"version\\\"])\\r\\n\\r\\n\\r\\nreturn 'Hello magic-api-job'\",\n                \"startTime\": \"2026-03-13 16:22:00\",\n                \"endTime\": null,\n                \"duration\": null,\n                \"status\": \"RUNNING\",\n                \"result\": null,\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": \"CRON\",\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:21:57\"\n            },\n            {\n                \"id\": 78,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": null,\n                \"startTime\": \"2026-03-13 16:22:00\",\n                \"endTime\": \"2026-03-13 16:22:00\",\n                \"duration\": 0,\n                \"status\": \"SUCCESS\",\n                \"result\": \"Hello magic-api-job\",\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": null,\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:21:57\"\n            },\n            {\n                \"id\": 79,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": \"import log\\r\\nimport redis\\r\\n\\r\\nlog.info(\\\"hello world\\\")\\r\\n\\r\\nlog.info(student)\\r\\n\\r\\n// redis.local.putString(\\\"magic-api\\\", { \\\"result\\\": 1 })\\r\\n// redis.local.remove(\\\"magic-api\\\")\\r\\nredis.default.putObject('magic-api', { \\\"version\\\": \\\"1.0.0\\\" })\\r\\n\\r\\n\\r\\nvar object = redis.default.getObject(\\\"magic-api\\\", Map.class)\\r\\nlog.info(\\\"magic-api: {}\\\", object[\\\"version\\\"])\\r\\n\\r\\n\\r\\nreturn 'Hello magic-api-job'\",\n                \"startTime\": \"2026-03-13 16:23:00\",\n                \"endTime\": null,\n                \"duration\": null,\n                \"status\": \"RUNNING\",\n                \"result\": null,\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": \"CRON\",\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:22:57\"\n            },\n            {\n                \"id\": 80,\n                \"jobId\": \"0b605de92ec34e76b3340c565c31f791\",\n                \"jobName\": \"/默认分组/测试(/job/test)\",\n                \"jobGroup\": \"MAGIC_API_JOBS\",\n                \"scriptPath\": null,\n                \"startTime\": \"2026-03-13 16:23:00\",\n                \"endTime\": \"2026-03-13 16:23:00\",\n                \"duration\": 0,\n                \"status\": \"SUCCESS\",\n                \"result\": \"Hello magic-api-job\",\n                \"exceptionMessage\": null,\n                \"exceptionStack\": null,\n                \"triggerType\": null,\n                \"triggeredBy\": null,\n                \"createTime\": \"2026-03-13 16:22:57\"\n            }\n        ]\n    },\n    \"timestamp\": 1773395212962,\n    \"executeTime\": 16\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "success",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "total",
        "value" : "74",
        "description" : "",
        "required" : false,
        "dataType" : "Integer",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "list",
        "value" : "",
        "description" : "",
        "required" : false,
        "dataType" : "Array",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ {
          "name" : "",
          "value" : "",
          "description" : "",
          "required" : false,
          "dataType" : "Object",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ {
            "name" : "id",
            "value" : "71",
            "description" : "",
            "required" : false,
            "dataType" : "Integer",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "jobId",
            "value" : "0b605de92ec34e76b3340c565c31f791",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "jobName",
            "value" : "/默认分组/测试(/job/test)",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "jobGroup",
            "value" : "MAGIC_API_JOBS",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "scriptPath",
            "value" : "import log\r\nimport redis\r\n\r\nlog.info(\"hello world\")\r\n\r\nlog.info(student)\r\n\r\n// redis.local.putString(\"magic-api\", { \"result\": 1 })\r\n// redis.local.remove(\"magic-api\")\r\nredis.default.putObject('magic-api', { \"version\": \"1.0.0\" })\r\n\r\n\r\nvar object = redis.default.getObject(\"magic-api\", Map.class)\r\nlog.info(\"magic-api: {}\", object[\"version\"])\r\n\r\n\r\nreturn 'Hello magic-api-job'",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "startTime",
            "value" : "2026-03-13 16:19:00",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "endTime",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "duration",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "status",
            "value" : "RUNNING",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "result",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "exceptionMessage",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "exceptionStack",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "triggerType",
            "value" : "CRON",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "triggeredBy",
            "value" : "null",
            "description" : "",
            "required" : false,
            "dataType" : "Object",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "createTime",
            "value" : "2026-03-13 16:18:57",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          } ]
        } ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1773392632721",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "31",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
let sql = """
    select *
    from magic_job_log
    <where>
        <if test="jobName != null and jobName != ''">
        and job_name = #{jobName}
        </if>
        <if test="status != null">
        and status = #{status}
        </if>
        <if test="triggerType != null and triggerType != ''">
        and trigger_type = #{triggerType}
        </if>
        <if test="createTimeStart != null and createTimeStart != ''">
        and create_time >= #{createTimeStart}
        </if>
        <if test="createTimeEnd != null and createTimeEnd != ''">
        and create_time <= #{createTimeEnd}
        </if>
    </where>
    order by create_time desc
"""

return db.page(sql)