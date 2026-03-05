{
  "properties" : { },
  "id" : "0b605de92ec34e76b3340c565c31f791",
  "script" : null,
  "groupId" : "4a3c0ad862bb4b5fb16c4c673b5a75a3",
  "name" : "测试",
  "createTime" : 1772601698095,
  "updateTime" : 1772648975333,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/test",
  "cron" : "0 * * * * ?",
  "enabled" : false,
  "description" : "这是一个测试任务1",
  "misfirePolicy" : "SMART",
  "concurrent" : false,
  "dependsOn" : null,
  "maxRetry" : 0,
  "timeout" : 0
}
================================
import log
import redis

log.info("hello world")

log.info(student)

// redis.local.putString("magic-api", { "result": 1 })
// redis.local.remove("magic-api")
redis.local.putObject('magic-api', { "version": "1.0.0" })


var object = redis.local.getObject("magic-api", Map.class)
log.info("magic-api: {}", object["version"])


return 'Hello magic-api-job'