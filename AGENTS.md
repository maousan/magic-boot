## AGENT行为

- 使用中文回复

## 构建与运行命令

```bash
# 启动/重启后端服务，必须使用task启动
./start.bat 

# 先构建再启动，必须使用task启动
./start.bat -b

```

## 项目架构

### 技术栈

- **框架**: Spring Boot 3.1.2
- **JDK**: 17
- **数据库**: MySQL + Druid 连接池
- **API 框架**: magic-api 2.2.2 (低代码 API 平台，基于脚本的 API 定义)
- **认证**: Sa-Token 1.44.0
- **Redis**: 可选的 Redis 集成（使用 Redisson 客户端）
- **插件系统**: PF4J 3.12.0（运行时热加载）
- **其他**: LiteFlow 工作流引擎、Quartz 定时任务、Forest HTTP 客户端、WebSocket

### 模块结构

项目包含 **两套独立的插件系统**：

| 系统 | 位置 | 加载方式 | 用途 |
| ---- | ---- | -------- | ---- |
| **PF4J 动态插件系统** | `magic-plugins/` | 运行时热加载 | 用户自定义扩展 |
| **Magic-API 内置插件** | `magic-boot-plugins/` | 编译时模块 | 核心功能扩展 |

```
magic-boot/
├── magic-boot-master/          # 主应用模块
│   ├── configuration/          # Spring 配置类
│   ├── interceptor/            # 请求拦截器（权限、认证）
│   ├── controller/             # REST 控制器
│   ├── websocket/              # WebSocket 处理器（实时日志流）
│   └── config/                 # Redis 和 Sa-Token 配置
│
├── magic-plugins/              # PF4J 插件系统核心
│   └── pf4j/                   # 插件生命周期管理、扩展点执行
│
├── magic-plugin-api/           # 扩展点 API 定义
│   ├── interceptor/            # ApiInterceptorExtension
│   ├── datasource/             # DataSourceExtension
│   ├── scheduler/              # JobExtension
│   └── transformer/            # DataTransformerExtension
│
├── magic-plugins-demo/         # 示例插件（参考实现）
│   └── demo/
│       ├── DemoPlugin.java     # 插件入口
│       ├── controller/         # 插件 REST API
│       └── service/            # 扩展点实现
│
├── magic-boot-plugins/         # Magic-API 内置插件（编译时）
│   ├── magic-api-plugin-redis/     # Redis 集成
│   ├── magic-api-plugin-job/       # Quartz 定时任务
│   ├── magic-api-plugin-liteflow/  # LiteFlow 工作流
│   ├── magic-api-plugin-excel/     # Excel 导出
│   └── magic-api-plugin-springdoc/ # OpenAPI 文档
│
├── magic-api-parent/           # magic-api 源码模块
├── magic-dependencies/         # 依赖版本管理
├── magic-editor/               # Web 编辑器组件
│
└── data/magic-api/             # magic-api 脚本定义
    ├── api/                    # API 端点脚本 (.ms 文件)
    └── function/               # 可复用函数脚本
```

### 核心组件

**magic-api 集成:**

- API 定义存储在 `data/magic-api/` 目录下的 `.ms` 脚本文件
- Web 管理界面：`/magic/web` (凭据：admin/admin123456)
- 自定义请求拦截器实现权限控制
- SpringDoc 集成：`/v3/api-docs/magic-api/openapi.json`
- Swagger UI：`/swagger-ui.html`

**PF4J 插件系统:**

- 插件目录：`D:/mb/plugins/`（通过 `plugin.dir` 配置）
- 管理 API：`/plugin/admin/*`（list、install、start、stop、reload、uninstall）
- 支持运行时热加载，独立的 ClassLoader 隔离
- 扩展点通过 `@Extension` 注解实现，由 `ExtensionPointManager` 管理
- 插件 Controller 通过 `PluginControllerRegistrar` 动态注册

**认证流程:**

- Sa-Token 会话管理，默认 30 天有效期
- Token 存储在 Header/Cookie 中，名称为 `token`
- `PermissionInterceptor` 验证登录状态和 API 权限
- 登录接口：`/system/security/登录`

**Redis 集成:**

- 配置开关：`extend.redis.enabled`
- 使用 Redisson 客户端，支持单机/哨兵/集群模式
- 禁用时自动跳过健康检查

**WebSocket 功能:**

- 实时日志流：`/log` 端点
- 基于 Token 的握手认证
- 支持关键词过滤和日志级别过滤

### 配置

主配置文件：`magic-boot-master/src/main/resources/application.yml`

关键配置项:

- 服务器端口：8089
- 上传目录：`./upload/`
- 插件目录：`./plugins/`
- magic-api 资源位置：`data/magic-api`
- Redis 开关：`extend.redis.enabled`

可用的 Profiles:

- `dev` - 开发环境（默认）
- `demo` - 演示环境
- `online` - 生产环境

### 开发规则

- **编译规则**: 修改代码后不需要运行编译命令，用户会自己编译测试
- **新增扩展点**: 在 `magic-plugin-api` 模块定义接口，在 `magic-plugins` 模块实现执行器
- **开发插件**: 参考 `magic-plugins-demo` 模块结构，实现扩展点接口并标注 `@Extension`

### 测试

- **测试http接口**: 在目录http中编写.http文件，根据.http文件进行单元测试

### 其他

- 新增特性或者功能后生成里程碑
