{
  "properties" : { },
  "id" : "6546a014e39247448caecef6255ea0d6",
  "script" : null,
  "groupId" : "dc4936b5a9a2456282ab5c4aaa046625",
  "name" : "查询chain列表",
  "createTime" : 1772679546115,
  "updateTime" : 1773470836530,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/chain/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误\",\n    \"data\": null,\n    \"timestamp\": 1772682267581,\n    \"executeTime\": 7\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.yomahub.liteflow.flow.FlowBus

const list = []

FlowBus.chainMap.keySet().forEach(key => {
    const value = FlowBus.chainMap[key]
    list.push({
        chainId: value.chainId,
        chainName: value.chainName,
        namespace: value.namespace

    })
})

return list