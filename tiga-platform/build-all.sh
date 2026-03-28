#!/bin/bash
# Tiga Platform - 快速构建脚本（Linux/Mac）

set -e

echo "========================================"
echo "  Tiga Platform - 模块化构建"
echo "========================================"
echo ""

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：未找到Maven，请先安装Maven 3.6+"
    exit 1
fi

echo "✅ Maven版本："
mvn -version | head -1
echo ""

# 构建核心引擎模块
echo "[1/4] 📦 构建核心引擎模块 (tiga-engine)..."
cd tiga-engine
mvn clean install -DskipTests
cd ..
echo "✅ tiga-engine 构建完成"
echo ""

# 构建Solon集成模块
echo "[2/4] 📦 构建Solon集成模块 (tiga-engine-solon)..."
cd tiga-engine-solon
mvn clean install -DskipTests
cd ..
echo "✅ tiga-engine-solon 构建完成"
echo ""

# 构建Spring Boot集成模块
echo "[3/4] 📦 构建Spring Boot集成模块 (tiga-engine-spring)..."
cd tiga-engine-spring
mvn clean install -DskipTests
cd ..
echo "✅ tiga-engine-spring 构建完成"
echo ""

# 检查是否需要构建应用模块
if [ -d "tiga-platform-app" ]; then
    echo "[4/4] 📦 构建应用模块 (tiga-platform-app)..."
    cd tiga-platform-app
    mvn clean install -DskipTests
    cd ..
    echo "✅ tiga-platform-app 构建完成"
else
    echo "[4/4] ⏭️  跳过应用模块构建（tiga-platform-app目录不存在）"
fi

echo ""
echo "========================================"
echo "  🎉 所有模块构建成功！"
echo "========================================"
echo ""
echo "生成的JAR文件："
find . -name "*.jar" -path "*/target/*" -type f | grep -v original | sort

echo ""
echo "下一步："
echo "  - 阅读 README.md 了解如何使用"
echo "  - 阅读 MIGRATION.md 了解如何迁移"
echo "  - 运行测试：mvn test"
