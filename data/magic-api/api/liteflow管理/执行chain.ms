{
  "properties" : { },
  "id" : "a2a6ae63c7cf47afb187acf9f1a81d58",
  "script" : null,
  "groupId" : "dc4936b5a9a2456282ab5c4aaa046625",
  "name" : "执行chain",
  "createTime" : 1772680353098,
  "updateTime" : 1773470835801,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/chain/execute",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"chainId\": \"chain1\",\r\n    \"params\": {}\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"result\": {\n            \"student\": {\n                \"studentID\": 100301,\n                \"studentName\": \"张三\"\n            },\n            \"demoDate\": \"2022-10-17 13:31:43\",\n            \"resultList\": [\n                \"hello,a\",\n                \"hello,b\",\n                \"hello,c\"\n            ],\n            \"s1\": 6\n        },\n        \"stepInfo\": \"groovy1[groovy1]<1>\",\n        \"chainId\": \"chain1\",\n        \"requestId\": \"169b6e7b595f48338229a3ceeab17d54\",\n        \"timeoutInfo\": null\n    },\n    \"timestamp\": 1772680804722,\n    \"executeTime\": 5\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import com.yomahub.liteflow.slot.DefaultContext
import lf

const resp = lf.execute(body.chainId, body.params)

if (resp.isSuccess()) {
    return {
        result: resp.getContextBean(DefaultContext.class).getDataMap(),
        stepInfo: resp.getExecuteSteps(),
        chainId: resp.getChainId(),
        requestId: resp.getRequestId(),
        timeoutInfo: resp.getTimeoutItems(),
    }
} else {
    exit 0, resp.getMessage()
}