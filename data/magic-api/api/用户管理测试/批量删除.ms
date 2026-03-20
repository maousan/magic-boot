{
  "properties": {},
  "id": "z29qa9mgozqxuvm2ouppt9wm8ugabbbf",
  "script": null,
  "groupId": "zqov8sdr55f5wmdwfqkwfvmjkd77w9bf",
  "name": "批量删除",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/batch-delete",
  "method": "DELETE",
  "parameters": [
    {
      "name": "ids",
      "value": "1,2,3",
      "description": "用户ID列表（逗号分隔）",
      "required": true,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": "用户ID列表不能为空",
      "expression": null,
      "children": null
    }
  ],
  "options": [
    {
      "name": "permission",
      "value": "user-test:delete",
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
  "description": "用户批量删除接口 - 测试批量操作参数（逗号分隔的 ID 列表）",
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

log.info("开始批量删除用户")

// 参数验证
if(!ids || ids.trim() === ''){
    exit 0, '用户ID列表不能为空'
}

// 分割 ID 列表
var idList = ids.split(',')
log.info("准备删除 " + idList.length() + " 个用户")

var successCount = 0
var failCount = 0
var errors = []

// 遍历删除
for(userId in idList){
    // 去除空格
    var id = userId.trim()

    if(!id || id === ''){
        continue
    }

    try {
        // 检查用户是否存在
        var user = anyline.select("sys_user", "id", id)

        if(!user){
            log.warn("用户不存在：" + id)
            errors.add("用户ID " + id + " 不存在")
            failCount++
            continue
        }

        // 删除用户角色关系
        anyline.deletes("sys_user_role", "user_id", id)

        // 删除用户（逻辑删除）
        anyline.update("sys_user", {
            id: id,
            is_del: 1,
            update_time: now()
        })

        log.info("删除用户成功：" + user.username + " (ID: " + id + ")")
        successCount++

    } catch(e) {
        log.error("删除用户失败：" + id + "，错误：" + e.message)
        errors.add("删除用户ID " + id + " 失败：" + e.message)
        failCount++
    }
}

// 返回结果
var result = {
    total: idList.length(),
    success: successCount,
    fail: failCount,
    errors: errors
}

log.info("批量删除完成 - 成功：" + successCount + "，失败：" + failCount)

return result
