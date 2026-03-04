# Docker Compose 一键部署指南

## 🚀 快速开始

### 开发环境（推荐）

启动所有服务：MySQL + Redis + Spring Boot应用

```bash
# 启动所有服务（后台运行）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 仅查看应用日志
docker-compose logs -f app
```

### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清空所有数据）
docker-compose down -v
```

## 📦 服务列表

| 服务 | 端口 | 说明 | 管理界面 |
|------|------|------|----------|
| **MySQL** | 3306 | 主数据库 | - |
| **Redis** | 6379 | 缓存数据库 | - |
| **App** | 8081 | Spring Boot应用 | http://localhost:8081 |
| **Redis UI** | 8082 | Redis可视化管理 | http://localhost:8082 (可选) |
| **Nginx** | 80/443 | 反向代理 | - (可选) |

## 🛠️ 使用场景

### 1. 开发环境（默认）

```bash
# 启动核心服务
docker-compose up -d
```

**包含服务：** MySQL + Redis + App

**访问地址：**
- 应用：http://localhost:8081
- Magic-API编辑器：http://localhost:8081/magic/web
  - 用户名：admin
  - 密码：admin123456
- Druid监控：http://localhost:8081/druid
  - 用户名：admin
  - 密码：123456

### 2. 包含Redis管理工具

```bash
# 启动核心服务 + Redis可视化工具
docker-compose --profile tools up -d
```

**额外包含：** Redis Commander (http://localhost:8082)

### 3. 生产环境（包含Nginx）

```bash
# 启动生产环境配置
docker-compose --profile production up -d
```

**包含服务：** MySQL + Redis + App + Nginx

**注意：** 需要先配置 `nginx/` 目录下的配置文件

## ⚙️ 配置说明

### 数据库配置

**MySQL连接信息：**
```yaml
地址：mysql (容器内) 或 localhost:3306 (宿主机)
数据库：magic-boot
用户名：magic
密码：Magic@2026
Root密码：root123456
```

**Redis连接信息：**
```yaml
地址：redis (容器内) 或 localhost:6379 (宿主机)
密码：Magic@Redis2026
数据库：0
```

### 环境变量覆盖

创建 `.env` 文件自定义配置：

```bash
# .env 文件示例
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=magic-boot
MYSQL_USER=magic
MYSQL_PASSWORD=your_mysql_password

REDIS_PASSWORD=your_redis_password

SPRING_PROFILES_ACTIVE=online
```

然后启动：
```bash
docker-compose up -d
```

### 启用Redis缓存（可选）

1. **添加Redis依赖** - 编辑 `pom.xml`：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2. **取消docker-compose.yml中的注释**：
```yaml
environment:
  - SPRING_DATA_REDIS_HOST=redis
  - SPRING_DATA_REDIS_PORT=6379
  - SPRING_DATA_REDIS_PASSWORD=Magic@Redis2026
  - SPRING_DATA_REDIS_DATABASE=0
```

3. **重启服务**：
```bash
docker-compose restart app
```

## 📊 数据持久化

数据存储在Docker卷中：

```bash
# 查看数据卷
docker volume ls | grep magic-boot

# MySQL数据
docker volume inspect magic-boot_mysql_data

# Redis数据
docker volume inspect magic-boot_redis_data
```

**备份MySQL数据：**
```bash
docker-compose exec mysql mysqldump -u root -proot123456 magic-boot > backup.sql
```

**恢复MySQL数据：**
```bash
docker-compose exec -T mysql mysql -u root -proot123456 magic-boot < backup.sql
```

## 🔧 常用命令

```bash
# 重新构建应用镜像
docker-compose build app

# 重启单个服务
docker-compose restart app

# 查看服务日志
docker-compose logs -f app

# 进入应用容器
docker-compose exec app sh

# 进入MySQL容器
docker-compose exec mysql bash

# 连接MySQL
docker-compose exec mysql mysql -u magic -pMagic@2026 magic-boot

# 连接Redis
docker-compose exec redis redis-cli -a Magic@Redis2026

# 查看容器资源使用
docker stats
```

## 🔍 健康检查

所有服务都配置了健康检查：

```bash
# 查看健康状态
docker-compose ps

# 查看详细健康信息
docker inspect --format='{{json .State.Health}}' magic-boot-app | jq
```

## 🐛 故障排查

### 1. 查看服务日志

```bash
# 所有服务日志
docker-compose logs

# 特定服务日志
docker-compose logs app
docker-compose logs mysql
docker-compose logs redis
```

### 2. 检查网络连接

```bash
# 进入应用容器
docker-compose exec app sh

# 测试MySQL连接
ping mysql
telnet mysql 3306

# 测试Redis连接
ping redis
telnet redis 6379
```

### 3. 重置环境

```bash
# 停止并删除所有容器、网络、卷
docker-compose down -v --rmi all

# 重新启动
docker-compose up -d
```

### 4. 端口冲突

如果端口被占用，修改 `docker-compose.yml` 中的端口映射：

```yaml
ports:
  - "8090:8081"  # 将8081改为8090
```

## 🔐 安全建议

**生产环境必须修改：**

1. 修改所有默认密码
2. 使用环境变量或密钥管理服务
3. 启用SSL/TLS加密
4. 配置防火墙规则
5. 限制容器资源使用
6. 定期备份数据

**示例 - 资源限制：**
```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

## 📝 注意事项

1. **首次启动**：MySQL初始化需要30-60秒，请耐心等待
2. **数据持久化**：删除容器不会丢失数据，需要 `docker-compose down -v`
3. **Magic-API资源**：`data/magic-api` 目录会自动挂载，支持热更新
4. **文件上传**：上传的文件保存在 `./uploads` 目录
5. **日志文件**：应用日志保存在 `./logs` 目录

## 🎯 生产部署检查清单

- [ ] 修改所有默认密码
- [ ] 配置SSL证书（Nginx）
- [ ] 设置资源限制
- [ ] 配置日志收集
- [ ] 设置数据库备份策略
- [ ] 配置监控告警
- [ ] 启用防火墙
- [ ] 配置域名和DNS
