# LiteFlow 任务编排插件开发计划

## TL;DR

> **Quick Summary**: 为 magic-boot 开发基于 LiteFlow 组件式规则引擎的任务编排插件，支持在浏览器中动态编写 Groovy 脚本和 EL 表达式流程编排，实现 API 调用和事件驱动两种触发方式。
> 
> **Deliverables**:
> - magic-api-plugin-liteflow 插件模块（后端 + 前端）
> - LiteFlow 与 magic-boot 集成配置
> - 流程定义文件存储和管理
> - 流程执行引擎（API 调用 + 事件驱动）
> - Groovy 脚本动态编译和执行
> - 流程监控和统计功能
> - 流程版本管理和回滚
> 
> **Estimated Effort**: Medium-Large (1-2 周)
> **Parallel Execution**: YES - 4 waves
> **Critical Path**: 插件架构 → LiteFlow 集成 → 执行引擎 → 前端编辑 → 监控功能

---

## Context

### Original Request
编写一个任务编排插件，核心技术使用 LiteFlow 组件式规则引擎，集成 LiteFlow Groovy 脚本编程。插件允许编写编排规则，每个组件节点可以动态编写 Groovy 脚本。

### Interview Summary

**Key Discussions**:
- **集成深度**: 基础级 - 仅 LiteFlow 核心引擎，不包含高级可视化编排
- **编辑方式**: 方案 A - 文本编辑，使用现有 magic-api 编辑器
- **存储方式**: 文件存储 - 使用 magic-api 现有文件存储机制
- **脚本范围**: 业务逻辑片段，访问流程上下文变量
- **触发方式**: API 调用 + 事件驱动（其他 API 完成时触发）
- **监控需求**: 基础日志 + 执行追踪 + 统计仪表盘
- **高级功能**: 版本管理 + 复杂规则（条件分支、并行执行）
- **工期要求**: 1-2 周

**Research Findings**:
- magic-boot 使用标准 Maven 多模块结构
- 插件遵循统一架构模式（model/service/starter/web + console 前端）
- 前端使用 Vue 3 + Vite，通过 exec-maven-plugin 集成构建
- magic-api 文件存储位于 `data/magic-api/` 目录

### Metis Review

**Identified Gaps** (addressed):
- ✅ 明确 LiteFlow 版本号选择（与 Spring Boot 3.1.2 兼容）
- ✅ 确定事件驱动集成点（magic-api 脚本执行拦截器）
- ✅ 定义文件存储格式和内容
- ✅ 确保监控数据的持久化策略

---

## Work Objectives

### Core Objective
开发一个符合 magic-boot 插件规范的 LiteFlow 任务编排插件，支持流程定义、Groovy 脚本编写、API/事件触发、执行监控和版本管理。

### Concrete Deliverables
- `magic-api-plugin-liteflow/` 插件模块
- `src/main/java/org/ssssssss/magicapi/liteflow/` 后端实现
- `src/console/` 前端编辑器资源
- `data/magic-api/liteflow/` 流程定义存储目录
- 流程执行日志和统计数据存储

### Definition of Done
- [ ] `mvn clean package -Pprod` 构建成功
- [ ] 插件自动注册到 magic-boot
- [ ] 可在 magic-api Web UI 中编辑和管理流程
- [ ] API 调用和事件驱动触发均正常工作
- [ ] 流程监控和统计功能可用
- [ ] 版本管理功能可用

### Must Have
- LiteFlow 核心引擎集成（Spring Boot 3.1.2 兼容）
- Groovy 脚本动态编译和执行
- EL 表达式流程编排
- API 调用触发接口
- 事件驱动触发（集成 magic-api 执行拦截器）
- 流程执行日志记录
- 流程追踪（查看每次执行详情）
- 统计功能（调用次数、成功率、耗时）
- 流程版本管理（发布/回滚）
- 条件分支和并行执行支持

### Must NOT Have (Guardrails)
- ❌ 可视化流程编排（复杂度高，不符合 1-2 周工期）
- ❌ 复杂的流程模板系统
- ❌ 定时任务触发（已有 job 插件）
- ❌ 独立的监控 UI（复用 magic-api 界面）
- ❌ 修改 magic-boot 核心代码

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.
> Acceptance criteria requiring "user manually tests/confirms" are FORBIDDEN.

### Test Decision
- **Infrastructure exists**: YES (magic-boot 已有测试基础设施)
- **Automated tests**: Tests-after（实施完成后补充测试）
- **Framework**: JUnit 5 + Spring Boot Test
- **Agent-Executed QA**: ALWAYS（每个任务包含详细 QA 场景）

### QA Policy
每个任务 MUST 包含 agent-executed QA 场景：
- **后端**: Bash 调用 API + curl 验证响应
- **前端**: Playwright 浏览器自动化测试
- **集成**: 完整流程执行验证

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — 基础架构 + 配置):
├── Task 1: 插件模块脚手架 + Maven 配置 [quick]
├── Task 2: LiteFlow 依赖集成 + Spring Boot 配置 [unspecified-low]
├── Task 3: 流程定义数据模型设计 [unspecified-low]
├── Task 4: 流程文件存储路径和格式定义 [quick]
└── Task 5: 插件国际化资源配置 [quick]

Wave 2 (After Wave 1 — 核心引擎):
├── Task 6: LiteFlowComponent 基类 + Groovy 脚本执行器 [unspecified-high]
├── Task 7: 流程加载和解析服务 [unspecified-high]
├── Task 8: 流程执行引擎（API 触发） [deep]
├── Task 9: magic-api 执行拦截器 + 事件驱动触发 [deep]
└── Task 10: 流程执行日志记录服务 [unspecified-high]

Wave 3 (After Wave 2 — 前端编辑器):
├── Task 11: 插件前端入口 + 路由注册 [visual-engineering]
├── Task 12: 流程列表/创建/删除组件 [visual-engineering]
├── Task 13: 流程编辑器组件（EL 表达式 + Groovy 脚本） [visual-engineering]
├── Task 14: REST API 接口实现 [quick]
└── Task 15: 流程验证（语法检查、组件引用验证） [unspecified-high]

Wave 4 (After Wave 3 — 监控和版本):
├── Task 16: 流程执行追踪组件 [visual-engineering]
├── Task 17: 统计仪表盘组件 [visual-engineering]
├── Task 18: 流程版本管理功能 [unspecified-high]
└── Task 19: 集成测试 + 文档 [deep]

Wave 5 (After ALL tasks — 验证):
└── Task 20-23: 最终验证波次（4 个并行 review 任务）

Critical Path: Task 1 → Task 2 → Task 6 → Task 8 → Task 11 → Task 13 → Task 16 → Task 20-23
Parallel Speedup: ~60% faster than sequential
Max Concurrent: 5 (Wave 1)
```

### Dependency Matrix

| Task | Depends On | Blocks | Wave |
|------|-----------|--------|------|
| 1-5 | — | 6, 7, 11, 14 | 1 |
| 6 | 2, 3 | 8, 9, 13 | 2 |
| 7 | 3, 4 | 8, 9 | 2 |
| 8 | 6, 7 | 13, 16, 19 | 2 |
| 9 | 6, 7 | 19 | 2 |
| 10 | 6 | 16, 17 | 2 |
| 11 | 4, 5 | 12, 13 | 3 |
| 12 | 11 | 13 | 3 |
| 13 | 6, 11, 12 | 16, 17 | 3 |
| 14 | 7, 8 | 15, 16, 17, 18 | 3 |
| 15 | 14 | 19 | 3 |
| 16 | 10, 13, 14 | 19 | 4 |
| 17 | 10, 14 | 19 | 4 |
| 18 | 7, 14 | 19 | 4 |
| 19 | 15, 16, 17, 18 | 20-23 | 4 |
| 20-23 | 19 | — | 5 |

### Agent Dispatch Summary

- **Wave 1**: **quick** - 5 个并行任务（脚手架、配置、模型、存储、i18n）
- **Wave 2**: **deep/unspecified** - 5 个并行任务（核心引擎、存储、执行、事件、日志）
- **Wave 3**: **visual-engineering/quick** - 5 个并行任务（前端入口、组件、编辑器、API）
- **Wave 4**: **visual-engineering/unspecified/deep** - 4 个并行任务（监控、统计、版本、集成测试）
- **Wave 5**: **oracle/unspecified-high/deep** - 4 个并行 review 任务

---

## TODOs

<incremental_write_protocol>
**Incremental Write Protocol Active**
Write skeleton first → Edit tasks in batches (4 per Edit)
</incremental_write_protocol>

- [ ] 1. 插件模块脚手架 + Maven 配置

  **What to do**:
  - 创建 `magic-api-plugins/magic-api-plugin-liteflow/` 目录结构
  - 创建 `pom.xml` 配置（参考 magic-api-plugin-job）
  - 添加 LiteFlow 依赖（liteflow-core，与 Spring Boot 3.1.2 兼容的版本）
  - 配置 exec-maven-plugin 执行前端构建
  - 配置 maven-resources-plugin 复制前端产物
  
  **Must NOT do**:
  - 不要添加不需要的依赖（如 Quartz、MQTT 等）
  - 不要修改根 pom.xml
  
  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Reason**: 目录创建和 XML 配置，简单机械性任务
  - **Skills**: []
  
  **References**:
  - `magic-api-plugins/magic-api-plugin-job/pom.xml` - 依赖和管理插件 pom 模板
  - `magic-api-plugins/pom.xml` - 父级插件依赖管理
  
  **Acceptance Criteria**:
  - [ ] pom.xml 创建成功，Maven 可识别模块
  - [ ] `mvn clean package -Pdev` 构建成功
  
  **QA Scenarios**:
  
  ```
  Scenario: Maven 模块化验证
    Tool: Bash
    Steps:
      1. 运行：mvn clean package -Pdev -DskipTests -pl magic-api-plugins/magic-api-plugin-liteflow
      2. 验证：target/ 目录生成 jar 文件
      3. 检查：jar 中包含正确的包路径
    Expected Result: 构建成功，无 Maven 错误
    Evidence: .sisyphus/evidence/task-1-maven-build.log
  ```
  
  **Commit**: YES (与 Task 2-5 合并)
  - Message: `feat(liteflow): 插件模块脚手架和基础配置`
  - Files: `magic-api-plugin-liteflow/**/*`

---

- [ ] 2. LiteFlow 依赖集成 + Spring Boot 自动配置

  **What to do**:
  - 查找与 Spring Boot 3.1.2 兼容的 LiteFlow 版本
  - 在 pom.xml 中添加 liteflow-core 依赖
  - 创建 `MagicLiteFlowConfig.java` 配置属性类（参考 MagicJobConfig）
  - 创建 `MagicLiteFlowConfiguration.java` 自动配置类
  - 启用 LiteFlow Spring Boot 集成
  
  **Must NOT do**:
  - 不要手动配置 LiteFlow Bean（使用自动配置）
  - 不要绕过 Spring Boot 的自动配置机制
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-low`
  - **Reason**: Spring Boot 自动配置，需要理解配置约定
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/starter/` - 配置类参考
  - LiteFlow 官方文档 - Spring Boot 集成指南
  
  **Acceptance Criteria**:
  - [ ] LiteFlow 依赖添加成功
  - [ ] Spring Boot 启动时无冲突
  - [ ] LiteFlow FlowExecutor Bean 可注入
  
  **QA Scenarios**:
  
  ```
  Scenario: LiteFlow Spring 集成验证
    Tool: Bash
    Steps:
      1. 启动应用：mvn spring-boot:run -Pdev
      2. 验证：Spring Boot 启动成功，无 LiteFlow 相关错误
      3. 使用 curl 调用健康检查接口
    Expected Result: 应用正常启动，LiteFlow 组件可用
    Evidence: .sisyphus/evidence/task-2-spring-boot.log
  ```
  
  **Commit**: 与 Task 1 合并

---

- [ ] 3. 流程定义数据模型设计

  **What to do**:
  - 创建 `FlowDefinition.java` 模型类（包含：flowId、flowName、elExpression、description、enabled、version、createTime、updateTime）
  - 创建 `FlowComponent.java` 模型类（包含：componentId、componentName、scriptContent、componentType）
  - 实现序列化/反序列化方法（JSON ↔ Java）
  - 添加验证逻辑（EL 表达式语法、组件引用完整性）
  
  **Must NOT do**:
  - 不要使用数据库实体注解（使用文件存储）
  - 不要添加复杂的状态机逻辑
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-low`
  - **Reason**: 数据模型设计，需要理解业务需求
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/model/JobInfo.java` - 模型类参考
  - LiteFlow 官方文档 - EL 表达式语法
  
  **Acceptance Criteria**:
  - [ ] 模型类包含所有必要字段
  - [ ] JSON 序列化/反序列化正常
  - [ ] 验证逻辑正确
  
  **QA Scenarios**:
  
  ```
  Scenario: FlowDefinition 序列化验证
    Tool: Bash
    Steps:
      1. 编写单元测试：创建 FlowDefinition 实例
      2. 序列化为 JSON
      3. 反序列化为 Java 对象
      4. 验证字段值一致
    Expected Result: 序列化/反序列化成功，字段完整
    Evidence: .sisyphus/evidence/task-3-model-test.log
  ```
  
  **Commit**: 与 Task 1 合并

---

- [ ] 4. 流程文件存储路径和格式定义

  **What to do**:
  - 定义存储路径：`data/magic-api/liteflow/flow/`（流程定义），`data/magic-api/liteflow/component/`（组件脚本）
  - 创建 FlowStorageService.java 存储服务接口
  - 实现文件读写方法（create、read、update、delete、list）
  - 实现文件监听和自动刷新（参考 magic-api 实现）
  - 添加文件备份逻辑（修改前备份）
  
  **Must NOT do**:
  - 不要使用数据库存储
  - 不要绕过 magic-api 的备份机制
  
  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Reason**: 文件 I/O 操作，模式固定
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/service/JobInfoMagicResourceStorage.java` - 存储实现参考
  - magic-api 核心源码 - 文件监听和刷新机制
  
  **Acceptance Criteria**:
  - [ ] 文件 CRUD 操作正常
  - [ ] 文件修改自动刷新到内存
  - [ ] 备份文件正确生成
  
  **QA Scenarios**:
  
  ```
  Scenario: 流程文件存储验证
    Tool: Bash
    Steps:
      1. 创建流程定义文件
      2. 读取文件内容并解析为 FlowDefinition
      3. 更新文件内容
      4. 删除文件
      5. 验证备份文件存在
    Expected Result: 所有操作成功，数据一致
    Evidence: .sisyphus/evidence/task-4-storage-test.log
  ```
  
  **Commit**: 与 Task 1 合并

---

- [ ] 5. 插件国际化资源配置

  **What to do**:
  - 创建 `src/console/src/i18n/zh-cn.js` 中文国际化文件
  - 创建 `src/console/src/i18n/en.js` 英文国际化文件
  - 定义关键文本：liteflow.name, liteflow.title, liteflow.form.* 等
  - 创建插件图标 `src/console/src/icons/liteflow.svg`
  
  **Must NOT do**:
  - 不要添加复杂的样式
  - 不要遗漏必要的国际化键
  
  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Reason**: 配置文件和 SVG 图标，简单
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/console/src/i18n/zh-cn.js` - 国际化参考
  - `magic-api-plugin-job/src/console/src/icons/job.svg` - 图标参考
  
  **Acceptance Criteria**:
  - [ ] 中英文国际化配置完整
  - [ ] SVG 图标有效
  
  **QA Scenarios**:
  
  ```
  Scenario: 国际化配置验证
    Tool: Bash
    Steps:
      1. 读取 zh-cn.js 和 en.js
      2. 验证包含所有必要键
      3. 格式为标准 JS 模块导出
    Expected Result: 文件格式正确，键完整
    Evidence: .sisyphus/evidence/task-5-i18n-check.log
  ```
  
  **Commit**: 与 Task 1 合并

---

- [ ] 6. LiteFlowComponent 基类 + Groovy 脚本执行器

  **What to do**:
  - 创建 `LiteFlowComponent.java` 抽象基类继承 LiteFlow NodeComponent
  - 实现 `execute()` 方法，动态加载和执行 Groovy 脚本
  - 集成 magic-script 脚本引擎执行 Groovy
  - 创建脚本上下文（包含 flowContext、result 等变量）
  - 实现异常处理和结果返回
  - 支持脚本热加载（文件修改后自动生效）
  
  **Must NOT do**:
  - 不要硬编码组件逻辑
  - 不要绕过 magic-script 的安全检查
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Reason**: 核心执行引擎，需要深入理解 LiteFlow 和 magic-script
  - **Skills**: []
  
  **References**:
  - LiteFlow 官方文档 - NodeComponent 基类和扩展
  - magic-script 文档 - Groovy 脚本执行
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/service/JobMagicDynamicRegistry.java` - 动态注册参考
  
  **Acceptance Criteria**:
  - [ ] 组件基类正确继承 LiteFlow
  - [ ] Groovy 脚本可执行并访问上下文
  - [ ] 异常正确处理
  - [ ] 脚本热加载有效
  
  **QA Scenarios**:
  
  ```
  Scenario: Groovy 组件执行验证
    Tool: Bash
    Steps:
      1. 创建一个简单组件脚本：context.setData("result", "hello")
      2. 启动流程执行
      3. 验证结果数据
    Expected Result: 脚本执行成功，结果正确
    Evidence: .sisyphus/evidence/task-6-component-exec.log
  ```
  
  **Commit**: YES（核心功能）
  - Message: `feat(liteflow): 组件基类和 Groovy 脚本执行器`
  - Files: `LiteFlowComponent.java`

---

- [ ] 7. 流程加载和解析服务

  **What to do**:
  - 创建 `FlowLoaderService.java` 加载和解析服务
  - 从 `data/magic-api/liteflow/flow/` 读取流程定义文件
  - 解析 EL 表达式并注册到 LiteFlow
  - 实现流程动态注册和刷新（文件变更自动更新）
  - 处理流程依赖的组件引用
  
  **Must NOT do**:
  - 不要缓存流程定义（使用动态刷新）
  - 不要手动解析 EL 表达式（使用 LiteFlow 解析器）
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Reason**: 流程加载核心，需要理解 LiteFlow 注册机制
  - **Skills**: []
  
  **References**:
  - LiteFlow 官方文档 - 规则文件加载和刷新
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/starter/MagicAPIJobConfiguration.java` - 配置和刷新参考
  
  **Acceptance Criteria**:
  - [ ] 流程定义正确加载
  - [ ] EL 表达式正确解析
  - [ ] 文件变更自动刷新
  
  **QA Scenarios**:
  
  ```
  Scenario: 流程加载和刷新验证
    Tool: Bash
    Steps:
      1. 创建流程定义文件
      2. 验证流程注册成功
      3. 修改流程定义
      4. 验证刷新生效
    Expected Result: 加载和刷新成功
    Evidence: .sisyphus/evidence/task-7-flow-loader.log
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程加载和解析服务`
  - Files: `FlowLoaderService.java`

---

- [ ] 8. 流程执行引擎（API 触发）

  **What to do**:
  - 创建 `MagicLiteFlowController.java` REST 控制器
  - 实现 `/liteflow/execute/{flowId}` POST 接口
  - 支持传入流程上下文参数（Map<String, Object>）
  - 调用 LiteFlow FlowExecutor 执行流程
  - 返回流程执行结果
  - 实现执行日志记录（开始时间、结束时间、结果、异常）
  
  **Must NOT do**:
  - 不要同步阻塞（支持异步执行）
  - 不要暴露 LiteFlow 内部异常
  
  **Recommended Agent Profile**:
  - **Category**: `deep`
  - **Reason**: 核心执行接口，涉及多层集成
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/web/MagicJobController.java` - 控制器参考
  - LiteFlow 官方文档 - FlowExecutor 使用
  - magic-api 核心源码 - 执行和日志记录
  
  **Acceptance Criteria**:
  - [ ] API 接口可调用
  - [ ] 流程执行成功并返回结果
  - [ ] 执行日志正确记录
  
  **QA Scenarios**:
  
  ```
  Scenario: API 执行流程验证
    Tool: Bash (curl)
    Steps:
      1. POST /liteflow/execute/{flowId} -d '{"input":"test"}'
      2. 验证返回状态码 200
      3. 验证返回结果包含处理数据
      4. 检查执行日志文件
    Expected Result: 流程执行成功，日志正确
    Evidence: .sisyphus/evidence/task-8-api-exec-curl.json
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程执行 API 接口`
  - Files: `MagicLiteFlowController.java`

---

- [ ] 9. magic-api 执行拦截器 + 事件驱动触发

  **What to do**:
  - 创建 `LiteFlowMagicScriptEventListener.java` 监听器
  - 实现 magic-api 脚本执行完成监听接口
  - 配置触发规则（当指定 API 执行完成时触发流程）
  - 从流程定义中读取事件配置
  - 调用流程执行引擎
  - 支持多个 API 触发同一个流程
  - 支持条件触发（只有满足条件才触发）
  
  **Must NOT do**:
  - 不要阻塞 magic-api 执行（使用异步触发）
  - 不要影响 magic-api 正常执行
  
  **Recommended Agent Profile**:
  - **Category**: `deep`
  - **Reason**: 事件驱动架构，需要理解 magic-api 拦截器机制
  - **Skills**: []
  
  **References**:
  - magic-api 源码 - 脚本执行监听器扩展点
  - LiteFlow 官方文档 - 流程触发方式
  - Spring Boot Event - 事件驱动模式
  
  **Acceptance Criteria**:
  - [ ] magic-api 执行可触发流程
  - [ ] 触发配置正确解析
  - [ ] 异步触发不阻塞主流程
  - [ ] 条件触发逻辑正确
  
  **QA Scenarios**:
  
  ```
  Scenario: 事件驱动触发验证
    Tool: Bash (curl)
    Steps:
      1. 魔法 API 配置事件触发：execute API 后触发 liteflow-1
      2. 调用 magic API: POST /magic/api/test-api
      3. 验证 liteflow-1 流程被执行
      4. 检查事件触发日志
    Expected Result: 事件触发成功，流程异步执行
    Evidence: .sisyphus/evidence/task-9-event-trigger.log
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): magic-api 执行拦截器和事件驱动触发`
  - Files: `LiteFlowMagicScriptEventListener.java`

---

- [ ] 10. 流程执行日志记录服务

  **What to do**:
  - 创建 `FlowExecutionLog.java` 日志模型（flowId、executionId、startTime、endTime、status、result、errorMsg）
  - 创建 `FlowExecutionLogService.java` 日志服务
  - 实现日志写入（文件存储：`data/magic-api/liteflow/logs/`）
  - 实现日志查询（按流程、时间、状态筛选）
  - 实现日志清理（保留最近 N 天）
  - 添加执行追踪 ID（用于追踪完整流程）
  
  **Must NOT do**:
  - 不要使用数据库存储日志
  - 不要无限期保留日志（实现清理策略）
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Reason**: 日志服务，需要设计存储格式和查询接口
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/service/JobInfoMagicResourceStorage.java` - 文件存储参考
  - magic-api 源码 - 操作日志实现
  
  **Acceptance Criteria**:
  - [ ] 执行日志正确记录
  - [ ] 日志查询功能正常
  - [ ] 过期日志自动清理
  
  **QA Scenarios**:
  
  ```
  Scenario: 执行日志记录验证
    Tool: Bash
    Steps:
      1. 执行流程 3 次（成功 2 次，失败 1 次）
      2. 查询日志文件
      3. 验证日志包含所有执行记录
      4. 验证日志格式正确
    Expected Result: 日志完整，格式正确
    Evidence: .sisyphus/evidence/task-10-exec-logs.json
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程执行日志记录服务`
  - Files: `FlowExecutionLog.java`, `FlowExecutionLogService.java`
  - Parent: Task 8（日志服务依赖执行接口）

---

- [ ] 11. 插件前端入口 + 路由注册

  **What to do**:
  - 创建 `src/console/package.json` 配置
  - 创建 `src/console/vite.config.js` 构建配置
  - 创建 `src/console/src/index.js` 插件入口
  - 注册左侧资源列表（type: 'liteflow'）
  - 注册底部工具条组件
  - 配置路由和 i18n
  
  **Must NOT do**:
  - 不要使用复杂的 UI 框架（复用 magic 组件）
  - 不要遗漏资源注册
  
  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Reason**: Vue 3 前端配置和组件注册
  - **Skills**: ['vue', 'vite']
  
  **References**:
  - `magic-api-plugin-job/src/console/src/index.js` - 插件入口参考
  - `magic-api-plugin-job/src/console/package.json` - 依赖配置参考
  
  **Acceptance Criteria**:
  - [ ] 插件前端构建成功
  - [ ] 左侧资源列表显示 liteflow
  - [ ] 组件正确注册
  
  **QA Scenarios**:
  
  ```
  Scenario: 前端插件加载验证
    Tool: Playwright
    Steps:
      1. 打开 magic-api Web UI: http://localhost:8081/magic/web
      2. 验证左侧资源列表显示 "LiteFlow"
      3. 点击 LiteFlow 图标
      4. 验证显示流程列表页面
    Expected Result: 插件正确加载，UI 显示正常
    Evidence: .sisyphus/evidence/task-11-plugin-load.png
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 前端插件入口和路由注册`
  - Files: `src/console/**/*`

---

- [ ] 12. 流程列表/创建/删除组件

  **What to do**:
  - 创建 `FlowList.vue` 列表组件（显示所有流程定义）
  - 实现流程搜索和筛选
  - 实现创建流程按钮和弹窗
  - 实现删除流程功能（带确认）
  - 实现流程启用/禁用切换
  - 实现流程导入/导出功能
  
  **Must NOT do**:
  - 不要自定义复杂样式（使用 magic-ui 组件）
  - 不要省略确认步骤（删除需二次确认）
  
  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Reason**: Vue 3 组件开发，列表和表单交互
  - **Skills**: ['vue', 'vueuse-functions']
  
  **References**:
  - `magic-api-plugin-job/src/console/src/components/magic-job-info.vue` - 组件参考
  - magic-editor 源码 - magic-ui 组件使用
  
  **Acceptance Criteria**:
  - [ ] 流程列表正确显示
  - [ ] 创建流程功能正常
  - [ ] 删除流程有确认
  - [ ] 启用/禁用切换生效
  
  **QA Scenarios**:
  
  ```
  Scenario: 流程列表操作验证
    Tool: Playwright
    Steps:
      1. 打开流程列表页面
      2. 点击"新建流程"
      3. 填写表单并提交
      4. 验证列表显示新流程
      5. 点击删除按钮并确认
      6. 验证流程已删除
    Expected Result: 所有操作成功
    Evidence: .sisyphus/evidence/task-12-crud-operations.png
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程列表和 CRUD 组件`
  - Files: `FlowList.vue`, `FlowCreateModal.vue`
  - Parent: Task 11

---

- [ ] 13. 流程编辑器组件（EL 表达式 + Groovy 脚本）

  **What to do**:
  - 创建 `FlowEditor.vue` 编辑器组件
  - 集成代码编辑器（复用 magic-api 编辑器）
  - 上半部分：EL 表达式编辑（如：THEN a, b, c WHEN d THEN e）
  - 下半部分：组件列表和 Groovy 脚本编辑
  - 实现语法高亮（EL 和 Groovy）
  - 实现保存功能
  - 实现组件引用验证
  
  **Must NOT do**:
  - 不要重复开发编辑器（复用 magic 编辑器）
  - 不要省略语法验证
  
  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Reason**: 复杂前端编辑器，代码编辑和验证
  - **Skills**: ['vue', 'vueuse-functions']
  
  **References**:
  - magic-api 编辑器源码 - 代码编辑器组件
  - `magic-api-plugin-job/src/console/src/components/magic-job-info.vue` - 编辑器结构参考
  
  **Acceptance Criteria**:
  - [ ] EL 表达式编辑器正常
  - [ ] Groovy 脚本编辑器正常
  - [ ] 语法高亮和验证有效
  - [ ] 保存功能正常
  
  **QA Scenarios**:
  
  ```
  Scenario: 流程编辑器功能验证
    Tool: Playwright
    Steps:
      1. 打开一个流程的编辑页面
      2. 编辑 EL 表达式：THEN a, b
      3. 添加组件 a，编写 Groovy 脚本
      4. 点击保存
      5. 验证保存成功
      6. 刷新页面，验证内容恢复
    Expected Result: 编辑器功能完整，保存成功
    Evidence: .sisyphus/evidence/task-13-editor-save.png
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程编辑器组件（EL+Groovy）`
  - Files: `FlowEditor.vue`
  - Parent: Task 6, 11, 12

---

- [ ] 14. REST API 接口实现

  **What to do**:
  - 在 MagicLiteFlowController 中实现 REST API：
    - GET /liteflow/flow (列表)
    - POST /liteflow/flow (创建)
    - PUT /liteflow/flow/{id} (更新)
    - DELETE /liteflow/flow/{id} (删除)
    - GET /liteflow/flow/{id} (详情)
    - POST /liteflow/flow/{id}/enable (启用)
    - POST /liteflow/flow/{id}/disable (禁用)
    - GET /liteflow/logs (执行日志列表)
    - GET /liteflow/logs/{executionId} (执行详情)
    - GET /liteflow/statistics (统计信息)
  - 实现请求参数校验
  - 实现统一异常处理
  
  **Must NOT do**:
  - 不要省略参数验证
  - 不要暴露内部异常信息
  
  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Reason**: REST API CRUD，模式化开发
  - **Skills**: []
  
  **References**:
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/web/MagicJobController.java` - 控制器参考
  - magic-api 核心源码 - REST API 设计模式
  
  **Acceptance Criteria**:
  - [ ] 所有 REST API 接口可调用
  - [ ] 参数校验正确
  - [ ] 异常处理统一
  
  **QA Scenarios**:
  
  ```
  Scenario: REST API 接口验证
    Tool: Bash (curl)
    Steps:
      1. GET /liteflow/flow - 获取列表
      2. POST /liteflow/flow - 创建流程
      3. GET /liteflow/flow/{id} - 获取详情
      4. PUT /liteflow/flow/{id} - 更新
      5. DELETE /liteflow/flow/{id} - 删除
      6. 验证每个接口响应正确
    Expected Result: 所有接口响应正常
    Evidence: .sisyphus/evidence/task-14-rest-api.json
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): REST API 接口实现`
  - Files: `MagicLiteFlowController.java`
  - Parent: Task 7, 8

---

- [ ] 15. 流程验证（语法检查、组件引用验证）

  **What to do**:
  - 创建 `FlowValidator.java` 验证器
  - 实现 EL 表达式语法验证（调用 LiteFlow 验证 API）
  - 实现组件引用验证（检查组件是否存在）
  - 实现循环依赖检测
  - 在保存前自动调用验证
  - 返回详细的错误信息
  
  **Must NOT do**:
  - 不要跳过验证直接保存
  - 不要显示模糊的错误信息
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Reason**: 验证逻辑，需要理解 LiteFlow 语法规则
  - **Skills**: []
  
  **References**:
  - LiteFlow 官方文档 - 规则验证 API
  - magic-api 源码 - 脚本验证实现
  
  **Acceptance Criteria**:
  - [ ] EL 表达式语法验证正确
  - [ ] 组件引用验证有效
  - [ ] 循环依赖检测准确
  
  **QA Scenarios**:
  
  ```
  Scenario: 流程验证功能测试
    Tool: Bash (curl)
    Steps:
      1. 提交一个无效的 EL 表达式
      2. 验证返回错误信息
      3. 提交一个引用不存在组件的流程
      4. 验证返回组件不存在错误
      5. 提交一个循环依赖的流程
      6. 验证返回循环依赖错误
    Expected Result: 所有验证正确触发
    Evidence: .sisyphus/evidence/task-15-validation.json
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程验证器`
  - Files: `FlowValidator.java`
  - Parent: Task 14

---

- [ ] 16. 流程执行追踪组件

  **What to do**:
  - 创建 `FlowExecutionTrace.vue` 追踪组件
  - 显示流程执行的详细步骤
  - 展示每个组件的执行状态（成功/失败）
  - 显示组件执行耗时
  - 显示组件输入输出数据
  - 支持按 executionId 查询历史执行
  
  **Must NOT do**:
  - 不要省略关键执行信息
  - 不要使用复杂图表（简单列表即可）
  
  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Reason**: 前端展示组件，数据可视化
  - **Skills**: ['vue']
  
  **References**:
  - magic-api 源码 - 调试和日志展示组件
  - `magic-api-plugin-job/src/console/src/components/magic-job-info.vue` - 信息展示参考
  
  **Acceptance Criteria**:
  - [ ] 执行追踪数据正确显示
  - [ ] 支持历史查询
  - [ ] 组件状态可视化
  
  **QA Scenarios**:
  
  ```
  Scenario: 执行追踪功能验证
    Tool: Playwright
    Steps:
      1. 执行一个流程（包含多个组件）
      2. 打开执行追踪页面
      3. 选择刚执行的记录
      4. 验证显示每个组件的执行状态
      5. 验证显示执行耗时和输入输出
    Expected Result: 追踪信息完整准确
    Evidence: .sisyphus/evidence/task-16-exec-trace.png
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程执行追踪组件`
  - Files: `FlowExecutionTrace.vue`
  - Parent: Task 10, 13, 14

---

- [ ] 17. 统计仪表盘组件

  **What to do**:
  - 创建 `FlowStatistics.vue` 统计组件
  - 显示流程调用次数（按天/周/月）
  - 显示成功率统计
  - 显示平均耗时统计
  - 显示 Top N 耗时流程
  - 显示 Top N 失败流程
  - 使用时间范围筛选
  
  **Must NOT do**:
  - 不要过度设计图表（简单统计即可）
  - 不要实时刷新（手动刷新即可）
  
  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Reason**: 数据可视化组件
  - **Skills**: ['vue']
  
  **References**:
  - magic-api 源码 - 统计图表组件
  - Chart.js / ECharts - 图表库（如果 magic-editor 已有集成）
  
  **Acceptance Criteria**:
  - [ ] 统计数据计算正确
  - [ ] 按时间筛选生效
  - [ ] 图表显示正常
  
  **QA Scenarios**:
  
  ```
  Scenario: 统计仪表板验证
    Tool: Playwright + Bash
    Steps:
      1. 执行流程 10 次（模拟不同结果）
      2. 打开统计页面
      3. 验证显示调用次数
      4. 验证成功率和平均耗时
      5. 切换时间范围，验证数据更新
    Expected Result: 统计数据准确
    Evidence: .sisyphus/evidence/task-17-statistics.png
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 统计仪表盘组件`
  - Files: `FlowStatistics.vue`, `FlowStatisticsService.java`
  - Parent: Task 10, 14

---

- [ ] 18. 流程版本管理功能

  **What to do**:
  - 创建 `FlowVersion.java` 版本模型（version、content、createTime、createBy、releaseStatus）
  - 在 FlowStorageService 中添加版本管理方法
  - 实现保存时自动创建新版本
  - 实现版本列表查询
  - 实现版本回滚（恢复到历史版本）
  - 实现版本对比（显示差异）
  - 实现版本发布/下线
  
  **Must NOT do**:
  - 不要使用 Git（使用文件版本存储）
  - 不要存储全量历史（保留最近 N 个版本）
  
  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Reason**: 版本管理，需要设计存储和对比逻辑
  - **Skills**: []
  
  **References**:
  - magic-api 源码 - 备份和版本管理实现
  - `magic-api-plugin-job/src/main/java/org/ssssssss/magicapi/job/service/JobInfoMagicResourceStorage.java` - 文件存储参考
  
  **Acceptance Criteria**:
  - [ ] 版本自动创建
  - [ ] 版本列表查询正常
  - [ ] 版本回滚功能生效
  - [ ] 版本对比显示差异
  
  **QA Scenarios**:
  
  ```
  Scenario: 版本管理功能验证
    Tool: Bash + Playwright
    Steps:
      1. 创建流程并保存 3 次（生成 3 个版本）
      2. 查询版本列表
      3. 选择版本 1 回滚
      4. 验证流程内容回滚到版本 1
      5. 查看版本对比
    Expected Result: 版本管理功能完整
    Evidence: .sisyphus/evidence/task-18-version-manage.json
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 流程版本管理功能`
  - Files: `FlowVersion.java`, `FlowVersionService.java`
  - Parent: Task 7, 14

---

- [ ] 19. 集成测试 + 文档

  **What to do**:
  - 编写集成测试类 LiteFlowPluginIntegrationTest
    - 测试流程创建、编辑、删除
    - 测试流程执行（API 触发）
    - 测试事件驱动触发
    - 测试执行日志记录
    - 测试版本管理
  - 编写 README.md 文档
    - 插件安装说明
    - 快速开始指南
    - API 接口文档
    - 配置说明
    - 常见问题
  
  **Must NOT do**:
  - 不要省略关键测试场景
  - 不要编写模糊的文档
  
  **Recommended Agent Profile**:
  - **Category**: `deep`
  - **Reason**: 集成测试和文档，需要全面理解插件功能
  - **Skills**: []
  
  **References**:
  - magic-boot 现有插件的测试代码（如果有）
  - magic-api 文档结构
  
  **Acceptance Criteria**:
  - [ ] 集成测试全部通过
  - [ ] README 文档完整
  - [ ] 示例代码可运行
  
  **QA Scenarios**:
  
  ```
  Scenario: 集成测试执行
    Tool: Bash
    Steps:
      1. 运行 mvn test -pl magic-api-plugin-liteflow
      2. 验证所有测试用例通过
      3. 检查测试覆盖率
    Expected Result: 所有测试通过
    Evidence: .sisyphus/evidence/task-19-integration-test.log
  ```
  
  **Commit**: YES
  - Message: `feat(liteflow): 集成测试和文档`
  - Files: `LiteFlowPluginIntegrationTest.java`, `README.md`
  - Parent: Task 15, 16, 17, 18

---

## Final Verification Wave (MANDATORY)

> ⚠️ **4 review agents run in PARALLEL. ALL must APPROVE. Rejection → fix → re-run.**

- [ ] F1. **Plan Compliance Audit** — `oracle`
  - Read the plan end-to-end
  - Verify all "Must Have" implemented
  - Verify all "Must NOT Have" absent
  - Check evidence files in `.sisyphus/evidence/`
  - Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  - Run `mvn clean compile -Pprod`
  - Check for code quality issues
  - Verify no hardcoded values, proper error handling
  - Check AI slop patterns
  - Output: `Build [PASS/FAIL] | Code Quality [PASS/FAIL] | VERDICT`

- [ ] F3. **Real Manual QA** — `unspecified-high` (+ `playwright` if UI)
  - Execute ALL QA scenarios from ALL tasks
  - Test cross-task integration
  - Save to `.sisyphus/evidence/final-qa/`
  - Output: `Scenarios [N/N pass] | Integration [N/N] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  - For each task: verify 1:1 implementation
  - Detect scope creep or missing features
  - Flag unaccounted changes
  - Output: `Tasks [N/N compliant] | Scope [N/N] | VERDICT`

---

## Commit Strategy

- **Wave 1**: `feat(liteflow): 插件模块脚手架和基础配置` — Tasks 1-5 combined
- **Wave 2**: `feat(liteflow): 核心引擎和执行服务` — Tasks 6-10 (2 commits)
- **Wave 3**: `feat(liteflow): 前端编辑器和 API` — Tasks 11-15 (2 commits)
- **Wave 4**: `feat(liteflow): 监控和版本管理` — Tasks 16-18 (1 commit)
- **Wave 5**: `feat(liteflow): 集成测试和文档` — Task 19 (1 commit)

---

## Success Criteria

### Verification Commands
```bash
# Build verification
mvn clean package -Pprod -DskipTests -pl magic-api-plugins/magic-api-plugin-liteflow

# Integration test
mvn test -pl magic-api-plugins/magic-api-plugin-liteflow

# Start application
mvn spring-boot:run -pl magic-boot-master

# Test API endpoint
curl -X POST http://localhost:8081/liteflow/execute/{flowId} -H "Content-Type: application/json" -d '{"input":"test"}'
```

### Final Checklist
- [ ] All "Must Have" features implemented
- [ ] All "Must NOT Have" guardrails respected
- [ ] All integration tests pass
- [ ] All QA scenarios verified with evidence
- [ ] Plugin auto-registers on startup
- [ ] Web UI shows LiteFlow editor
- [ ] API trigger works correctly
- [ ] Event trigger works correctly
- [ ] Execution logs recorded
- [ ] Statistics dashboard displays data
- [ ] Version management functional
- [ ] README documentation complete
