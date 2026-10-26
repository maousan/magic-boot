{
  "properties" : { },
  "id" : "6481df82f594405eac157c86cff5b14d",
  "script" : null,
  "groupId" : "bb5ffce7c9a74a6a962e38a8e12b7262",
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
  "responseBody" : "{\n  \"days\": 90,\n  \"accessDeleted\": 12\n}",
  "description" : "手动清理访问日志：删除 magic_access_log 中早于 days 天的记录（0=全部清理）；自动清理走定时任务（保留天数配置 access-log.retention-days）",
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

let accessDeleted = db.update("""
    delete from magic_access_log where create_time < date_sub(now(), interval #{days} day)
""", {days: daysNum})

log.info('access log cleanup manual: days={}, accessDeleted={}', daysNum, accessDeleted)

return {
    days: daysNum,
    accessDeleted: accessDeleted
}
