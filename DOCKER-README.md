# Docker 一键部署

## 🚀 快速开始（推荐）

### Windows 用户
```bash
# 双击运行或在命令行执行
docker-manage.bat start

# 查看帮助
docker-manage.bat help
```

### Linux/Mac 用户
```bash
# 添加执行权限
chmod +x docker-manage.sh

# 启动服务
./docker-manage.sh start

# 查看帮助
./docker-manage.sh help
```

## 📦 包含的服务

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| **MySQL** | mysql:8.0 | 3306 | 主数据库 |
| **Redis** | redis:7-alpine | 6379 | 缓存数据库 |
| **App** | 自定义构建 | 8081 | Spring Boot应用 |
| **Redis UI** | redis-commander | 8082 | Redis可视化管理（可选） |
| **Nginx** | nginx:alpine | 80/443 | 反向代理（可选） |

## 🎯 使用场景

### 1. 开发环境（默认）
```bash
# 启动核心服务
docker-manage.bat start   # Windows
./docker-manage.sh start  # Linux/Mac

# 包含Redis管理工具
docker-manage.bat start --with-tools
```

### 2. 生产环境
```bash
# 包含Nginx反向代理
docker-manage.bat start --production
```

### 3. 常用操作

```bash
# 查看服务状态
docker-manage.bat status

# 查看日志
docker-manage.bat logs           # 所有服务
docker-manage.bat logs app       # 仅应用

# 停止服务
docker-manage.bat stop

# 重启服务
docker-manage.bat restart

# 备份数据库
docker-manage.bat backup

# 重新构建镜像
docker-manage.bat build

# 清理环境（删除所有数据）
docker-manage.bat clean
```

## 🔑 默认密码

**生产环境请务必修改！**

| 服务 | 用户名 | 密码 |
|------|--------|------|
| MySQL Root | root | root123456 |
| MySQL App | magic | Magic@2026 |
| Redis | - | Magic@Redis2026 |
| Magic-API | admin | admin123456 |
| Druid | admin | 123456 |

## 📁 目录结构

```
magic-boot/
├── docker-compose.yml      # Docker Compose配置
├── Dockerfile              # 应用镜像构建文件
├── .dockerignore          # Docker构建忽略文件
├── docker-manage.bat      # Windows管理脚本
├── docker-manage.sh       # Linux/Mac管理脚本
├── DOCKER-COMPOSE.md      # 详细使用文档
├── data/
│   └── magic-api/         # Magic-API资源（支持热更新）
├── uploads/               # 文件上传目录
├── logs/                  # 应用日志
├── db/                    # 数据库初始化脚本
└── nginx/                 # Nginx配置（生产环境）
    ├── nginx.conf
    ├── conf.d/
    └── ssl/
```

## ⚡ 一键启动流程

1. **环境检查** - 验证Docker和Docker Compose
2. **目录创建** - 自动创建必要的目录
3. **镜像构建** - 构建Spring Boot应用镜像
4. **服务启动** - 按依赖顺序启动MySQL → Redis → App
5. **健康检查** - 等待所有服务就绪

## 🔧 高级配置

### 自定义环境变量

创建 `.env` 文件：
```bash
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_PASSWORD=your_app_password
REDIS_PASSWORD=your_redis_password
```

### 启用Redis缓存

1. 在 `pom.xml` 添加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2. 在 `docker-compose.yml` 取消Redis配置的注释

3. 重启服务：
```bash
docker-manage.bat restart
```

## 📚 详细文档

- [Docker Compose 详细配置](DOCKER-COMPOSE.md)
- [Dockerfile 构建说明](DOCKER.md)
- [Magic-API 文档](data/magic-api/AGENTS.md)

## 🐛 故障排查

### 服务启动失败
```bash
# 查看详细日志
docker-manage.bat logs

# 检查端口占用
netstat -ano | findstr :8081
netstat -ano | findstr :3306
netstat -ano | findstr :6379
```

### 数据库连接失败
```bash
# 检查MySQL状态
docker-manage.bat logs mysql

# 手动测试连接
docker exec -it magic-boot-mysql mysql -u magic -p
```

### 重置环境
```bash
# ⚠️ 警告：会删除所有数据
docker-manage.bat clean
docker-manage.bat start
```

## 🎉 访问地址

启动成功后访问：

- **应用主页**: http://localhost:8081
- **Magic-API编辑器**: http://localhost:8081/magic/web
- **Druid监控**: http://localhost:8081/druid
- **Redis管理**: http://localhost:8082 (需使用 `--with-tools` 参数)

---

**提示**: 首次启动需要下载镜像和初始化数据库，请等待2-3分钟。
