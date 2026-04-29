{
  "properties" : { },
  "id" : "f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "列表查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "status",
    "value" : "",
    "description" : "状态过滤: 1=绑定中, 0=已解绑, 不传则全部",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "keyword",
    "value" : "",
    "description" : "关键词搜索(匹配batchId或epc)",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "page",
    "value" : "1",
    "description" : "页码",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageSize",
    "value" : "20",
    "description" : "每页条数",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"data\": {\n    \"list\": [],\n    \"total\": 0\n  }\n}",
  "description" : "分页查询批次EPC绑定记录。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
page = page != null ? Integer.valueOf(page + '') : 1
pageSize = pageSize != null ? Integer.valueOf(pageSize + '') : 20
if(page < 1){ page = 1 }
if(pageSize < 1 || pageSize > 100){ pageSize = 20 }

var sql = "select id, batch_id, epc, status, bind_time, unbind_time from t_batch_epc where 1=1"
var params = {}

if(status != null && (status + '').trim() != ''){
    sql = sql + " and status = #{status}"
    params.status = Integer.valueOf((status + '').trim())
}
if(keyword != null && (keyword + '').trim() != ''){
    sql = sql + " and (batch_id like concat('%', #{keyword}, '%') or epc like concat('%', #{keyword}, '%'))"
    params.keyword = (keyword + '').trim()
}

sql = sql + " order by create_time desc"

var list = db.page(sql, params, page, pageSize)

var items = []
if(list != null && list.content != null){
    for(item in list.content){
        items.add({
            id: item.id,
            batchId: item.batch_id,
            epc: item.epc,
            status: item.status,
            bindTime: item.bind_time,
            unbindTime: item.unbind_time
        })
    }
}

return {
    success: true,
    data: {
        list: items,
        total: list != null ? list.total : 0
    }
}