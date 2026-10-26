{
  "properties" : { },
  "id" : "a23e3f50b537473eb6fd629347125290",
  "script" : null,
  "groupId" : "7953230a18d442449d373a48b0ff3e35",
  "name" : "清理",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/clean",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\n  \"days\": 90\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"days\": 90,\n  \"errorDeleted\": 3\n}",
  "description" : "手动清理错误日志：删除 magic_error_log 中早于 days 天的记录（0=全部清理）；自动清理走定时任务（保留天数配置 error-log.retention-days）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import java.lang.Integer

let daysText = (body.days == null ? '' : (body.days + '')).trim()
let daysNum = null
if(daysText != ''){
    try{
        daysNum = Integer.parseInt(daysText)
    }catch(e){
    }
}
if(daysNum == null || daysNum < 0){
    exit 400, 'days 必须是不小于0的整数（0=全部清理）'
}

let errorDeleted = db.update("""
    delete from magic_error_log where create_time < date_sub(now(), interval #{days} day)
""", {days: daysNum})

log.info('error log cleanup manual: days={}, errorDeleted={}', daysNum, errorDeleted)

return {
    days: daysNum,
    errorDeleted: errorDeleted
}
