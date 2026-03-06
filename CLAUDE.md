# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行命令

```bash
# 构建项目
mvn clean package

# 运行应用
mvn spring-boot:run -pl magic-boot-master

# 指定 profile 运行
mvn spring-boot:run -pl magic-boot-master -Dspring-boot.run.profiles=dev

# 运行单个测试
mvn test -pl magic-boot-master -Dtest=测试类名#测试方法名

# 运行所有测试
mvn test
```

## 项目架构

### 技术栈
- **框架**: Spring Boot 3.1.2
- **JDK**: 17
- **数据库**: MySQL + Druid 连接池
- **API 框架**: magic-api 2.2.2 (低代码 API 平台，基于脚本的 API 定义)
- **认证**: Sa-Token 1.44.0
- **Redis**: 可选的 Redis 集成（使用 Redisson 客户端）
- **其他**: LiteFlow 工作流引擎、Quartz 定时任务、Forest HTTP 客户端、WebSocket

### 模块结构
```
magic-boot/
├── magic-boot-master/          # 主应用模块
│   ├── configuration/          # Spring 配置类
│   ├── interceptor/            # 请求拦截器（权限、认证）
│   ├── controller/             # REST 控制器 (LogDownloadController)
│   ├── websocket/              # WebSocket 处理器（实时日志流）
│   ├── utils/                  # 工具类 (AddressUtil, FileUtils, WebUtils)
│   ├── config/                 # Redis 和 Sa-Token 配置
│   └── model/                  # 数据模型和常量
├── magic-api-plugins/          # 插件模块
│   ├── magic-api-plugin-redis/     # Redis 集成（Redisson）
│   ├── magic-api-plugin-job/       # Quartz 定时任务
│   ├── magic-api-plugin-liteflow/  # LiteFlow 工作流引擎
│   ├── magic-api-plugin-mqtt/      # MQTT 客户端
│   ├── magic-api-plugin-springdoc/ # OpenAPI 3.0 文档
│   └── magic-editor/               # Web 编辑器组件
└── data/magic-api/             # magic-api 脚本定义（文件存储）
    ├── api/                    # API 端点脚本 (.ms 文件)
    └── function/               # 可复用函数脚本
```

### 核心组件

**magic-api 集成:**
- API 定义存储在 `data/magic-api/` 目录下的 `.ms` 脚本文件
- Web 管理界面访问地址：`/magic/web` (凭据：admin/admin123456)
- 自定义请求拦截器实现权限控制
- 支持基于分组的 API 组织
- SpringDoc 集成提供 OpenAPI 3.0 文档：`/v3/api-docs/magic-api/openapi.json`
- Swagger UI 访问：`/swagger-ui.html`

**认证流程:**
- Sa-Token 会话管理，默认 30 天有效期
- Token 存储在 Header/Cookie 中，名称为 `token`
- `PermissionInterceptor` 验证登录状态和 API 权限
- 登录接口：`/system/security/登录`

**Redis 集成:**
- 通过 `application.yml` 中的 `extend.redis.enabled` 配置开关
- 使用 Redisson 客户端实现高级 Redis 操作
- 支持单机、哨兵、集群三种模式
- Redis 禁用时自动跳过健康检查

**WebSocket 功能:**
- 实时日志流：`/log` 端点
- 基于 Token 的握手认证
- 支持关键词过滤和日志级别过滤

**数据库:**
- MySQL + Druid 连接池
- Magic API 备份记录表：`magic_backup_record_v2`
- 操作日志表：`sys_oper_log`

### 配置

主配置文件：`magic-boot-master/src/main/resources/application.yml`

关键配置项:
- 服务器端口：8081
- 上传目录：`D:/mb/`
- magic-api 资源位置：`data/magic-api`
- SQL 日志：已启用
- CRUD 逻辑删除列：`is_del`
- Redis 开关：`extend.redis.enabled` (true/false)

可用的 Profiles:
- `dev` - 开发环境（默认）
- `demo` - 演示环境
- `online` - 生产环境

### API 脚本位置

API 定义在 `data/magic-api/api/` 目录下，按分组组织:
- `系统管理/` - 系统管理 API（用户、角色、菜单等）
- `function/` - 可复用的工具函数
- 脚本使用 magic-api 脚本语言，支持内嵌 SQL

### 拦截器
- `PermissionInterceptor`: 请求级权限验证（Order: 1）
- `TokenHandshakeInterceptor`: WebSocket 握手认证

### 开发规则
- **编译规则**: 修改代码后不需要运行编译命令，用户会自己编译测试
- **插件模块**: `magic-boot-plugins/magic-api-plugin-excel` 是 Excel 导出插件，基于 EasyExcel 实现
