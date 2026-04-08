# 2026-03-29 Flyway 全库自动迁移机制实施计划

## 背景
当前数据库结构依赖 `db/*.sql` 手工执行，发布一致性和可追溯性不足。需要引入 Flyway 自动迁移并以启动失败阻断不一致运行。

## 实施项
1. 在 `magic-boot-master` 引入 `flyway-core` 依赖。
2. 在 `application.yml` 增加 Flyway 配置：
   - `spring.flyway.enabled=true`
   - `spring.flyway.locations=classpath:db/migration`
   - `spring.flyway.baseline-on-migrate=true`
   - `spring.flyway.baseline-version=20260329.005`
   - `spring.flyway.validate-on-migrate=true`
3. 新建迁移目录并拆分版本脚本：
   - `V20260329.001` 核心基线 schema（来自 `db/magic-boot.sql` 的 DDL）
   - `V20260329.002` sys_file 最新结构
   - `V20260329.003` 插件基础表结构
   - `V20260329.004` job 历史表结构
   - `V20260329.005` 历史 update20240410 兼容迁移
   - `V20260329.006` magic_plugin 治理字段增量迁移
4. 更新文档与发布规范，明确禁止继续手工执行 `db/*.sql`。

## 验证
- 运行：`mvn -pl magic-boot-master test`
- 检查：迁移脚本命名、配置生效、编译与测试通过。
