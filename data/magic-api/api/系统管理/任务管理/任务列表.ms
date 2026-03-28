{
  "properties" : { },
  "id" : "eb92d28985d74d0e888180a98e150f9e",
  "script" : null,
  "groupId" : "1828eba93a114c2f9f957e9cfc77258b",
  "name" : "任务列表",
  "createTime" : 1772682310657,
  "updateTime" : 1774602578229,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "name",
    "value" : null,
    "description" : "job name keyword",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "path",
    "value" : null,
    "description" : "job path keyword",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "enabled",
    "value" : null,
    "description" : "enabled flag, true/false",
    "required" : false,
    "dataType" : "Boolean",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "jobType",
    "value" : null,
    "description" : "job type, script/clazz",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "page",
    "value" : null,
    "description" : "page index, default 1",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "1",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "size",
    "value" : null,
    "description" : "page size, default 10",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "10",
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
import cn.hutool.extra.spring.SpringUtil
import org.quartz.Scheduler
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.TriggerKey
import org.ssssssss.magicapi.core.service.MagicResourceService

try {
    var resourceService = SpringUtil.getBean(MagicResourceService.class)
    var scheduler = SpringUtil.getBean(Scheduler.class)

    var allJobs = resourceService.files("job")
    if (allJobs == null) {
        return {
            total: 0,
            list: [],
            page: 1,
            size: 10
        }
    }

    var keywordName = name
    var keywordPath = path
    var filterType = jobType
    var filterEnabled = enabled

    var result = []

    for (job in allJobs) {
        var jobId = job.getId()
        var jobName = job.getName()
        var jobPath = job.getPath()
        var jobEnabled = job.isEnabled()
        var currentType = job.getJobType()
        if (currentType == null || currentType == '') {
            currentType = "script"
        }

        if (keywordName != null && keywordName != '' && (jobName == null || !jobName.contains(keywordName))) {
            continue
        }
        if (keywordPath != null && keywordPath != '' && (jobPath == null || !jobPath.contains(keywordPath))) {
            continue
        }
        if (filterType != null && filterType != '' && currentType != filterType) {
            continue
        }
        if (filterEnabled != null && filterEnabled != '' && (jobEnabled + '') != (filterEnabled + '')) {
            continue
        }

        var exists = false
        var paused = false
        var nextFireTime = null
        try {
            var jobKey = new JobKey(jobId, "MAGIC_API_JOBS")
            exists = scheduler.checkExists(jobKey)
            if (exists) {
                var triggerKey = new TriggerKey(jobId + "_trigger", "MAGIC_API_JOBS")
                var state = scheduler.getTriggerState(triggerKey)
                paused = state == Trigger.TriggerState.PAUSED
                var trigger = scheduler.getTrigger(triggerKey)
                nextFireTime = trigger != null ? trigger.getNextFireTime() : null
            }
        } catch (ignore) {
        }

        result.push({
            id: jobId,
            name: jobName,
            path: jobPath,
            cron: job.getCron(),
            enabled: jobEnabled,
            jobType: currentType,
            clazz: job.getClazz(),
            params: job.getParams(),
            description: job.getDescription(),
            createTime: job.getCreateTime(),
            updateTime: job.getUpdateTime(),
            exists: exists,
            paused: paused,
            nextFireTime: nextFireTime != null ? nextFireTime.toString() : null
        })
    }

    result.sort((a, b) => {
        var at = a.updateTime || a.createTime || 0
        var bt = b.updateTime || b.createTime || 0
        return bt - at
    })

    var currentPage = page
    if (currentPage == null || currentPage < 1) {
        currentPage = 1
    }
    var pageSize = size
    if (pageSize == null || pageSize < 1) {
        pageSize = 10
    }

    var total = result.length
    var start = (currentPage - 1) * pageSize
    if (start < 0) {
        start = 0
    }
    var end = start + pageSize
    if (end > total) {
        end = total
    }
    var pageList = []
    if (start < total) {
        pageList = result.slice(start, end)
    }

    return {
        total: total,
        list: pageList,
        page: currentPage,
        size: pageSize
    }
} catch (e) {
    return e.getMessage()
}
