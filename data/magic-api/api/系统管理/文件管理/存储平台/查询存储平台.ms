{
  "properties" : { },
  "id" : "8b9de7f6a4c2418f9d2b6c3e1f0a5d7b",
  "script" : null,
  "groupId" : "f6b8c4d2a9e1473cb8d9f1a25c0e7b4f",
  "name" : "查询存储平台",
  "createTime" : null,
  "updateTime" : 1774347063278,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": [\n        {\n            \"id\": \"979de4a633ba4661a97014b2ce16c511\",\n            \"key\": \"minio\",\n            \"name\": \"minio\",\n            \"type\": \"minio\",\n            \"isDefault\": false,\n            \"enabled\": true,\n            \"properties\": {\n                \"endpoint\": \"http://192.168.2.90:9000\",\n                \"bucket\": \"my-bucket\",\n                \"accessKey\": \"hjv2S20zcJIiJcMSjqhJ\",\n                \"secretKey\": \"MdbiD8zGSIjddiAcDVhZihXaK198BAJC9CNe50GK\",\n                \"region\": \"us-east-1\"\n            },\n            \"createTime\": 1774177723620,\n            \"updateTime\": 1774179136603\n        },\n        {\n            \"id\": \"ef4d7d120a3b445fa37ef603a25c6a4c\",\n            \"key\": \"local\",\n            \"name\": \"本地存储\",\n            \"type\": \"local\",\n            \"isDefault\": true,\n            \"enabled\": true,\n            \"properties\": {\n                \"basePath\": \"upload/\",\n                \"domain\": \"http://localhost:8089\"\n            },\n            \"createTime\": 1774177048192,\n            \"updateTime\": 1774257003231\n        }\n    ],\n    \"timestamp\": 1774346799243,\n    \"executeTime\": 9\n}",
  "description" : "查询已配置的存储平台列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "success",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "",
      "description" : "",
      "required" : false,
      "dataType" : "Array",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "",
        "value" : "",
        "description" : "",
        "required" : false,
        "dataType" : "Object",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ {
          "name" : "id",
          "value" : "979de4a633ba4661a97014b2ce16c511",
          "description" : "",
          "required" : false,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "key",
          "value" : "minio",
          "description" : "",
          "required" : false,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "name",
          "value" : "minio",
          "description" : "",
          "required" : false,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "type",
          "value" : "minio",
          "description" : "",
          "required" : false,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "isDefault",
          "value" : "false",
          "description" : "",
          "required" : false,
          "dataType" : "Boolean",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "enabled",
          "value" : "true",
          "description" : "",
          "required" : false,
          "dataType" : "Boolean",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "properties",
          "value" : "",
          "description" : "",
          "required" : false,
          "dataType" : "Object",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ {
            "name" : "endpoint",
            "value" : "http://192.168.2.90:9000",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "bucket",
            "value" : "my-bucket",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "accessKey",
            "value" : "hjv2S20zcJIiJcMSjqhJ",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "secretKey",
            "value" : "MdbiD8zGSIjddiAcDVhZihXaK198BAJC9CNe50GK",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "region",
            "value" : "us-east-1",
            "description" : "",
            "required" : false,
            "dataType" : "String",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          } ]
        }, {
          "name" : "createTime",
          "value" : "1774177723620",
          "description" : "",
          "required" : false,
          "dataType" : "Long",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "updateTime",
          "value" : "1774179136603",
          "description" : "",
          "required" : false,
          "dataType" : "Long",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        } ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1774346799243",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "9",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import file

var platforms = file.getAllFilePlatforms()
if(platforms == null){
    platforms = []
}

var list = []
var defaultKey = null

for(item in platforms){
    var isDefault = item.getIsDefault() == true
    var row = {
        id: item.getId(),
        key: item.getKey(),
        name: item.getName(),
        type: item.getType(),
        isDefault: isDefault,
        enabled: item.getEnabled(),
        // properties: item.getProperties(),
        createTime: item.getCreateTime(),
        updateTime: item.getUpdateTime()
    }
    list.push(row)
    if(defaultKey == null && isDefault){
        defaultKey = item.getKey()
    }
}

if(defaultKey == null && list.size() > 0){
    defaultKey = list[0].key
}

return list
