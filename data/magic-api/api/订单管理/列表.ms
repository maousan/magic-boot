{
  "properties": {},
  "id": "v2e0pwz13kb6m5ebkj7vshyva1e0y3ub",
  "script": null,
  "groupId": "tyrjbfgxpxxpwyr5xhtnlqd37vqraxzj",
  "name": "列表",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/list",
  "method": "GET",
  "parameters": [],
  "options": [
    {
      "name": "permission",
      "value": "order:view",
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
  "description": "订单列表查询接口，支持按订单编号和订单状态查询，按创建时间倒序排列",
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
let sql = """
    select * from sys_order
    <where>
        and is_del = 0
        <if test="order_no != null and order_no != ''">
            and order_no like concat('%', #{order_no}, '%')
        </if>
        <if test="status != null and status != ''">
            and status = #{status}
        </if>
    </where>
    order by create_date desc
"""

const page = db.page(sql)

return page
