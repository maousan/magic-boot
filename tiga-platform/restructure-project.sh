#!/bin/bash
# Tiga Platform - 项目结构重组脚本

set -e

echo "========================================"
echo "  Tiga Platform - 项目结构重组"
echo "========================================"
echo ""

# 检查当前目录
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误：请在项目根目录执行此脚本"
    exit 1
fi

echo "📋 当前项目结构："
echo ""
ls -la | grep -E "^d|pom.xml"
echo ""

# 创建临时目录
TEMP_DIR="temp-migration-$(date +%s)"
mkdir -p "$TEMP_DIR"

echo "📦 Step 1: 备份现有文件..."
cp -r src "$TEMP_DIR/"
cp pom.xml "$TEMP_DIR/pom.xml.original"

echo "📦 Step 2: 创建应用子模块目录..."
mkdir -p tiga-platform-app/src

echo "📦 Step 3: 移动应用代码到子模块..."
# 移动源代码
if [ -d "src" ]; then
    mv src/* tiga-platform-app/src/ 2>/dev/null || true
fi

echo "📦 Step 4: 创建应用子模块的pom.xml..."
cat > tiga-platform-app/pom.xml << 'EOL'
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
    <description>Tiga Platform Application Layer</description>

    <dependencies>
        <!-- 新的模块化引擎依赖 -->
        <dependency>
            <groupId>com.ocean.tiga</groupId>
            <artifactId>tiga-engine-solon</artifactId>
        </dependency>

        <!-- Solon Web框架 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>wood-solon-plugin</artifactId>
        </dependency>

        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>snack3</artifactId>
        </dependency>

        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-logging-simple</artifactId>
        </dependency>

        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-boot-smarthttp</artifactId>
        </dependency>

        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- 监控 -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- 数据库 -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>

        <!-- Groovy -->
        <dependency>
            <groupId>org.apache.groovy</groupId>
            <artifactId>groovy</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.groovy</groupId>
            <artifactId>groovy-json</artifactId>
        </dependency>

        <!-- 工具类 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

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

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- JavaParser -->
        <dependency>
            <groupId>com.github.javaparser</groupId>
            <artifactId>javaparser-symbol-solver-core</artifactId>
            <version>3.26.3</version>
        </dependency>

        <!-- JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2</artifactId>
        </dependency>

        <!-- Calcite SQL引擎 -->
        <dependency>
            <groupId>org.apache.calcite</groupId>
            <artifactId>calcite-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.calcite</groupId>
            <artifactId>calcite-linq4j</artifactId>
        </dependency>

        <!-- 性能测试 -->
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-core</artifactId>
            <version>1.37</version>
        </dependency>

        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-generator-annprocess</artifactId>
            <version>1.37</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.noear</groupId>
                <artifactId>solon-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOL

echo "📦 Step 5: 更新父POM，添加应用模块..."
# 检查父POM是否已包含tiga-platform-app
if ! grep -q "tiga-platform-app" pom.xml; then
    # 在modules部分添加tiga-platform-app
    if grep -q "<modules>" pom.xml; then
        # 在</modules>前添加
        sed -i 's|</modules>|        <module>tiga-platform-app</module>\n    </modules>|' pom.xml
    else
        # 如果没有modules，添加整个部分
        sed -i 's|<packaging>pom</packaging>|<packaging>pom</packaging>\n\n    <modules>\n        <module>tiga-platform-app</module>\n    </modules>|' pom.xml
    fi
fi

echo ""
echo "✅ 项目结构重组完成！"
echo ""
echo "📁 新的项目结构："
tree -L 2 -d 2>/dev/null || find . -maxdepth 2 -type d | grep -E "tiga-|src" | head -20
echo ""

echo "📝 下一步："
echo "  1. 检查tiga-platform-app/src目录"
echo "  2. 更新代码中的TigaEngineManager引用"
echo "  3. 运行构建测试：mvn clean compile"
echo ""
echo "⚠️  备份位置: $TEMP_DIR"
