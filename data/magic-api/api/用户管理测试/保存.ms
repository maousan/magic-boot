{
  "properties": {},
  "id": "61902ii7d6jh41ljeztst2syg2eh19ul",
  "script": null,
  "groupId": "zqov8sdr55f5wmdwfqkwfvmjkd77w9bf",
  "name": "保存",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/save",
  "method": "POST",
  "parameters": [],
  "options": [
    {
      "name": "permission",
      "value": "user-test:save",
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
  "requestBody": "{\n  \"username\": \"zhangsan\",\n  \"name\": \"张三\",\n  \"phone\": \"13800138000\",\n  \"email\": \"zhangsan@example.com\",\n  \"status\": 1,\n  \"officeId\": \"1\",\n  \"roles\": [\"admin\", \"user\"]\n}",
  "headers": [],
  "paths": [],
  "responseBody": null,
  "description": "用户保存接口 - 测试请求体参数（requestBody）和字段验证",
  "requestBodyDefinition": {
    "name": "root",
    "value": "",
    "description": "用户信息对象",
    "required": false,
    "dataType": "Object",
    "type": null,
    "defaultValue": null,
    "validateType": "",
    "error": "",
    "expression": "",
    "children": [
      {
        "name": "id",
        "value": "1",
        "description": "用户ID（更新时必填）",
        "required": false,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      },
      {
        "name": "username",
        "value": "zhangsan",
        "description": "用户名（必填，3-20字符）",
        "required": true,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "length(3,20)",
        "error": "用户名长度必须在3-20字符之间",
        "expression": "",
        "children": []
      },
      {
        "name": "name",
        "value": "张三",
        "description": "姓名（必填）",
        "required": true,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      },
      {
        "name": "phone",
        "value": "13800138000",
        "description": "手机号（必填）",
        "required": true,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "@phone",
        "error": "手机号格式不正确",
        "expression": "",
        "children": []
      },
      {
        "name": "email",
        "value": "zhangsan@example.com",
        "description": "邮箱（选填）",
        "required": false,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "@email",
        "error": "邮箱格式不正确",
        "expression": "",
        "children": []
      },
      {
        "name": "status",
        "value": "1",
        "description": "状态（0禁用，1启用）",
        "required": false,
        "dataType": "Integer",
        "type": null,
        "defaultValue": "1",
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      },
      {
        "name": "officeId",
        "value": "1",
        "description": "所属机构ID",
        "required": false,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      },
      {
        "name": "roles",
        "value": "[\"admin\", \"user\"]",
        "description": "角色列表",
        "required": false,
        "dataType": "Array",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      }
    ]
  },
  "responseBodyDefinition": null
}
================================
import log
import java.util.regex.Pattern

log.info("开始保存用户信息")

// 获取请求体数据
var data = body

// 1. 必填参数验证
if(!data.username || data.username.trim() === ''){
    exit 0, '用户名不能为空'
}

if(!data.name || data.name.trim() === ''){
    exit 0, '姓名不能为空'
}

if(!data.phone || data.phone.trim() === ''){
    exit 0, '手机号不能为空'
}

// 2. 格式验证
// 用户名长度验证
if(data.username.length() < 3 || data.username.length() > 20){
    exit 0, '用户名长度必须在3-20字符之间'
}

// 手机号格式验证
if(!Pattern.matches("^1[3-9]\\d{9}$", data.phone)){
    exit 0, '手机号格式不正确'
}

// 邮箱格式验证（如果填写了）
if(data.email && data.email.trim() !== ''){
    if(!Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", data.email)){
        exit 0, '邮箱格式不正确'
    }
}

// 3. 业务逻辑验证
// 检查用户名是否已存在
var existsUser
if(data.id){
    // 更新时，检查是否是其他用户的用户名
    existsUser = db.selectOne("""
        select id from sys_user
        where is_del = 0
        and username = #{username}
        and id != #{id}
    """, data)
} else {
    // 新增时，检查用户名是否已存在
    existsUser = anyline.select("sys_user", "username", data.username)
}

if(existsUser){
    exit 0, '用户名已存在'
}

// 4. 执行保存操作
var userId = data.id

if(userId){
    // 更新
    log.info("更新用户：" + userId)
    anyline.update("sys_user", data)
} else {
    // 新增
    log.info("新增用户：" + data.username)
    anyline.insert("sys_user", data, true)
}

// 5. 处理角色关系（如果提供了角色列表）
if(data.roles && data.roles.length() > 0){
    // 删除旧的角色关系
    if(userId){
        anyline.deletes("sys_user_role", "user_id", userId)
    }

    // 插入新的角色关系
    var newUserId = userId ? userId : data.id
    for(roleCode in data.roles){
        var role = anyline.select("sys_role", "code", roleCode)
        if(role){
            anyline.insert("sys_user_role", {
                user_id: newUserId,
                role_id: role.id,
                create_time: now()
            })
        }
    }
}

log.info("用户保存成功")

return true
