{
  "properties" : { },
  "id" : "813d6be5a1ee4c01b2ee754921f6d315",
  "script" : null,
  "groupId" : "773f68957add4b70ad062f67fc2cd0df",
  "name" : "清理过期日志",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/access-log-cleanup",
  "cron" : "0 30 3 * * ?",
  "enabled" : true,
  "description" : "每天 03:30 清理过期访问日志（magic_access_log），保留天数读系统配置 access-log.retention-days（默认90，0=停用自动清理）",
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

// 保留天数：系统配置 access-log.retention-days（默认90；0 或非法 = 停用本次自动清理）
let retentionText = (getSysConfig('access-log.retention-days', false) + '').trim()
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
    log.info('access log cleanup skipped: retention disabled (access-log.retention-days={})', retentionText)
    return {
        skipped: true,
        reason: 'retention disabled',
        finishedAt: new Date()
    }
}

let accessDeleted = db.update("delete from magic_access_log where create_time < date_sub(now(), interval #{days} day)", {days: retentionDays})

log.info('access log cleanup done: retentionDays={}, accessDeleted={}', retentionDays, accessDeleted)

return {
    skipped: false,
    retentionDays: retentionDays,
    accessDeleted: accessDeleted,
    finishedAt: new Date()
}
