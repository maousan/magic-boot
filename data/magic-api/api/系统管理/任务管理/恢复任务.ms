{
  "properties" : { },
  "id" : "35eb6dd8ff4440ae8f450d06f4448226",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "恢复任务",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/{jobId}/resume",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ {
    "name" : "jobId",
    "value" : null,
    "description" : "任务ID",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "responseBody" : null,
  "description" : "恢复指定任务",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.spring.SpringUtil
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistryForQuartz

try {
    var registry = SpringUtil.getBean(JobMagicDynamicRegistryForQuartz.class)
    registry.resumeJob(jobId)
    return "ok"
} catch (e) {
    return e.getMessage()
}
