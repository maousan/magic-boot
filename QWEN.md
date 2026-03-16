# Magic-Boot 项目知识库

## 项目概述

Magic-Boot 是一个基于 [magic-api](https://gitee.com/ssssssss-team/magic-api) 搭建的快速开发平台。它结合了 Spring Boot 3.1.2 + Java 17 的后端技术栈和 Vue 3 + Naive UI 的前端技术栈，支持在浏览器中动态编写 API 和 Vue 组件，实现即改即生效的快速开发模式。

### 核心特性
- **动态 API 生成**: 通过 magic-api 实现 HTTP API 的自动生成
- **在线组件开发**: 支持在浏览器中编写 Vue3 业务页面代码并实时生效
- **完整的后台管理系统**: 包含菜单管理、组织机构、角色管理、用户管理、数据字典等功能
- **丰富的插件体系**: 包括 Redis、定时任务、工作流(LiteFlow)、Excel 导出等插件

### 项目架构
- **后端**: Spring Boot 3.1.2 + Java 17
- **前端**: Vue 3 + Naive UI
- **数据库**: MySQL
- **缓存**: Redis
- **API 框架**: magic-api
- **认证授权**: Sa-Token

## 项目结构

```
magic-boot/
├── magic-api-parent/      # magic-api 父项目及核心模块
├── magic-boot-plugins/    # magic-api 扩展插件集
│   ├── magic-api-plugin-redis/      # Redis 插件
│   ├── magic-api-plugin-job/        # 定时任务插件
│   ├── magic-api-plugin-liteflow/   # 工作流插件
│   ├── magic-api-plugin-mqtt/       # MQTT 插件
│   ├── magic-api-plugin-springdoc/  # SpringDoc 插件
│   ├── magic-api-plugin-excel/      # Excel 插件
│   └── magic-editor/                # 编辑器前端资源
├── magic-boot-master/     # 主应用模块（Spring Boot 入口）
├── data/magic-api/        # 运行时 API 定义（通过 Web UI 编辑）
│   ├── api/               # API 定义文件 (.ms)
│   ├── datasource/        # 数据源配置
│   ├── function/          # 可复用函数
│   ├── job/               # 定时任务定义
│   └── ...
├── db/                    # 数据库初始化脚本
└── logs/                  # 日志目录
```

## 构建与运行

### 环境要求
- Java 17
- Maven 3.6+
- Node.js (如果需要构建前端)
- MySQL
- Redis

### 构建命令
```bash
# 开发环境构建（跳过前端构建）
mvn clean package -Pdev -DskipTests

# 生产环境构建（完整构建）
mvn clean package -Pprod -DskipTests
```

### 运行命令
```bash
# 直接运行主应用
java -jar magic-boot-master/target/magic-boot.jar

# 或者使用 Maven 运行
mvn spring-boot:run -pl magic-boot-master
```

### Docker 部署
```bash
# 使用 Docker Compose 启动完整环境
docker-compose up -d
```

## 配置说明

### 主要配置文件
- `magic-boot-master/src/main/resources/application.yml`: 主应用配置文件
- `.env.example`: 环境变量示例文件

### 关键配置项
- **服务端口**: `server.port` (默认 8081)
- **数据库连接**: 在 `application.yml` 中配置 MySQL 连接信息
- **Redis 配置**: 在 `application.yml` 中配置 Redis 连接信息
- **magic-api 配置**:
  - `magic-api.web`: Web UI 路径 (默认 `/magic/web`)
  - `magic-api.show-sql`: 是否显示 SQL (默认 true)
  - `magic-api.cache.ttl`: 缓存有效期 (默认 1 小时)
- **Sa-Token 配置**: 认证授权相关配置

## 开发约定

### 代码风格
- 后端: 遵循标准 Spring Boot 代码风格
- 前端: Vue 3 + Vite，无强制 ESLint/Prettier 配置

### 数据库约定
- **逻辑删除**: 使用 `is_del` 字段，值为 `1` 表示已删除
- **分页参数**: `pageNo` 和 `pageSize`

### API 开发
- API 通过 magic-api 的 Web UI (`/magic/web`) 进行动态创建和编辑
- 不应直接编辑 `data/magic-api/api/*.ms` 文件
- 支持多种 HTTP 方法和参数类型定义

## 重要功能模块

### 用户认证与授权
- 基于 Sa-Token 实现
- Token 有效期 30 天
- 支持多设备登录

### 数据库监控
- 集成 Druid 监控
- 访问路径: `/druid/*`
- 默认账号: admin/123456

### 文件上传
- 限制大小: 200MB
- 上传路径可在配置中自定义

### 工作流支持
- 集成 LiteFlow 工作流引擎
- 支持复杂业务流程编排

## 部署说明

### 生产环境部署
1. 构建项目: `mvn clean package -Pprod -DskipTests`
2. 配置生产环境的 `application.yml` 或环境变量
3. 运行 JAR 文件: `java -jar magic-boot.jar`

### Docker 部署
- 使用提供的 `docker-compose.yml` 文件
- 支持一键部署 MySQL、Redis 和应用服务

## 项目入口

- **应用主入口**: `magic-boot-master/src/main/java/.../MagicBootApplication.java`
- **Web UI 地址**: `http://localhost:8081/magic/web`
- **API 文档**: 通过 SpringDoc 集成
- **演示地址**:
  - 前台: https://preview.magicboot.net/
  - 后台: https://api.magicboot.net:8443/magic/web/index.html
  - 账号: system/123456