# ms 模板速查

## 列表查询模板（GET）
- 路径：`/list`
- 权限：`{module}:view`
- 要点：`db.page(sql)`、`and is_del = 0`、按创建时间倒序

脚本骨架：
```javascript
let sql = """
    select * from {table_name}
    <where>
        and is_del = 0
    </where>
    order by create_date desc
"""
return db.page(sql)
```

## 保存/更新模板（POST）
- 路径：`/save`
- 权限：`{module}:save`
- 要点：根据 `id` 判断 insert/update

脚本骨架：
```javascript
var data = body
if(data.id){
    anyline.update("{table_name}", data)
}else{
    anyline.insert("{table_name}", data)
}
return true
```

## 定时任务模板（job）
- 要点：导入 `log`、`try-catch` 包裹全流程、返回 success/error

脚本骨架：
```javascript
import log
log.info("任务开始")
try {
    // task logic
    return "success"
} catch(e) {
    log.error("任务异常: " + e.message)
    return "error: " + e.message
}
```

## 常用 cron 示例
- `0 0 2 * * ?` 每天 02:00
- `0 */5 * * * ?` 每 5 分钟
- `0 0 0/1 * * ?` 每小时
