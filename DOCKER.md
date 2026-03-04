# Docker 部署指南

## 快速开始

### 方式一：使用 Docker Compose（推荐）

完整的开发环境（包括MySQL数据库）：

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

访问地址：
- 应用：http://localhost:8081
- Magic-API编辑器：http://localhost:8081/magic/web
- Druid监控：http://localhost:8081/druid

### 方式二：仅构建应用镜像

```bash
# 构建镜像
docker build -t magic-boot:latest .

# 运行容器（需要外部MySQL）
docker run -d \
  --name magic-boot \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/magic-boot?useSSL=false \
  -e SPRING_DATASOURCE_USERNAME=magic \
  -e SPRING_DATASOURCE_PASSWORD=Magic@2026 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -v $(pwd)/data/magic-api:/app/data/magic-api \
  magic-boot:latest
```

## 环境配置

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| SPRING_PROFILES_ACTIVE | Spring配置环境 | dev |
| SPRING_DATASOURCE_URL | 数据库连接URL | - |
| SPRING_DATASOURCE_USERNAME | 数据库用户名 | magic |
| SPRING_DATASOURCE_PASSWORD | 数据库密码 | Magic@2026 |
| JAVA_OPTS | JVM参数 | -Xms512m -Xmx1024m |

### 数据卷挂载

```bash
# Magic-API资源目录（支持热更新）
-v ./data/magic-api:/app/data/magic-api

# 上传文件目录
-v ./uploads:/app/uploads

# 日志目录
-v ./logs:/app/logs
```

## 生产环境部署

### 1. 使用生产配置文件

```bash
docker run -d \
  --name magic-boot \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=online \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://prod-mysql:3306/magic-boot \
  -e SPRING_DATASOURCE_USERNAME=magic \
  -e SPRING_DATASOURCE_PASSWORD=<secure-password> \
  -e JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC \
  magic-boot:latest
```

### 2. 健康检查

容器内置健康检查，可通过以下命令查看状态：

```bash
docker inspect --format='{{json .State.Health}}' magic-boot
```

### 3. 资源限制

```bash
docker run -d \
  --name magic-boot \
  --memory="2g" \
  --cpus="2" \
  -p 8081:8081 \
  magic-boot:latest
```

## 多阶段构建说明

Dockerfile使用多阶段构建：
1. **Builder阶段**：Maven编译打包
2. **Runtime阶段**：精简的JRE运行环境

优势：
- 镜像体积小（~200MB vs ~600MB）
- 安全性高（非root用户运行）
- 构建缓存优化

## 故障排查

### 查看容器日志
```bash
docker logs -f magic-boot
```

### 进入容器
```bash
docker exec -it magic-boot sh
```

### 检查网络连接
```bash
docker exec magic-boot ping mysql
```

## 注意事项

1. **magic-api资源**：生产环境建议将`data/magic-api`挂载到持久化卷
2. **数据库初始化**：首次启动会自动执行`db/magic-boot.sql`
3. **文件上传**：需要挂载上传目录或配置OSS
4. **安全性**：
   - 生产环境使用强密码
   - 启用SSL/TLS
   - 配置防火墙规则
