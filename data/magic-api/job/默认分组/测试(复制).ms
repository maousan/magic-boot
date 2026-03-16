{
  "properties" : { },
  "id" : "bf95ed42566f47c8ac459372881fe955",
  "script" : null,
  "groupId" : "4a3c0ad862bb4b5fb16c4c673b5a75a3",
  "name" : "测试(复制)",
  "createTime" : 1773492220253,
  "updateTime" : 1773633862648,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/test_copy",
  "cron" : "20 * * * * ?",
  "enabled" : false,
  "description" : "这是一个测试任务1",
  "params" : "{\r\n    \"s\":1\r\n}",
  "misfirePolicy" : "IGNORE",
  "concurrent" : false,
  "dependsOn" : null,
  "maxRetry" : 0,
  "timeout" : 1,
  "jobType" : "script",
  "clazz" : "org.ssssssss.magicapi.job.quartz.TestJob"
}
================================
import log
import redis

log.info("hello world2")