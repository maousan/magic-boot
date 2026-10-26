{
  "properties" : { },
  "id" : "0068263875a1428491a6d6500b83246a",
  "script" : null,
  "groupId" : "307cb79bb76f4a0f9002fd494767a2aa",
  "name" : "清理过期日志",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/error-log-cleanup",
  "cron" : "0 40 3 * * ?",
  "enabled" : true,
  "description" : "每天 03:40 清理过期错误日志（magic_error_log），保留天数读系统配置 error-log.retention-days（默认90，0=停用自动清理）",
  "params" : "",
  "misfirePolicy" : "SMART",
  "concurrent" : false,
  "dependsOn" : null,
  "maxRetry" : 0,
  "timeout" : 0,
  "jobType" : "script",
  "clazz" : null
}
================================
import log
import java.lang.Integer
import '@/sys-config/getValue' as getSysConfig

// 保留天数：系统配置 error-log.retention-days（默认90；0 或非法 = 停用本次自动清理）
let retentionText = (getSysConfig('error-log.retention-days', false) + '').trim()
let retentionDays = null
if(retentionText != ''){
    try{
        retentionDays = Integer.parseInt(retentionText)
    }catch(e){
    }
}
if(retentionDays == null){
    retentionDays = 90
}
if(retentionDays <= 0){
    log.info('error log cleanup skipped: retention disabled (error-log.retention-days={})', retentionText)
    return {
        skipped: true,
        reason: 'retention disabled',
        finishedAt: new Date()
    }
}

let errorDeleted = db.update("delete from magic_error_log where create_time < date_sub(now(), interval #{days} day)", {days: retentionDays})

log.info('error log cleanup done: retentionDays={}, errorDeleted={}', retentionDays, errorDeleted)

return {
    skipped: false,
    retentionDays: retentionDays,
    errorDeleted: errorDeleted,
    finishedAt: new Date()
}
