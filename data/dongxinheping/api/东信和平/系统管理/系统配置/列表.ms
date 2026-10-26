{
  "properties" : { },
  "id" : "7c83169601cd4549918e18be68955a7b",
  "script" : null,
  "groupId" : "5f5a6aa7b12e400da07b653a3216c600",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "[\n  {\n    \"id\": \"a2cac5d9036b41ecaed4496b2f40085c\",\n    \"configureKey\": \"batch-epc.strict-one-label\",\n    \"configureValue\": \"false\",\n    \"configureName\": \"批次EPC严格1箱1标（true=同批次仅允许一个活跃EPC）\",\n    \"remark\": null,\n    \"updateDate\": \"2026-10-21 10:00:00\"\n  }\n]",
  "description" : "系统配置列表：读 sys_configure 全部未删除项（键/值/名称/备注/更新时间），按 key 排序；配置值改后即时生效",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

// 行键经全局 sql-column-case: camel 转驼峰（configureKey/configureValue/configureName/updateDate）
let list = db.select("""
    select id, configure_key, configure_value, configure_name, remark, update_date
    from sys_configure
    where is_del = 0
    order by configure_key
""")

return list == null ? [] : list
