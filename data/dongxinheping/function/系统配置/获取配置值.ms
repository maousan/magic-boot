{
  "properties" : { },
  "id" : "95b63c03f67a4b0b874cda9239b03319",
  "script" : null,
  "groupId" : "4516f947849e42068a0af5e3fbe9d638",
  "name" : "获取配置值",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/getValue",
  "description" : "读 sys_configure 配置值，经系统 redis 插件缓存（redis.default，key 前缀 sys_config:，TTL 60 秒）。refresh=true 时强制读库并回写缓存（系统配置页保存后调用，改值即时生效）；直接 SQL 改值最长 60s 内自然生效；无记录缓存并返回空串",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "configureKey",
    "value" : null,
    "description" : "配置键，如 batch-epc.strict-one-label",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "refresh",
    "value" : null,
    "description" : "true=强制读库并刷新缓存（保存配置后调用）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import log
import redis

// 2026-10-21 新增：配置读取走 redis 缓存（系统 redis 插件，TTL 60s），避免业务脚本每次调用直查 sys_configure。
// 选型说明：实测 magic-script 的 const 与 hutool Singleton 方案均无法跨调用共享状态，redis 是脚本层可靠的共享缓存；
// 系统配置页保存后以 refresh=true 强制读库并回写缓存（即时生效）；直接 SQL 改值最长 60s 内自然过期；无记录缓存空串防穿透
let cacheKey = 'sys_config:' + configureKey

let force = refresh != null && ((refresh + '') == 'true')
let v = force ? null : redis.default.getString(cacheKey)

if(v == null){
    v = db.selectValue("select configure_value from sys_configure where configure_key = #{key} and is_del = 0", {key: configureKey})
    if(v == null){
        v = ''
    }
    redis.default.putString(cacheKey, v, 60)
    log.info('sys-config cache {} for key={}', force ? 'refreshed' : 'loaded', configureKey)
}

return v
