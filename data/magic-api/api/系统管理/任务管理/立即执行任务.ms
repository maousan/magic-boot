{
  "properties" : { },
  "id" : "a71481b2c6f145a7a584f7571756da37",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "立即执行任务",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/{jobId}/trigger",
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
  "description" : "立即执行一次任务",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.spring.SpringUtil
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistryForQuartz

try {
    var registry = SpringUtil.getBean(JobMagicDynamicRegistryForQuartz.class)
    registry.triggerJob(jobId)
    return "ok"
} catch (e) {
    return e.getMessage()
}
