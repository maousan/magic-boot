{
  "properties" : { },
  "id" : "5cae635c8e194528969889c260ee861d",
  "script" : null,
  "groupId" : "5f5a6aa7b12e400da07b653a3216c600",
  "name" : "保存",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\n  \"id\": \"a2cac5d9036b41ecaed4496b2f40085c\",\n  \"configureValue\": \"true\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "\"保存成功\"",
  "description" : "按 id 更新系统配置值（sys_configure.configure_value），记录更新人与时间；配置由脚本按次读库，改后即时生效",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import cn.dev33.satoken.stp.StpUtil
import '@/sys-config/getValue' as getSysConfig

let id = body.id
let configureValue = body.configureValue

if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}
if(configureValue == null){
    exit 400, '配置值不能为空'
}

id = (id + '').trim()

let exists = db.selectOne("select id, configure_key from sys_configure where id = #{id} and is_del = 0", {id: id})
if(exists == null){
    exit 404, '配置项不存在'
}

let updateBy = StpUtil.getLoginIdAsString()

db.update("""
    update sys_configure set configure_value = #{v}, update_by = #{u}, update_date = now() where id = #{id}
""", {id: id, v: configureValue, u: updateBy})

// 2026-10-21 改因：保存后强制刷新该键的进程内缓存（getSysConfig(refresh=true)），消费方即时读到新值
getSysConfig(exists.configureKey, true)

log.info('system configure update: key=' + exists.configureKey + ', value=' + configureValue + ', by=' + updateBy)

return '保存成功'
