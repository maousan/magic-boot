# 里程碑：ExtendedMagicJobController 迁移至 .ms

## 日期
2026-03-27

## 里程碑目标
将任务暂停/恢复/触发/历史/状态相关能力迁移到 magic-api 脚本接口，减少 Java Controller 维护成本。

## 完成项
- 完成 5 个任务控制接口的 `.ms` 化。
- 路径统一到 `/system/job/*`。
- 增加 `.http` 回归用例，覆盖迁移接口。
- 补充计划文档与变更日志文档。

## 产出文件
- `data/magic-api/api/系统管理/任务管理/暂停任务.ms`
- `data/magic-api/api/系统管理/任务管理/恢复任务.ms`
- `data/magic-api/api/系统管理/任务管理/立即执行任务.ms`
- `data/magic-api/api/系统管理/任务管理/查询任务历史.ms`
- `data/magic-api/api/系统管理/任务管理/查询任务状态.ms`
- `http/test-magic-job-ms.http`
- `milestones/2026-03-27-magic-job-ms-migration-plan.md`
- `milestones/2026-03-27-magic-job-ms-migration-changelog.md`
- `milestones/2026-03-27-magic-job-ms-migration.md`
