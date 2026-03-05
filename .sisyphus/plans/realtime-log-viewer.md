# 实时日志查看功能开发计划

## TL;DR

> **Quick Summary**: 开发一个基于 WebSocket 的实时日志查看功能，支持多种日志类型切换、关键词过滤、日志级别筛选等高级功能，采用终端风格 UI，默认显示最近 100 行日志记录。
> 
> **Deliverables**: 
> - WebSocket 连接管理 Composable (自动重连、心跳检测)
> - 日志终端组件 (虚拟列表、日志着色)
> - 工具栏组件 (过滤、搜索、导出)
> - 实时日志查看页面
> - 路由和菜单配置
> 
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: WebSocket Composable → LogTerminal 组件 → RealtimeLog 页面 → 路由配置

---

## Context

### Original Request
开发一个实时显示 Spring Boot 后端日志的功能，使用 WebSocket 通信，支持类似 tail 命令，可以显示最近的多少行记录。

### Interview Summary
**Key Discussions**:
- **开发范围**: 仅前端部分（后端 WebSocket 端点由其他人开发）
- **功能级别**: 高级功能（关键词过滤、日志级别筛选、时间范围查询、搜索高亮）
- **默认行数**: 100 行
- **菜单位置**: 系统监控菜单下
- **WebSocket 协议**: 原生 WebSocket（不是 STOMP）
- **认证方式**: URL 参数传递 token（如 ws://host/ws?token=xxx）
- **日志类型**: 支持多种日志类型切换（应用日志、系统日志、错误日志、访问日志）
- **UI 风格**: 终端风格（黑色背景、等宽字体）
- **测试策略**: 测试后置（先实现功能，后补充测试用例）

**Research Findings**:
- **项目现状**: 项目中未找到 WebSocket 相关代码，需要从零开始实现
- **现有组件**: 有 mb-table、mb-search 等数据展示组件，但需要定制化的日志终端组件
- **权限控制**: 使用 v-permission 指令，权限码格式为 system:monitor:xxx
- **技术栈**: Vue 3 + Naive UI + Vite，- **最佳实践**: 使用 Composables 管理 WebSocket 连接，使用虚拟列表处理大量日志，实现自动重连和心跳检测

### Metis Review
**Identified Gaps** (addressed):
- **后端接口细节**: 遇 Metis 超时，但根据用户确认，后端由其他人开发，前端只需按照约定的接口实现
- **日志消息格式**: 假设为 JSON 格式，包含 level、message、timestamp 字段
- **WebSocket 端点**: 假设为 ws://host/ws/logs?token=xxx
- **错误处理**: 需要处理连接失败、消息解析失败等异常情况

---

## Work Objectives

### Core Objective
开发一个完整的实时日志查看功能，包含 WebSocket 连接管理、日志显示、高级过滤和搜索功能。

### Concrete Deliverables
- `src/composables/useWebSocket.js` - WebSocket 连接管理 Composable
- `src/components/log-viewer/LogTerminal.vue` - 日志终端组件
- `src/components/log-viewer/LogToolbar.vue` - 工具栏组件
- `src/views/system/monitor/realtime-log.vue` - 实时日志查看页面
- 路由和菜单配置更新

### Definition of Done
- [x] WebSocket 连接可以正常建立、断开、重连
- [x] 日志实时显示，支持自动滚动
- [x] 关键词过滤功能正常工作
- [x] 日志级别筛选功能正常工作
- [x] 可以切换不同日志类型
- [x] 可以导出日志
- [x] 连接状态正确显示
- [x] 页面已添加到系统监控菜单

### Must Have
- WebSocket 连接管理（自动重连、心跳检测）
- 日志实时显示（虚拟列表）
- 关键词过滤
- 日志级别筛选
- 日志类型切换
- 日志导出

### Must NOT Have (Guardrails)
- ❌ 不要实现后端 WebSocket 端点（仅前端）
- ❌ 不要使用 STOMP 协议（使用原生 WebSocket）
- ❌ 不要使用表格风格 UI（使用终端风格）
- ❌ 不要在连接失败时自动刷新页面
- ❌ 不要在内存中保存超过 10000 条日志

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.
> Acceptance criteria requiring "user manually tests/confirms" are FORBIDDEN.

### Test Decision
- **Infrastructure exists**: NO (项目无测试框架)
- **Automated tests**: Tests-after (先实现功能，后补充测试)
- **Framework**: None (暂不设置测试框架)
- **Agent-Executed QA**: ALWAYS (mandatory for all tasks)

### QA Policy
Every task MUST include agent-executed QA scenarios.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Frontend/UI**: Use Playwright (playwright skill) — Navigate, interact, assert DOM, screenshot
- **WebSocket**: Use Bash (curl/wscat) — Test connection, send/receive messages
- **Component**: Use Bash (npm run dev) — Start dev server, open in browser, verify functionality

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — foundation):
├── Task 1: WebSocket Composable [quick]
├── Task 2: LogTerminal 组件基础结构 [visual-engineering]
└── Task 3: LogToolbar 组件 [quick]

Wave 2 (After Wave 1 — integration):
├── Task 4: RealtimeLog 页面集成 [visual-engineering]
└── Task 5: 路由和菜单配置 [quick]

Wave FINAL (After ALL tasks — verification):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Real manual QA (unspecified-high)
└── Task F4: Scope fidelity check (deep)

Critical Path: Task 1 → Task 2 → Task 4 → Task 5 → F1-F4
Parallel Speedup: ~40% faster than sequential
Max Concurrent: 3 (Wave 1)
```

### Dependency Matrix

- **1**: — — 4
- **2**: — — 4
- **3**: — — 4
- **4**: 1, 2, 3 — F1-F4
- **5**: 4 — F1-F4

### Agent Dispatch Summary

- **1**: **1** — T1 → `quick`
- **2**: **1** — T2 → `visual-engineering`
- **3**: **1** — T3 → `quick`
- **3**: **2** — T4 → `visual-engineering`, T5 → `quick`
- **FINAL**: **4** — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

> Implementation + Test = ONE Task. Never separate.
> EVERY task MUST have: Recommended Agent Profile + Parallelization info + QA Scenarios.
> **A task WITHOUT QA Scenarios is INCOMPLETE. No exceptions.**

- [x] 1. WebSocket 连接管理 Composable

  **What to do**:
  - 创建 `src/composables/useWebSocket.js` 文件
  - 实现 WebSocket 连接管理逻辑：
    - 连接建立（支持 URL 参数传递 token）
    - 自动重连（指数退避策略，最大重连次数 10 次）
    - 心跳检测（30 秒间隔，10 秒超时）
    - 连接状态管理（CONNECTING/OPEN/CLOSING/CLOSED）
    - 消息队列（离线时缓存消息）
  - 导出 composable 函数：useWebSocket(url, options)

  **Must NOT do**:
  - 不要实现 STOMP 协议（使用原生 WebSocket）
  - 不要硬编码 WebSocket URL（从参数获取）
  - 不要在组件卸载时自动重连

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 单文件逻辑，标准 WebSocket API，无需复杂设计
  - **Skills**: []
    - 无特殊技能需求

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Task 4 (RealtimeLog 页面需要使用此 composable)
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `src/scripts/request.js:4-7` - Axios 实例创建和配置模式（baseURL、timeout）
  - `src/store/modules/userStore.js:42-47` - Token 获取和设置模式（从 localStorage 读取）

  **API/Type References** (contracts to implement against):
  - 无现有 WebSocket 相关 API，需要新建

  **External References** (libraries and frameworks):
  - MDN WebSocket API: https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API
  - Vue 3 Composition API: https://vuejs.org/guide/extras/composition-api-faq.html

  **WHY Each Reference Matters**:
  - `request.js`: 学习如何封装网络请求和错误处理
  - `userStore.js`: 了解如何获取 token 用于 WebSocket 认证

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: WebSocket 连接建立成功
    Tool: Bash (Node.js REPL)
    Preconditions: 
      1. 后端 WebSocket 服务已启动
      2. 有效的 token 可用
    Steps:
      1. 在 Node.js REPL 中导入 composable: const { connect, isConnected } = require('./src/composables/useWebSocket.js')
      2. 调用 connect()，传入 ws://localhost:8080/ws/logs?token=test-token
      3. 等待 2 秒
      4. 检查 isConnected.value 是否为 true
    Expected Result: isConnected.value === true
    Failure Indicators: 连接超时、连接状态始终为 CONNECTING
    Evidence: .sisyphus/evidence/task-1-websocket-connect-success.txt

  Scenario: WebSocket 自动重连
    Tool: Bash (Node.js REPL)
    Preconditions:
      1. WebSocket 连接已建立
      2. 后端服务可重启
    Steps:
      1. 建立 WebSocket 连接
      2. 停止后端服务，模拟连接断开
      3. 检查连接状态变为 CLOSED
      4. 重启后端服务
      5. 等待 5 秒
      6. 检查 isConnected.value 是否恢复为 true
    Expected Result: 连接自动重连成功，isConnected.value === true
    Failure Indicators: 连接未重连、超过最大重连次数
    Evidence: .sisyphus/evidence/task-1-websocket-reconnect.txt

  Scenario: WebSocket 心跳检测
    Tool: Bash (Node.js REPL)
    Preconditions:
      1. WebSocket 连接已建立
    Steps:
      1. 建立 WebSocket 连接
      2. 等待 35 秒（超过心跳间隔）
      3. 检查是否发送了 ping 消息
      4. 模拟 pong 响应
      5. 检查连接保持 OPEN 状态
    Expected Result: 心跳消息正常发送和接收，连接保持稳定
    Failure Indicators: 心跳超时、连接断开
    Evidence: .sisyphus/evidence/task-1-websocket-heartbeat.txt
  ```

  **Evidence to Capture**:
  - [ ] REPL 交互日志
  - [ ] 连接状态变化记录

  **Commit**: YES
  - Message: `feat(websocket): add WebSocket composable with reconnection and heartbeat`
  - Files: `src/composables/useWebSocket.js`
  - Pre-commit: None

---

- [x] 2. LogTerminal 组件基础结构

  **What to do**:
  - 创建 `src/components/log-viewer/LogTerminal.vue` 文件
  - 实现日志终端组件：
    - 使用 n-virtual-list 实现虚拟滚动（处理大量日志）
    - 日志行渲染（时间戳、级别、消息）
    - 日志级别着色（ERROR 红色、WARN 黄色、INFO 默认、DEBUG 灰色）
    - 自动滚动到底部（可配置）
    - 最大日志数量限制（10000 条）
  - Props 定义：
    - maxLogs: Number (default: 10000)
    - autoScroll: Boolean (default: true)
  - Methods 暴露：
    - addLog(log): 添加日志
    - clearLogs(): 清空日志
    - scrollToBottom(): 滚动到底部

  **Must NOT do**:
  - 不要使用真实 DOM 渲染所有日志（使用虚拟列表）
  - 不要在组件内部管理 WebSocket 连接（由父组件管理）
  - 不要实现过滤逻辑（由 LogToolbar 处理）

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: UI 组件开发，需要关注视觉设计和用户体验
  - **Skills**: [`vue`, `naive-ui`]
    - `vue`: Vue 3 Composition API 和组件开发
    - `naive-ui`: Naive UI 组件库使用（n-virtual-list、n-tag 等）

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Task 4 (RealtimeLog 页面需要使用此组件)
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `src/views/system/monitor/oper-log.vue:1-17` - 页面结构和布局模式
  - `src/components/magic/data/mb-table.vue:5-25` - 数据展示组件结构（使用 n-data-table）

  **API/Type References** (contracts to implement against):
  - Naive UI NVirtualList: https://www.naiveui.com/zh-CN/os-theme/components/virtual-list

  **Test References** (testing patterns to follow):
  - 无现有测试文件

  **External References** (libraries and frameworks):
  - Naive UI Virtual List: https://www.naiveui.com/zh-CN/os-theme/components/virtual-list
  - Vue 3 Script Setup: https://vuejs.org/api/sfc-script-setup.html

  **WHY Each Reference Matters**:
  - `oper-log.vue`: 学习监控页面的布局和组件使用模式
  - `mb-table.vue`: 了解如何使用 Naive UI 数据展示组件
  - Naive UI Virtual List: 掌握虚拟列表的正确用法

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: 日志正常显示
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 开发服务器已启动 (npm run dev)
      2. 浏览器可访问 http://localhost:5173
    Steps:
      1. 启动 Playwright
      2. 创建测试页面，导入 LogTerminal 组件
      3. 调用 addLog() 添加 10 条不同级别的日志
      4. 检查日志是否正确显示
      5. 验证日志级别着色（ERROR 红色、WARN 黄色等）
    Expected Result: 
      - 日志正确显示在虚拟列表中
      - 日志级别颜色正确
      - 时间戳格式正确
    Failure Indicators: 日志不显示、颜色错误、虚拟列表报错
    Evidence: .sisyphus/evidence/task-2-log-display.png

  Scenario: 自动滚动到底部
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. LogTerminal 组件已渲染
      2. autoScroll = true
    Steps:
      1. 添加 100 条日志
      2. 检查滚动位置是否在底部
      3. 手动滚动到顶部
      4. 再添加 10 条日志
      5. 检查是否自动滚动回底部
    Expected Result: 自动滚动功能正常工作
    Failure Indicators: 不自动滚动、滚动位置错误
    Evidence: .sisyphus/evidence/task-2-auto-scroll.png

  Scenario: 日志数量限制
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. LogTerminal 组件已渲染
      2. maxLogs = 100
    Steps:
      1. 添加 150 条日志
      2. 检查日志数量是否为 100
      3. 检查是否移除了最早的日志
    Expected Result: 日志数量不超过 maxLogs，旧日志被移除
    Failure Indicators: 日志数量超过限制、未移除旧日志
    Evidence: .sisyphus/evidence/task-2-log-limit.png
  ```

  **Evidence to Capture**:
  - [ ] 浏览器截图
  - [ ] 控制台日志

  **Commit**: YES
  - Message: `feat(log-viewer): add LogTerminal component with virtual list`
  - Files: `src/components/log-viewer/LogTerminal.vue`
  - Pre-commit: None

---

- [x] 3. LogToolbar 组件

  **What to do**:
  - 创建 `src/components/log-viewer/LogToolbar.vue` 文件
  - 实现工具栏组件：
    - 日志类型选择（应用日志、系统日志、错误日志、访问日志）
    - 日志级别筛选（DEBUG、INFO、WARN、ERROR，多选）
    - 关键词搜索输入框（实时过滤）
    - 清空日志按钮
    - 导出日志按钮
    - 自动滚动切换按钮
    - 连接状态指示器
  - Props 定义：
    - logTypes: Array (日志类型列表)
    - selectedLogType: String (当前选中的日志类型)
    - selectedLevels: Array (当前选中的日志级别)
    - filterText: String (过滤关键词)
    - autoScroll: Boolean (是否自动滚动)
    - isConnected: Boolean (是否已连接)
    - logCount: Number (当前日志数量)
  - Emits 定义：
    - @update:logType(type)
    - @update:levels(levels)
    - @update:filter(text)
    - @clear
    - @export
    - @toggle:autoScroll

  **Must NOT do**:
  - 不要在工具栏中实现实际的过滤逻辑（只发出事件）
  - 不要直接操作日志数据（由父组件管理）

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 简单的 UI 组件，主要是 Naive UI 组件组合
  - **Skills**: [`vue`, `naive-ui`]
    - `vue`: Vue 3 组件开发
    - `naive-ui`: Naive UI 组件使用（n-select、n-input、n-button 等）

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: Task 4 (RealtimeLog 页面需要使用此组件)
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `src/views/system/monitor/oper-log.vue:4-11` - 工具栏和搜索区域布局
  - `src/components/magic/data/mb-search.vue` - 搜索组件结构

  **API/Type References** (contracts to implement against):
  - Naive UI Components: https://www.naiveui.com/zh-CN/os-theme/components

  **External References** (libraries and frameworks):
  - Naive UI Select: https://www.naiveui.com/zh-CN/os-theme/components/select
  - Naive UI Input: https://www.naiveui.com/zh-CN/os-theme/components/input
  - Naive UI Button: https://www.naiveui.com/zh-CN/os-theme/components/button

  **WHY Each Reference Matters**:
  - `oper-log.vue`: 学习工具栏的布局和按钮使用
  - `mb-search.vue`: 了解搜索组件的实现模式
  - Naive UI docs: 掌握各组件的正确用法

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: 工具栏交互正常
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 工具栏组件已渲染
    Steps:
      1. 点击日志类型选择器
      2. 选择"错误日志"
      3. 检查是否发出 @update:logType 事件
      4. 在搜索框输入"error"
      5. 检查是否发出 @update:filter 事件
      6. 点击"清空"按钮
      7. 检查是否发出 @clear 事件
    Expected Result: 所有交互正确发出对应事件
    Failure Indicators: 事件未发出、事件参数错误
    Evidence: .sisyphus/evidence/task-3-toolbar-interaction.png

  Scenario: 连接状态显示
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 工具栏组件已渲染
    Steps:
      1. 设置 isConnected = false
      2. 检查连接状态指示器显示"未连接"
      3. 设置 isConnected = true
      4. 检查连接状态指示器显示"已连接"
    Expected Result: 连接状态正确显示
    Failure Indicators: 状态显示错误、颜色错误
    Evidence: .sisyphus/evidence/task-3-connection-status.png
  ```

  **Evidence to Capture**:
  - [ ] 浏览器截图
  - [ ] 控制台事件日志

  **Commit**: YES
  - Message: `feat(log-viewer): add LogToolbar component with filters and controls`
  - Files: `src/components/log-viewer/LogToolbar.vue`
  - Pre-commit: None

---

- [x] 4. RealtimeLog 页面集成

  **What to do**:
  - 创建 `src/views/system/monitor/realtime-log.vue` 文件
  - 集成所有组件：
    - 使用 useWebSocket composable 建立 WebSocket 连接
    - 使用 LogToolbar 组件提供控制功能
    - 使用 LogTerminal 组件显示日志
    - 实现日志过滤逻辑（根据级别和关键词）
    - 实现日志导出功能（下载为 .log 文件）
    - 实现日志类型切换（重新建立 WebSocket 连接）
  - 从 userStore 获取 token 用于 WebSocket 认证
  - 处理 WebSocket 消息并添加到 LogTerminal
  - 实现连接失败时的错误提示

  **Must NOT do**:
  - 不要硬编码 WebSocket URL（从环境变量或配置获取）
  - 不要在页面刷新时保留日志（每次加载清空）
  - 不要实现后端 WebSocket 端点

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: 完整页面集成，需要协调多个组件和状态管理
  - **Skills**: [`vue`, `naive-ui`, `pinia`]
    - `vue`: Vue 3 Composition API 和响应式状态管理
    - `naive-ui`: Naive UI 组件使用
    - `pinia`: 从 userStore 获取 token

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (after Wave 1)
  - **Blocks**: Task 5 (路由配置需要此页面)
  - **Blocked By**: Tasks 1, 2, 3 (需要 composable 和组件)

  **References**:

  **Pattern References** (existing code to follow):
  - `src/views/system/monitor/oper-log.vue:19-101` - 监控页面结构、数据获取、状态管理
  - `src/views/system/monitor/online-user.vue` - 在线用户页面（类似实时数据展示）

  **API/Type References** (contracts to implement against):
  - `src/composables/useWebSocket.js` - useWebSocket composable API
  - `src/store/modules/userStore.js:42-47` - getToken() 方法

  **External References** (libraries and frameworks):
  - Vue 3 Composables: https://vuejs.org/guide/reusability/composables.html
  - Pinia Stores: https://pinia.vuejs.org/core-concepts/

  **WHY Each Reference Matters**:
  - `oper-log.vue`: 学习监控页面的整体结构和逻辑
  - `online-user.vue`: 了解实时数据的处理方式
  - `useWebSocket.js`: 正确使用 WebSocket composable
  - `userStore.js`: 获取 token 进行认证

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: 页面完整功能测试
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 后端 WebSocket 服务已启动
      2. 开发服务器已启动
    Steps:
      1. 导航到实时日志页面
      2. 检查页面是否正确加载
      3. 检查 WebSocket 连接是否建立
      4. 验证日志是否实时显示
      5. 测试关键词过滤功能
      6. 测试日志级别筛选功能
      7. 测试日志类型切换功能
      8. 测试清空日志功能
      9. 测试导出日志功能
    Expected Result: 所有功能正常工作
    Failure Indicators: 任何功能失败、控制台报错
    Evidence: .sisyphus/evidence/task-4-full-functionality.png

  Scenario: WebSocket 连接失败处理
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 后端 WebSocket 服务未启动
    Steps:
      1. 导航到实时日志页面
      2. 检查连接状态指示器显示"未连接"
      3. 检查是否显示错误提示
      4. 启动后端服务
      5. 检查是否自动重连成功
    Expected Result: 正确处理连接失败，自动重连
    Failure Indicators: 页面崩溃、无错误提示、不重连
    Evidence: .sisyphus/evidence/task-4-connection-failure.png

  Scenario: 日志过滤和搜索
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 页面已加载并显示多条日志
    Steps:
      1. 在搜索框输入"error"
      2. 检查日志列表是否只显示包含"error"的日志
      3. 取消选择 INFO 和 DEBUG 级别
      4. 检查日志列表是否只显示 WARN 和 ERROR 级别
      5. 清空搜索框
      6. 检查日志列表是否恢复显示所有日志
    Expected Result: 过滤和搜索功能正常工作
    Failure Indicators: 过滤不生效、搜索无响应
    Evidence: .sisyphus/evidence/task-4-filter-search.png
  ```

  **Evidence to Capture**:
  - [ ] 浏览器截图
  - [ ] 控制台日志
  - [ ] 网络请求日志

  **Commit**: YES
  - Message: `feat(log-viewer): integrate realtime log viewer page`
  - Files: `src/views/system/monitor/realtime-log.vue`
  - Pre-commit: None

---

- [x] 5. 路由和菜单配置

  **What to do**:
  - 在路由配置中添加实时日志页面路由
  - 在后端 magic-api 中配置菜单项（通过界面操作或数据库）
  - 添加权限控制（v-permission 指令）
  - 权限码: `system:monitor:realtime-log`
  - 菜单路径: 系统监控 > 实时日志
  - 路由路径: `/system/monitor/realtime-log`

  **Must NOT do**:
  - 不要修改动态路由生成逻辑（由后端控制）
  - 不要硬编码菜单配置（通过后端配置）

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: 简单的路由和配置更新
  - **Skills**: [`vue`]
    - `vue`: Vue Router 配置

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (after Wave 1)
  - **Blocks**: None (final task)
  - **Blocked By**: Task 4 (需要页面文件)

  **References**:

  **Pattern References** (existing code to follow):
  - `src/scripts/router/index.js` - 路由配置模式
  - `src/scripts/router/loadRouter.js` - 动态路由加载

  **API/Type References** (contracts to implement against):
  - Vue Router: https://router.vuejs.org/

  **External References** (libraries and frameworks):
  - Vue Router: https://router.vuejs.org/guide/essentials/named-routes.html

  **WHY Each Reference Matters**:
  - `router/index.js`: 了解现有路由配置方式
  - `loadRouter.js`: 理解动态路由加载机制

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: 菜单导航正常
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 开发服务器已启动
      2. 用户已登录
    Steps:
      1. 导航到系统监控菜单
      2. 点击"实时日志"菜单项
      3. 检查 URL 是否为 /system/monitor/realtime-log
      4. 检查页面是否正确加载
    Expected Result: 菜单导航正常，页面正确加载
    Failure Indicators: 404 错误、页面不显示
    Evidence: .sisyphus/evidence/task-5-menu-navigation.png

  Scenario: 权限控制正常
    Tool: Bash (npm run dev + Playwright)
    Preconditions:
      1. 用户无 system:monitor:realtime-log 权限
    Steps:
      1. 登录无权限用户
      2. 检查"实时日志"菜单项是否隐藏
      3. 尝试直接访问 /system/monitor/realtime-log
      4. 检查是否重定向或显示无权限提示
    Expected Result: 无权限用户无法访问页面
    Failure Indicators: 无权限用户可以看到菜单或访问页面
    Evidence: .sisyphus/evidence/task-5-permission-control.png
  ```

  **Evidence to Capture**:
  - [ ] 浏览器截图
  - [ ] 控制台日志

  **Commit**: YES
  - Message: `feat(router): add realtime log viewer route and menu`
  - Files: `src/scripts/router/index.js` (if modified)
  - Pre-commit: None

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Rejection → fix → re-run.

- [x] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, curl endpoint, run command). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check evidence files exist in .sisyphus/evidence/. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [x] F2. **Code Quality Review** — `unspecified-high`
  Run `npm run build` (or equivalent). Review all changed files for: `as any`/`@ts-ignore`, empty catches, console.log in prod, commented-out code, unused imports. Check AI slop: excessive comments, over-abstraction, generic names (data/result/item/temp).
  Output: `Build [PASS/FAIL] | Files [N clean/N issues] | VERDICT`

- [x] F3. **Real Manual QA** — `unspecified-high`
  Start from clean state. Execute EVERY QA scenario from EVERY task — follow exact steps, capture evidence. Test cross-task integration (features working together, not isolation). Test edge cases: empty state, invalid input, rapid actions. Save to `.sisyphus/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | Edge Cases [N tested] | VERDICT`

- [x] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff (git log/diff). Verify 1:1 — everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT do" compliance. Detect cross-task contamination: Task N touching Task M's files. Flag unaccounted changes.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

- **Task 1**: `feat(websocket): add WebSocket composable with reconnection and heartbeat` — src/composables/useWebSocket.js
- **Task 2**: `feat(log-viewer): add LogTerminal component with virtual list` — src/components/log-viewer/LogTerminal.vue
- **Task 3**: `feat(log-viewer): add LogToolbar component with filters and controls` — src/components/log-viewer/LogToolbar.vue
- **Task 4**: `feat(log-viewer): integrate realtime log viewer page` — src/views/system/monitor/realtime-log.vue
- **Task 5**: `feat(router): add realtime log viewer route and menu` — src/scripts/router/index.js (if modified)

---

## Success Criteria

### Verification Commands
```bash
# 检查组件文件是否存在
ls src/composables/useWebSocket.js
ls src/components/log-viewer/LogTerminal.vue
ls src/components/log-viewer/LogToolbar.vue
ls src/views/system/monitor/realtime-log.vue

# 构建项目
npm run build

# 启动开发服务器测试
npm run dev
```

### Final Checklist
- [x] WebSocket 连接管理功能完整（重连、心跳）
- [x] 日志终端组件正确显示日志
- [x] 工具栏所有功能正常工作
- [x] 页面集成完整，所有组件协同工作
- [x] 路由和菜单配置正确
- [x] 权限控制正常
- [x] 所有 QA 场景通过
- [x] 代码质量检查通过
- [x] 无 scope creep
- [x] 所有证据文件已保存
