# Job 插件 Quartz 重构开发计划

## TL;DR

> **目标**：将 magic-api-plugin-job 从 Spring TaskScheduler 重构为真正的 Quartz 调度器
> 
> **核心改动**：
> - 真正使用 Quartz（替换 ThreadPoolTaskScheduler）
> - 新增执行历史表（保留 30 天）
> - 扩展 JobInfo 模型（misfirePolicy、concurrent）
> - 新增 REST API（暂停/恢复/历史查询）
> - 前端编辑器集成
> 
> **不包含**：任务依赖、集群支持
> 
> **估计工作量**：中等（4-6 小时）
> **并行执行**：YES - 2 波次

---

## Context

### 原始请求
用户希望完善 job 插件，改成使用 Quartz 调度器。

### 确认的需求
| 需求项 | 决定 | 理由 |
|--------|------|------|
| 持久化 | 混合方案 | 配置存 magic-api 资源，历史存数据库 |
| 集群 | 不需要 | 单实例部署 |
| 执行历史 | 需要，保留 30 天 | 便于排查问题 |
| 暂停/恢复 | 需要 | 临时停止任务 |
| 错过触发 | 需要 | misfire 处理策略 |
| 并发控制 | 需要 | 同任务是否并发执行 |
| 任务依赖 | **不需要** | 用户明确排除 |
| 前端 | 集成到编辑器 | 非独立页面 |

### 现有代码问题
1. `spring-boot-starter-quartz` 已依赖但未使用
2. 使用 `ThreadPoolTaskScheduler` 而非 Quartz
3. 无执行历史记录
4. API 功能有限（仅 `/job/execute`）

---

## Work Objectives

### Core Objective
将 job 插件重构为真正的 Quartz 调度器，提供完整的任务管理能力。

### Concrete Deliverables
- [ ] `magic_job_log` 数据库表
- [ ] 重构的 `JobMagicDynamicRegistry`（使用 Quartz）
- [ ] 扩展的 `JobInfo` 模型
- [ ] 新增的 REST API 端点
- [ ] 更新的前端编辑器组件

### Definition of Done
- [ ] 任务可动态创建/更新/删除
- [ ] 任务可暂停/恢复
- [ ] 执行历史可查询（保留 30 天）
- [ ] 错过触发可正确处理
- [ ] 并发控制生效
- [ ] 前端可查看任务状态和历史

### Must Have
- Quartz 调度器正常工作
- 执行历史正确记录
- 暂停/恢复功能
- 错过触发处理

### Must NOT Have (Guardrails)
- **不实现任务依赖**：用户明确不需要
- **不实现集群**：单实例部署
- **不使用 JDBC JobStore**：使用 RAM JobStore
- **不修改 magic-api 核心**：仅修改插件模块

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: NO（当前无测试）
- **Automated tests**: Tests after
- **Agent-Executed QA**: ALWAYS（每个任务包含 QA 场景）

### QA Policy
每个任务包含 Agent-Executed QA 场景：
- **API 测试**：curl 发送请求，验证状态码和响应
- **数据库验证**：查询数据库，验证记录存在
- **调度验证**：等待触发时间，验证任务执行

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (基础设施 - 2 任务并行):
├── Task 1: 数据库表创建 [quick]
└── Task 2: JobInfo 模型扩展 [quick]

Wave 2 (核心重构 - 2 任务并行):
├── Task 3: Quartz 调度器配置 [unspecified-high]
└── Task 4: MagicScriptJob 实现 [unspecified-high]

Wave 3 (业务逻辑 - 2 任务并行):
├── Task 5: JobMagicDynamicRegistry 重构 [deep]
└── Task 6: JobLogService 实现 [unspecified-high]

Wave 4 (API 层 - 2 任务并行):
├── Task 7: MagicJobController 扩展 [quick]
└── Task 8: 前端编辑器集成 [visual-engineering]

Wave FINAL (验证):
├── Task F1: 集成测试 [deep]
├── Task F2: 代码质量审查 [unspecified-high]
└── Task F3: 手动 QA [unspecified-high]
```

### Dependency Matrix
- **1-2**: — (无依赖)
- **3-4**: — (无依赖)
- **5**: 1, 2, 3, 4
- **6**: 1, 3
- **7**: 5, 6
- **8**: 7

---

## TODOs

- [ ] 1. 创建执行历史数据库表

  **What to do**:
  - 创建 `magic_job_log` 表的 SQL 脚本
  - 添加到 `db/` 目录或提供初始化脚本

  **SQL 脚本**:
  ```sql
  CREATE TABLE magic_job_log (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      job_id VARCHAR(64) NOT NULL COMMENT '任务ID',
      job_name VARCHAR(128) COMMENT '任务名称',
      job_group VARCHAR(128) COMMENT '任务分组',
      script_path VARCHAR(512) COMMENT '脚本路径',
      start_time DATETIME NOT NULL COMMENT '开始时间',
      end_time DATETIME COMMENT '结束时间',
      duration BIGINT COMMENT '耗时(毫秒)',
      status VARCHAR(16) NOT NULL COMMENT '状态: RUNNING/SUCCESS/FAILED',
      result TEXT COMMENT '执行结果',
      exception_message TEXT COMMENT '异常信息',
      exception_stack TEXT COMMENT '异常堆栈',
      trigger_type VARCHAR(16) COMMENT '触发类型: CRON/MANUAL',
      triggered_by VARCHAR(128) COMMENT '触发者',
      create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
      INDEX idx_job_id (job_id),
      INDEX idx_start_time (start_time),
      INDEX idx_status (status)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行历史';
  ```

  **Must NOT do**:
  - 不修改现有表结构
  - 不创建 Quartz 标准表（使用 RAM JobStore）

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 2)

  **QA Scenarios**:
  ```
  Scenario: 验证表结构创建成功
    Tool: Bash (mysql client)
    Steps:
      1. mysql -u magic -p'Magic@2026' magic-boot -e "SHOW TABLES LIKE 'magic_job_log'"
    Expected Result: 返回 magic_job_log 表
    Evidence: .sisyphus/evidence/task-01-table-created.log
  ```

  **Commit**: NO (与 Task 2 一起提交)

---

- [ ] 2. 扩展 JobInfo 模型

  **What to do**:
  - 在 `JobInfo.java` 中添加新字段
  - 添加 `MisfirePolicy` 枚举
  - 更新 copy() 和 equals() 方法

  **新增字段**:
  ```java
  // 新增字段
  private MisfirePolicy misfirePolicy = MisfirePolicy.SMART;
  private boolean concurrent = false;
  private int maxRetry = 0;
  private long timeout = 0;
  
  public enum MisfirePolicy {
      SMART,           // 智能处理
      IGNORE,          // 忽略
      FIRE_ONCE_NOW    // 立即执行一次
  }
  ```

  **References**:
  - `magic-api-plugin-job/src/main/java/.../model/JobInfo.java` - 现有模型

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Task 1)

  **QA Scenarios**:
  ```
  Scenario: 验证 JobInfo 序列化
    Tool: Bash (curl)
    Steps:
      1. 创建一个带新字段的 JobInfo JSON
      2. POST /magic/job 验证字段保存
    Expected Result: 返回包含新字段的任务对象
    Evidence: .sisyphus/evidence/task-02-model-extended.json
  ```

  **Commit**: YES
  - Message: `feat(job): extend JobInfo with misfirePolicy and concurrent fields`
  - Files: `magic-api-plugin-job/src/main/java/.../model/JobInfo.java`

---

- [ ] 3. 配置 Quartz 调度器

  **What to do**:
  - 修改 `MagicAPIJobConfiguration.java`
  - 创建 `SchedulerFactoryBean` 替代 `ThreadPoolTaskScheduler`
  - 配置 RAM JobStore

  **代码示例**:
  ```java
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
      SchedulerFactoryBean factory = new SchedulerFactoryBean();
      factory.setJobFactory(springBeanJobFactory);
      factory.setOverwriteExistingJobs(true);
      factory.setAutoStartup(true);
      // RAM JobStore 配置
      Properties properties = new Properties();
      properties.put("org.quartz.threadPool.threadCount", 
          String.valueOf(config.getPool().getSize()));
      factory.setQuartzProperties(properties);
      return factory;
  }
  ```

  **References**:
  - `magic-api-plugin-job/src/main/java/.../starter/MagicAPIJobConfiguration.java`
  - Spring Boot Quartz 文档

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Task 4)

  **QA Scenarios**:
  ```
  Scenario: 验证 Quartz 调度器启动
    Tool: Bash (curl)
    Steps:
      1. 启动应用
      2. GET /actuator/beans | grep scheduler
    Expected Result: 返回 schedulerFactoryBean
    Evidence: .sisyphus/evidence/task-03-quartz-started.log
  ```

  **Commit**: NO (与 Task 4 一起提交)

---

- [ ] 4. 实现 MagicScriptJob

  **What to do**:
  - 创建 `MagicScriptJob` 实现 `Job` 接口
  - 执行 magic-api 脚本
  - 记录执行历史

  **代码示例**:
  ```java
  public class MagicScriptJob implements Job {
      @Override
      public void execute(JobExecutionContext context) throws JobExecutionException {
          String jobId = context.getJobDetail().getKey().getName();
          String script = context.getJobDetail().getJobDataMap().getString("script");
          
          JobLog log = new JobLog();
          log.setJobId(jobId);
          log.setStartTime(new Date());
          log.setTriggerType("CRON");
          
          try {
              Object result = ScriptManager.executeScript(script, new MagicScriptContext());
              log.setStatus("SUCCESS");
              log.setResult(String.valueOf(result));
          } catch (Exception e) {
              log.setStatus("FAILED");
              log.setExceptionMessage(e.getMessage());
              log.setExceptionStack(ExceptionUtils.getStackTrace(e));
          } finally {
              log.setEndTime(new Date());
              log.setDuration(log.getEndTime().getTime() - log.getStartTime().getTime());
              jobLogService.save(log);
          }
      }
  }
  ```

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Task 3)

  **Commit**: YES
  - Message: `feat(job): implement MagicScriptJob with history logging`
  - Files: `MagicScriptJob.java`, `MagicAPIJobConfiguration.java`

---

- [ ] 5. 重构 JobMagicDynamicRegistry

  **What to do**:
  - 将 `ThreadPoolTaskScheduler` 替换为 `Scheduler`
  - 使用 Quartz API 注册/取消任务
  - 实现暂停/恢复功能

  **核心方法**:
  ```java
  @Override
  protected boolean register(MappingNode<JobInfo> mappingNode) {
      JobInfo entity = mappingNode.getEntity();
      String jobName = entity.getId();
      String groupName = "magic-api";
      
      JobDetail jobDetail = JobBuilder.newJob(MagicScriptJob.class)
          .withIdentity(jobName, groupName)
          .usingJobData("script", entity.getScript())
          .storeDurably(false)
          .build();
      
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
          .cronSchedule(entity.getCron())
          .withMisfireHandlingInstructionFireAndProceed(); // 根据 misfirePolicy 选择
      
      CronTrigger trigger = TriggerBuilder.newTrigger()
          .withIdentity(jobName + "_trigger", groupName)
          .withSchedule(scheduleBuilder)
          .build();
      
      scheduler.scheduleJob(jobDetail, trigger);
      return true;
  }
  ```

  **References**:
  - `magic-api-plugin-job/src/main/java/.../service/JobMagicDynamicRegistry.java`

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 6)
  - **Blocked By**: Task 1, 2, 3, 4

  **QA Scenarios**:
  ```
  Scenario: 验证任务注册
    Tool: Bash (curl)
    Steps:
      1. 创建一个 cron='0/5 * * * * ?' 的任务
      2. 等待 5 秒
      3. 查询执行历史
    Expected Result: 执行历史表有新记录
    Evidence: .sisyphus/evidence/task-05-job-registered.log
  
  Scenario: 验证任务取消
    Tool: Bash (curl)
    Steps:
      1. 删除任务
      2. 等待 5 秒
      3. 查询执行历史
    Expected Result: 没有新的执行记录
    Evidence: .sisyphus/evidence/task-05-job-unregistered.log
  ```

  **Commit**: YES
  - Message: `refactor(job): use Quartz Scheduler instead of ThreadPoolTaskScheduler`

---

- [ ] 6. 实现 JobLogService

  **What to do**:
  - 创建 `JobLogService` 服务类
  - 实现保存/查询/清理执行历史
  - 添加 30 天自动清理逻辑

  **代码示例**:
  ```java
  @Service
  public class JobLogService {
      public void save(JobLog log);
      public Page<JobLog> list(String jobId, int page, int size);
      public void cleanOldLogs(); // 清理 30 天前的记录
  }
  ```

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 3 (with Task 5)
  - **Blocked By**: Task 1

  **Commit**: YES
  - Message: `feat(job): add JobLogService with 30-day retention`

---

- [ ] 7. 扩展 MagicJobController

  **What to do**:
  - 添加暂停/恢复/立即执行端点
  - 添加执行历史查询端点
  - 添加下次执行时间预览端点

  **新增端点**:
  ```
  POST /job/{id}/pause      # 暂停任务
  POST /job/{id}/resume     # 恢复任务
  POST /job/{id}/trigger    # 立即执行
  GET  /job/{id}/history    # 执行历史
  GET  /job/{id}/next-times # 下 N 次执行时间
  ```

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with Task 8)
  - **Blocked By**: Task 5, 6

  **QA Scenarios**:
  ```
  Scenario: 验证暂停/恢复
    Tool: Bash (curl)
    Steps:
      1. POST /job/{id}/pause
      2. 等待 10 秒
      3. 验证任务未执行
      4. POST /job/{id}/resume
      5. 验证任务恢复执行
    Expected Result: 暂停期间无执行，恢复后有执行
    Evidence: .sisyphus/evidence/task-07-pause-resume.log
  ```

  **Commit**: YES
  - Message: `feat(job): add pause/resume/history API endpoints`

---

- [ ] 8. 前端编辑器集成

  **What to do**:
  - 在 magic-editor 中添加任务管理 UI
  - 显示任务状态（运行中/暂停）
  - 显示执行历史
  - 添加暂停/恢复/立即执行按钮

  **UI 组件**:
  - 任务状态徽章
  - 执行历史列表（分页）
  - 操作按钮组（暂停/恢复/触发）
  - 下次执行时间预览

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 4 (with Task 7)
  - **Blocked By**: Task 7

  **Commit**: YES
  - Message: `feat(job): integrate job management into magic-editor`

---

## Final Verification Wave

- [ ] F1. **集成测试**
  运行所有 QA 场景，验证端到端功能。
  Output: `QA Scenarios [N/N pass]`

- [ ] F2. **代码质量审查**
  运行 `tsc --noEmit`（如适用）+ 代码审查。
  Output: `Build [PASS] | Files [N clean]`

- [ ] F3. **手动 QA**
  启动应用，通过 magic-api web UI 测试所有功能。

---

## Commit Strategy

1. `feat(job): extend JobInfo with misfirePolicy and concurrent fields`
2. `feat(job): implement MagicScriptJob with history logging`
3. `refactor(job): use Quartz Scheduler instead of ThreadPoolTaskScheduler`
4. `feat(job): add JobLogService with 30-day retention`
5. `feat(job): add pause/resume/history API endpoints`
6. `feat(job): integrate job management into magic-editor`

---

## Success Criteria

### Verification Commands
```bash
# 验证 Quartz 调度器
curl http://localhost:8081/actuator/beans | grep scheduler

# 创建任务
curl -X POST http://localhost:8081/magic/job \
  -H "Content-Type: application/json" \
  -d '{"cron":"0/10 * * * * ?","enabled":true}'

# 暂停任务
curl -X POST http://localhost:8081/magic/job/{id}/pause

# 查询历史
curl http://localhost:8081/magic/job/{id}/history?page=1&size=10
```

### Final Checklist
- [ ] 任务可动态创建/更新/删除
- [ ] 任务可暂停/恢复
- [ ] 执行历史可查询（保留 30 天）
- [ ] 错过触发可正确处理
- [ ] 并发控制生效
- [ ] 前端可查看任务状态和历史
