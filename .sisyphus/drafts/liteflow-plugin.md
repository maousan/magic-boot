# Draft: LiteFlow 任务编排插件开发计划

## 项目背景
用户希望在 magic-boot 项目中开发一个基于 LiteFlow 组件式规则引擎的任务编排插件。

## 技术栈
- **后端**: Spring Boot 3.1.2 + Java 17
- **前端**: Vue 3 + Vite + naive-ui (magic-editor 编辑器)
- **规则引擎**: LiteFlow (支持 Groovy 脚本动态编程)
- **核心特性**: 
  - 允许编写编排规则（流程链）
  - 每个组件节点可以动态编写 Groovy 脚本

## magic-boot 现有插件架构模式

### 标准目录结构
```
magic-api-plugin-{name}/
├── src/main/java/org/ssssssss/magicapi/{name}/
│   ├── model/           # 数据模型 (如：JobInfo.java)
│   ├── service/         # 业务逻辑服务
│   ├── starter/         # Spring Boot 自动配置
│   │   ├── Magic{X}Config.java      # 配置属性类
│   │   └── Magic{X}Configuration.java # 自动配置类
│   ├── util/            # 工具类
│   └── web/             # REST 控制器
└── src/console/         # Vue 3 前端控制台
    ├── src/
    │   ├── index.js         # 插件入口
    │   ├── i18n/            # 国际化文件
    │   ├── icons/           # SVG 图标
    │   ├── components/      # UI 组件
    │   └── service/         # API 服务
    ├── package.json
    └── vite.config.js
```

### 构建约定
- **开发构建**: `mvn clean package -Pdev -DskipTests` (跳过前端)
- **生产构建**: `mvn clean package -Pprod -DskipTests` (完整构建)
- 前端通过 exec-maven-plugin 执行 npm install && npm run build
- 产物复制到 `target/classes/magic-editor/plugins/`

### 关键依赖
- magic-api (provided scope)
- magic-script (provided scope)
- Spring Boot starters (provided scope)

## LiteFlow 核心概念 (基于通用知识)

### 核心组件
- **Component**: 流程中的执行单元，定义组件逻辑
- **Chain**: 流程编排链，定义组件执行顺序
- **Flow**: 完整的流程实例
- **LiteFlowConfig**: Spring Boot 配置类
- **Groovy 脚本**: 支持动态编写组件和执行脚本

### 典型集成方式
1. 添加 LiteFlow 依赖
2. 配置 LiteFlowConfig 启用 Spring Boot 集成
3. 定义 EL 表达式编排规则
4. 支持脚本热加载

## 需要确认的关键问题

### 1. LiteFlow 集成深度
- [ ] 仅使用 LiteFlow 核心引擎，还是需要其全部功能？
- [ ] 是否需要支持流程可视化编排（流程图拖拽）？
- [ ] 流程定义存储方式：数据库/文件/两者结合？

### 2. Groovy 脚本范围
- [ ] 每个组件节点的脚本是完整的组件逻辑，还是仅业务片段？
- [ ] 脚本中可访问的上下文变量有哪些？
- [ ] 是否需要脚本版本管理和回滚？

### 3. 编排规则编辑
- [ ] 使用 Web UI 可视化编辑流程链？
- [ ] 还是使用文本方式编辑 EL 表达式？
- [ ] 或两者都支持？

### 4. 执行触发方式
- [ ] 仅支持 API 调用触发？
- [ ] 还是需要支持定时任务触发（类似 job 插件）？
- [ ] 或事件驱动触发？

### 5. 流程监控需求
- [ ] 是否需要流程执行日志和追踪？
- [ ] 是否需要流程执行统计和仪表盘？
- [ ] 是否需要流程执行暂停/恢复/终止？

### 6. 高级功能
- [ ] 是否需要支持子流程/嵌套流程？
- [ ] 是否需要支持流程版本管理？
- [ ] 是否需要支持条件分支和并行执行？
- [ ] 是否需要支持流程模板功能？

## 初步范围边界

### INCLUDE (范围内)
- LiteFlow 与 magic-boot 的基础集成
- 组件节点的 Groovy 脚本动态编辑
- 流程编排规则的定义和管理
- 流程执行 API
- 基础的前端编辑器支持

### EXCLUDE (范围外 - 待确认)
- 高级可视化流程编排（可能需要额外开发）
- 复杂的流程版本管理系统
- 企业级流程监控和告警

## 待决事项
- 等待用户确认以上 6 个大类问题
- 确认后制定详细开发计划

## 用户确认的需求 (2026-03-04)

### 1. LiteFlow 集成深度
- **选择**: 基础级
- **范围**: 仅使用 LiteFlow 核心引擎，EL 表达式编排 + Groovy 脚本组件

### 2. 流程编排编辑方式
- **选择**: 方案 A (文本编辑)
- **实现**: 类似 API 脚本，使用 magic-api 编辑器直接编写 EL 表达式和 Groovy 脚本

### 3. 流程定义存储
- **选择**: 文件存储
- **位置**: 使用 magic-api 现有的文件存储机制 (data/magic-api/liteflow/)

### 4. Groovy 脚本范围
- **选择**: B - 脚本只编写业务逻辑片段
- **上下文**: 访问流程上下文变量 (如:`context.get("data")`)

### 5. 执行触发方式
- **选择**: A + C
- **A**: API 调用触发 (`POST /liteflow/execute/{flowId}`)
- **C**: 事件驱动触发 (当其他 API 执行完成时自动触发流程)

### 6. 流程监控需求
- **选择**: A + B + C
- **A**: 基础执行日志 (流程开始/结束/异常记录)
- **B**: 流程追踪 (查看每次执行的详细步骤和结果)
- **C**: 统计仪表盘 (流程调用次数、成功率、耗时统计)

### 7. 高级功能
- **选择**: B + D
- **B**: 流程版本管理 (发布/回滚/多版本并存)
- **D**: 复杂规则 (条件分支、并行执行、异常捕获)

### 8. 预期工期
- **目标**: 1-2 周

## 状态
- ✅ 所有需求已确认
- 下一步：生成详细开发计划