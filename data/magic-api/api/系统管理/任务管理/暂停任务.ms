{
  "properties" : { },
  "id" : "fe87d8ac2b364bbf83e70e6c5c5418a1",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "暂停任务",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/{jobId}/pause",
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
  "description" : "暂停指定任务",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.spring.SpringUtil
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistryForQuartz

try {
    var registry = SpringUtil.getBean(JobMagicDynamicRegistryForQuartz.class)
    registry.pauseJob(jobId)
    return "ok"
} catch (e) {
    return e.getMessage()
}
