# 定时任务模板

## JSON 元数据

```json
{
  "properties": {},
  "id": "自动生成UUID",
  "script": null,
  "groupId": "从 job 目录的 group.json 读取",
  "name": "{task-name}",
  "createTime": null,
  "updateTime": null,
  "lock": null,
  "createBy": null,
  "updateBy": null,
  "path": "/{task-path}",
  "cron": "0 0 2 * * ?",
  "enabled": true,
  "description": "任务描述"
}
```

## 脚本代码

```javascript
import log

log.info("定时任务开始执行")

try {
    // 任务逻辑
    // ...

    log.info("定时任务执行完成")

    return 'success'

} catch(e) {
    log.error("定时任务执行异常: " + e.message)
    return 'error: ' + e.message
}
```

## Cron 表达式说明

| 表达式 | 含义 |
|-------|------|
| `0 0 2 * * ?` | 每天凌晨 2 点 |
| `0 */5 * * * ?` | 每 5 分钟 |
| `0 0 0/1 * * ?` | 每小时 |
| `0 0 12 * * ?` | 每天中午 12 点 |
| `0 0 12 ? * WED` | 每周三中午 12 点 |

## 使用说明

1. 替换 `{task-name}` 为任务名称
2. 设置正确的 cron 表达式
3. 必须导入 log 模块
4. 使用 try-catch 包裹整个逻辑
5. 返回 'success' 或错误信息
