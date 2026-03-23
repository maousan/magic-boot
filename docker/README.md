# Magic Boot Docker 部署指南

## 目录结构

```
magic-boot/
├── docker-compose.yml       # Docker Compose 配置文件
├── .env.example             # 环境变量示例文件
├── .env                     # 环境变量配置文件（需手动创建）
├── Dockerfile               # Docker 镜像构建文件
├── db/                      # 数据库初始化脚本
├── nginx/                   # Nginx 配置目录
│   ├── nginx.conf           # Nginx 主配置文件
│   ├── conf.d/
│   │   ├── magic-boot.conf      # HTTP 反向代理配置
│   │   └── magic-boot-ssl.conf  # HTTPS 配置（生产环境）
│   └── ssl/                 # SSL 证书目录
└── docker/
    └── mqtt/
        └── docker-compose.yml  # MQTT 服务配置
```

## 快速开始

### 方式一：使用 Docker 内置数据源

使用 Docker 内置的 MySQL 和 Redis 服务：

```bash
# 1. 复制环境变量文件
cp .env.example .env

# 2. 设置使用 Docker 内置数据库
# 编辑 .env 文件，设置 USE_DOCKER_DB=true

# 3. 启动服务
docker-compose --profile docker-db up -d

# 4. 查看日志
docker-compose logs -f app
```

### 方式二：使用外部数据源（推荐生产环境）

连接到外部的 MySQL 和 Redis 服务：

```bash
# 1. 复制环境变量文件
cp .env.example .env

# 2. 编辑 .env 文件，配置外部数据源
# - 设置 USE_DOCKER_DB=false
# - 修改 SPRING_DATASOURCE_URL 为外部 MySQL 地址
# - 修改 SPRING_DATA_REDIS_HOST 为外部 Redis 地址

# 3. 启动服务（不需要启动内置数据库）
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app
```

## 环境变量说明

### 数据源模式

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `USE_DOCKER_DB` | 是否使用 Docker 内置数据库 | `false` |

### MySQL 配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `MYSQL_PORT` | Docker MySQL 暴露端口 | `3306` |
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | `root123456` |
| `MYSQL_DATABASE` | MySQL 数据库名 | `magic-boot` |
| `MYSQL_USER` | MySQL 用户名 | `magic` |
| `MYSQL_PASSWORD` | MySQL 密码 | `Magic@2026` |
| `SPRING_DATASOURCE_URL` | 外部 MySQL JDBC 连接串 | - |
| `SPRING_DATASOURCE_USERNAME` | 外部 MySQL 用户名 | `magic` |
| `SPRING_DATASOURCE_PASSWORD` | 外部 MySQL 密码 | `Magic@2026` |

### Redis 配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `EXTEND_REDIS_ENABLED` | 是否启用 Redis | `true` |
| `REDIS_PORT` | Docker Redis 暴露端口 | `6379` |
| `REDIS_PASSWORD` | Docker Redis 密码 | `Magic@Redis2026` |
| `SPRING_DATA_REDIS_HOST` | 外部 Redis 主机地址 | `redis` |
| `SPRING_DATA_REDIS_PORT` | 外部 Redis 端口 | `6379` |
| `SPRING_DATA_REDIS_PASSWORD` | 外部 Redis 密码 | - |
| `SPRING_DATA_REDIS_DATABASE` | 外部 Redis 数据库索引 | `0` |

### 应用配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `APP_PORT` | 应用暴露端口 | `8089` |
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `dev` |
| `UPLOAD_DIR` | 上传目录 | `/app/uploads` |

## Profile 说明

| Profile | 说明 | 使用方式 |
|---------|------|----------|
| `docker-db` | 启用 Docker 内置 MySQL 和 Redis | `--profile docker-db` |
| `tools` | 启用 Redis Commander 管理工具 | `--profile tools` |
| `production` | 启用 Nginx 反向代理 | `--profile production` |

## Nginx 配置说明

### 配置文件

| 文件 | 说明 |
| :--- | :--- |
| `nginx/nginx.conf` | Nginx 主配置（Gzip、日志、安全头部） |
| `nginx/conf.d/magic-boot.conf` | HTTP 反向代理配置 |
| `nginx/conf.d/magic-boot-ssl.conf` | HTTPS 配置（生产环境） |

### 使用 Nginx 反向代理

```bash
# 1. 准备 SSL 证书（HTTPS 需要）
# 将证书文件放置到 nginx/ssl/ 目录：
# - fullchain.pem (证书链)
# - privkey.pem (私钥)

# 2. 编辑 nginx/conf.d/magic-boot-ssl.conf
# 修改 server_name 为你的域名

# 3. 启动服务（包含 Nginx）
docker-compose --profile production up -d

# 4. 查看 Nginx 日志
docker-compose logs -f nginx
```

### Nginx 端口配置

在 `.env` 文件中配置：

```env
# HTTP 端口（默认 80）
NGINX_HTTP_PORT=80

# HTTPS 端口（默认 443）
NGINX_HTTPS_PORT=443
```

### 已配置的代理路径

| 路径 | 说明 |
| :--- | :--- |
| `/magic/web/` | Magic API Web 管理界面 |
| `/magic/api/` | Magic API 接口 |
| `/log` | WebSocket 日志端点 |
| `/swagger-ui/` | Swagger UI |
| `/v3/api-docs/` | OpenAPI 文档 |
| `/actuator/` | Spring Actuator 健康检查 |
| `/druid/` | Druid 监控（建议限制访问） |

### 生产环境安全建议

1. **限制 Druid 监控访问**：在 `magic-boot.conf` 中取消注释 `allow/deny` 配置
2. **启用 HTTPS**：使用 SSL 配置并强制 HTTP 重定向
3. **配置防火墙**：仅开放必要的端口
4. **定期更新证书**：使用 Let's Encrypt 免费证书自动续期

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 使用内置数据库启动
docker-compose --profile docker-db up -d

# 停止服务
docker-compose down

# 停止服务并删除数据卷
docker-compose --profile docker-db down -v

# 查看日志
docker-compose logs -f app

# 重启服务
docker-compose restart

# 重新构建镜像
docker-compose build app

# 查看服务状态
docker-compose ps
```

## 外部数据源配置示例

### 配置外部 MySQL

```env
USE_DOCKER_DB=false

# 外部 MySQL 配置
SPRING_DATASOURCE_URL=jdbc:mysql://192.168.1.100:3306/magic-boot?useSSL=false&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF8&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=magic
SPRING_DATASOURCE_PASSWORD=Magic@2026
```

### 配置外部 Redis

```env
# 外部 Redis 配置
SPRING_DATA_REDIS_HOST=192.168.1.100
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=your_redis_password
SPRING_DATA_REDIS_DATABASE=3
```

## 目录挂载

以下目录会被挂载到容器外，便于数据持久化和热更新：

| 宿主机目录 | 容器目录 | 说明 |
|-----------|---------|------|
| `./data/magic-api` | `/app/data/magic-api` | magic-api 脚本（支持热更新） |
| `./uploads` | `/app/uploads` | 文件上传目录 |
| `./logs` | `/app/logs` | 日志文件目录 |
| `mysql_data` 卷 | `/var/lib/mysql` | MySQL 数据持久化 |
| `redis_data` 卷 | `/data` | Redis 数据持久化 |

## 健康检查

```bash
# 检查应用健康状态
curl http://localhost:8089/actuator/health

# 访问 magic-api 管理界面
# http://localhost:8089/magic/web
# 默认账号：admin / K7vQ9@xP4Lr2!mZ8
```

## 注意事项

1. **首次使用外部数据源**：需要确保外部 MySQL 数据库已创建并执行了 `db/magic-boot.sql` 初始化脚本
2. **Redis 可选**：如果不需要 Redis，设置 `EXTEND_REDIS_ENABLED=false`
3. **数据持久化**：使用 Docker 内置数据库时，数据存储在 Docker 卷中，删除容器不会丢失数据
4. **端口冲突**：如果本地已有 MySQL 或 Redis，请修改 `MYSQL_PORT` 和 `REDIS_PORT` 或使用外部数据源
