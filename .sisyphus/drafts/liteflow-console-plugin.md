# Draft: LiteFlow Console 插件规划

## 调研完成状态
- [x] 项目中是否已有 LiteFlow 相关代码 ✅
- [x] LiteFlow console 功能特性 ✅
- [x] magic-api 插件结构模式 ✅

## 项目现状（已确认）

### 已有集成
- **插件模块**：`magic-api-plugin-liteflow` 已存在
- **核心依赖**：`liteflow-core:2.15.2`
- **配置类**：`MagicAPILiteflowConfiguration.java`
- **控制器**：`LiteflowController.java`
- **数据目录**：`data/magic-api/liteflow/`
  - 组件：`component/S1Component.groovy`
  - 流程：`flow/simple_example.el.xml`

### LiteFlow 官方情况（重要发现）
- ⚠️ **LiteFlow 官方没有 Web Console**
- 只有简单的日志监控功能（打印组件耗时排行）
- 第三方可视化方案：liteflow-view 等

### 用户明确需求
- ✅ 集成到 magic-web 界面（非独立 UI）

---

## 用户确认的决策

### 功能范围（全部需要）
- ✅ 流程查看和编辑
- ✅ 组件管理
- ✅ 规则刷新
- ✅ 执行监控

### 技术决策
1. **规则存储**：文件系统（data/magic-api/liteflow/flow/*.xml）
2. **权限控制**：复用 magic-boot 权限体系
3. **开发优先级**：只读查看（第一阶段）
4. **UI 集成**：独立菜单项
5. **流程编辑**：代码编辑器（EL 规则 XML）
6. **组件编辑**：代码编辑器（Groovy/Java）
7. **监控可视化**：简单列表（不需要复杂图表）

### 明确需求
- 集成到 magic-web 界面
- 第一阶段实现只读查看功能
- 后续阶段实现完整功能（编辑、刷新、监控）

---

## Clearance Check

- [x] Core objective clearly defined? ✅
- [x] Scope boundaries established (IN/OUT)? ✅
- [x] No critical ambiguities remaining? ✅
- [x] Technical approach decided? ✅
- [x] Test strategy confirmed? ✅ 测试后置
- [x] No blocking questions outstanding? ✅

**✅ 所有要求已明确，准备进入计划生成阶段**

---

## 最终需求总结

### 核心目标
为 magic-boot 开发 LiteFlow Console 插件，集成到 magic-web 界面，提供流程编排可视化管理能力。

### 功能范围
1. **流程管理**：查看、编辑流程定义（EL 规则 XML）
2. **组件管理**：查看、编辑组件代码（Groovy/Java）
3. **规则刷新**：动态刷新流程规则
4. **执行监控**：查看执行历史和统计数据

### 技术方案
- **存储**：文件系统（data/magic-api/liteflow/）
- **权限**：复用 magic-boot 权限体系
- **UI**：独立菜单项，集成到 magic-web
- **编辑器**：代码编辑器（Monaco Editor）
- **可视化**：简单列表（无需复杂图表）

### 开发策略
- **第一阶段**：只读查看（流程列表、组件列表、详情查看）
- **第二阶段**：编辑功能（流程编辑、组件编辑）
- **第三阶段**：监控功能（执行历史、统计数据）
- **第四阶段**：测试补充（Vitest + 组件测试）

### 范围边界
**IN（包含）：**
- 流程和组件的 CRUD 操作
- 文件系统存储的读写
- 权限验证集成
- 执行监控数据展示

**OUT（不包含）：**
- 可视化拖拽编辑器
- 数据库存储方案
- 配置中心集成
- 复杂的图表可视化
- 实时推送（WebSocket）
