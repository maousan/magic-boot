# Tiga Platform - 构建指南

本文档说明如何构建新的模块化项目。

## ⚠️ 重要说明

当前项目处于迁移过渡期，存在两套构建系统：

1. **旧系统** - 单体应用（当前默认）
2. **新系统** - 模块化架构（需手动切换）

## 🏗️ 构建新模块化项目

### 方式1：使用新的父POM

```bash
# 1. 备份当前pom.xml
mv pom.xml pom-app.xml

# 2. 使用新的父POM
mv pom-parent.xml pom.xml

# 3. 创建应用子模块目录（如果不存在）
mkdir -p tiga-platform-app/src

# 4. 移动原应用代码到子模块
mv src tiga-platform-app/
mv pom-app.xml tiga-platform-app/pom.xml

# 5. 创建应用子模块的POM
cat > tiga-platform-app/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.ocean.tiga</groupId>
        <artifactId>tiga-platform-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>tiga-platform-app</artifactId>
    <packaging>jar</packaging>

    <name>Tiga Platform Application</name>

    <dependencies>
        <dependency>
            <groupId>com.ocean.tiga</groupId>
            <artifactId>tiga-engine-solon</artifactId>
        </dependency>

        <!-- 从原pom.xml复制其他依赖 -->
    </dependencies>
</project>
EOF

# 6. 更新父POM，添加应用模块
# 编辑pom.xml，在<modules>中添加：
# <module>tiga-platform-app</module>

# 7. 构建所有模块
mvn clean install
```

### 方式2：独立构建各个模块

#### 构建核心引擎模块

```bash
cd tiga-engine
mvn clean install
```

#### 构建Solon集成模块

```bash
cd tiga-engine-solon
mvn clean install
```

#### 构建Spring Boot集成模块

```bash
cd tiga-engine-spring
mvn clean install
```

## 📋 构建检查清单

### 核心模块（tiga-engine）

- [ ] 编译通过
  ```bash
  cd tiga-engine
  mvn clean compile
  ```

- [ ] 依赖检查
  ```bash
  mvn dependency:tree
  ```
  确保没有Solon/Spring依赖

- [ ] 测试通过
  ```bash
  mvn test
  ```

### Solon集成模块（tiga-engine-solon）

- [ ] 编译通过
- [ ] 测试通过
- [ ] 依赖tiga-engine成功

### Spring Boot集成模块（tiga-engine-spring）

- [ ] 编译通过
- [ ] 测试通过
- [ ] 依赖tiga-engine成功

## 🐛 常见构建问题

### Q1: 找不到父POM

**错误信息：**
```
Non-resolvable parent POM for com.ocean.tiga:tiga-engine:1.0.0
```

**解决方案：**
1. 确保在根目录下有`pom.xml`（父POM）
2. 检查`<relativePath>`配置是否正确
3. 先编译父POM：`mvn install -N`

### Q2: 依赖冲突

**错误信息：**
```
Detected both log4j and slf4j
```

**解决方案：**
使用`<exclusions>`排除冲突依赖：
```xml
<dependency>
    <groupId>org.ssssssss</groupId>
    <artifactId>magic-script</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Q3: 编译找不到类

**错误信息：**
```
package com.ocean.tiga.engine.api does not exist
```

**解决方案：**
1. 确保tiga-engine已安装到本地仓库：`cd tiga-engine && mvn install`
2. 检查依赖版本是否一致
3. 清理并重新构建：`mvn clean install`

## 📦 发布到Maven仓库

### 发布到本地仓库

```bash
mvn clean install
```

### 发布到远程仓库

需要配置`~/.m2/settings.xml`：

```xml
<servers>
    <server>
        <id>tiga-releases</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

然后在pom.xml中配置：

```xml
<distributionManagement>
    <repository>
        <id>tiga-releases</id>
        <url>https://your-nexus.com/repository/maven-releases/</url>
    </repository>
</distributionManagement>
```

执行发布：

```bash
mvn clean deploy
```

## 🔍 构建验证

### 验证模块独立性

```bash
# 检查tiga-engine的依赖
cd tiga-engine
mvn dependency:tree > dependencies.txt

# 确保没有以下依赖：
# - org.noear:*
# - org.springframework:*
# （除了测试依赖）
```

### 验证集成模块

```bash
# 检查tiga-engine-solon的依赖
cd tiga-engine-solon
mvn dependency:tree > dependencies.txt

# 确保包含：
# - com.ocean.tiga:tiga-engine
# - org.noear:solon-web
```

### 验证Spring Boot集成

```bash
# 检查tiga-engine-spring的依赖
cd tiga-engine-spring
mvn dependency:tree > dependencies.txt

# 确保包含：
# - com.ocean.tiga:tiga-engine
# - org.springframework.boot:*
```

## 🚀 快速构建脚本

创建`build-all.sh`（Linux/Mac）或`build-all.bat`（Windows）：

### Linux/Mac版本

```bash
#!/bin/bash
set -e

echo "=== Building Tiga Platform Modules ==="

echo "[1/4] Building tiga-engine..."
cd tiga-engine
mvn clean install
cd ..

echo "[2/4] Building tiga-engine-solon..."
cd tiga-engine-solon
mvn clean install
cd ..

echo "[3/4] Building tiga-engine-spring..."
cd tiga-engine-spring
mvn clean install
cd ..

echo "[4/4] Building tiga-platform-app..."
cd tiga-platform-app
mvn clean install
cd ..

echo "=== All modules built successfully! ==="
```

### Windows版本

```batch
@echo off
echo === Building Tiga Platform Modules ===

echo [1/4] Building tiga-engine...
cd tiga-engine
call mvn clean install
cd ..

echo [2/4] Building tiga-engine-solon...
cd tiga-engine-solon
call mvn clean install
cd ..

echo [3/4] Building tiga-engine-spring...
cd tiga-engine-spring
call mvn clean install
cd ..

echo [4/4] Building tiga-platform-app...
cd tiga-platform-app
call mvn clean install
cd ..

echo === All modules built successfully! ===
```

运行：

```bash
# Linux/Mac
chmod +x build-all.sh
./build-all.sh

# Windows
build-all.bat
```

## 📊 构建输出

成功构建后，应该在以下目录找到JAR文件：

```
tiga-engine/target/tiga-engine-1.0.0.jar
tiga-engine-solon/target/tiga-engine-solon-1.0.0.jar
tiga-engine-spring/target/tiga-engine-spring-1.0.0.jar
tiga-platform-app/target/tiga-platform-app-1.0.0.jar
```

## 🎯 下一步

构建成功后，请参考：
- [README.md](README.md) - 使用指南
- [MIGRATION.md](MIGRATION.md) - 迁移指南
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - 实施总结

---

**文档版本：** v1.0
**最后更新：** 2026-03-27
