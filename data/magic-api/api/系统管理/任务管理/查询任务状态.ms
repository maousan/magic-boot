{
  "properties" : { },
  "id" : "cb9c61bd3ad14bf29c911c0f1bf1dba9",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "查询任务状态",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/{jobId}/state",
  "method" : "GET",
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
  "description" : "查询任务当前状态",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.hutool.extra.spring.SpringUtil
import org.quartz.Scheduler
import org.quartz.JobKey
import org.quartz.TriggerKey
import org.quartz.Trigger

try {
    var scheduler = SpringUtil.getBean(Scheduler.class)
    var jobKey = new JobKey(jobId, "MAGIC_API_JOBS")
    var exists = scheduler.checkExists(jobKey)

    if (!exists) {
        return "任务不存在"
    }

    var triggerKey = new TriggerKey(jobId + "_trigger", "MAGIC_API_JOBS")
    var triggerState = scheduler.getTriggerState(triggerKey)
    var paused = triggerState == Trigger.TriggerState.PAUSED

    var trigger = scheduler.getTrigger(triggerKey)
    var nextFireTime = trigger != null ? trigger.getNextFireTime() : null

    return {
        "exists": exists,
        "paused": paused,
        "nextFireTime": nextFireTime != null ? nextFireTime.toString() : null
    }
} catch (e) {
    return e.getMessage()
}
