# 查询任务列表.ms 功能完善变更日志

## 变更日期
2026-03-27

## 变更类型
功能调整

## 变更内容
- 将 `查询任务列表.ms` 从占位实现（返回固定字符串）调整为真实列表查询实现。
- 新增筛选参数：`name`、`path`、`enabled`、`jobType`。
- 新增分页参数：`page`、`size`，返回结构补充 `total/list/page/size`。
- 列表数据来源改为 `MagicResourceService.files("job")`，对接 job 资源。
- 为每条任务追加调度状态字段：`exists`、`paused`、`nextFireTime`。
- 按 `updateTime/createTime` 倒序返回。

## 兼容性说明
- 接口路径与方法不变：`GET /system/job/list`。
- 旧调用方无需修改，仍可不传参数直接获取列表。
