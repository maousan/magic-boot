{
  "properties": {},
  "id": "uyw4jsvy86xw07985lv8urqwaykq67a3",
  "script": null,
  "groupId": "zqov8sdr55f5wmdwfqkwfvmjkd77w9bf",
  "name": "列表",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/list",
  "method": "GET",
  "parameters": [
    {
      "name": "pageNumber",
      "value": "1",
      "description": "当前页码",
      "required": false,
      "dataType": "Integer",
      "type": null,
      "defaultValue": "1",
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "pageSize",
      "value": "20",
      "description": "每页数量",
      "required": false,
      "dataType": "Integer",
      "type": null,
      "defaultValue": "20",
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "keyword",
      "value": "张三",
      "description": "搜索关键词（支持用户名、姓名模糊查询）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "startDate",
      "value": "2024-01-01",
      "description": "开始日期（格式：yyyy-MM-dd）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": "@date",
      "error": "日期格式不正确",
      "expression": null,
      "children": null
    },
    {
      "name": "endDate",
      "value": "2024-12-31",
      "description": "结束日期（格式：yyyy-MM-dd）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": "@date",
      "error": "日期格式不正确",
      "expression": null,
      "children": null
    },
    {
      "name": "status",
      "value": "1",
      "description": "状态（0禁用，1启用）",
      "required": false,
      "dataType": "Integer",
      "type": null,
      "defaultValue": "1",
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "orderBy",
      "value": "create_time",
      "description": "排序字段（create_time, update_time, username）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": "create_time",
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "orderType",
      "value": "desc",
      "description": "排序方式（asc升序，desc降序）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": "desc",
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "options": [
    {
      "name": "permission",
      "value": "user-test:view",
      "description": "允许拥有该权限的访问",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "requestBody": "{}",
  "headers": [],
  "paths": [],
  "responseBody": null,
  "description": "用户列表查询接口 - 测试 Query 参数（分页、搜索、日期范围、状态过滤、排序）",
  "requestBodyDefinition": {
    "name": "",
    "value": "",
    "description": "",
    "required": false,
    "dataType": "Object",
    "type": null,
    "defaultValue": null,
    "validateType": "",
    "error": "",
    "expression": "",
    "children": []
  },
  "responseBodyDefinition": null
}
================================
import log

// 处理默认值
var page = pageNumber ?: 1
var size = pageSize ?: 20
var orderField = orderBy ?: 'create_time'
var order = orderType ?: 'desc'

log.info("分页参数: page=" + page + ", size=" + size)
log.info("排序参数: orderBy=" + orderField + ", orderType=" + order)

// 构建动态 SQL
let sql = """
    select
        id,
        username,
        name,
        phone,
        email,
        status,
        create_time,
        update_time
    from sys_user
    <where>
        and is_del = 0
        <if test="keyword != null and keyword != ''">
            and (username like concat('%', #{keyword}, '%')
                 or name like concat('%', #{keyword}, '%'))
        </if>
        <if test="status != null">
            and status = #{status}
        </if>
        <if test="startDate != null and startDate != ''">
            and create_time >= #{startDate}
        </if>
        <if test="endDate != null and endDate != ''">
            and create_time &lt;= #{endDate}
        </if>
    </where>
    order by ${orderField} ${order}
"""

const result = db.page(sql)

log.info("查询完成，共 " + result.total + " 条记录")

return result
