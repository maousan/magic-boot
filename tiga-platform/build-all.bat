@echo off
REM Tiga Platform - 快速构建脚本（Windows）

echo ========================================
echo   Tiga Platform - 模块化构建
echo ========================================
echo.

REM 检查Maven是否安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误：未找到Maven，请先安装Maven 3.6+
    exit /b 1
)

echo ✅ Maven版本：
mvn -version | findstr "Apache Maven"
echo.

REM 构建核心引擎模块
echo [1/4] 📦 构建核心引擎模块 (tiga-engine)...
cd tiga-engine
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ tiga-engine 构建失败
    exit /b 1
)
cd ..
echo ✅ tiga-engine 构建完成
echo.

REM 构建Solon集成模块
echo [2/4] 📦 构建Solon集成模块 (tiga-engine-solon)...
cd tiga-engine-solon
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ tiga-engine-solon 构建失败
    exit /b 1
)
cd ..
echo ✅ tiga-engine-solon 构建完成
echo.

REM 构建Spring Boot集成模块
echo [3/4] 📦 构建Spring Boot集成模块 (tiga-engine-spring)...
cd tiga-engine-spring
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ❌ tiga-engine-spring 构建失败
    exit /b 1
)
cd ..
echo ✅ tiga-engine-spring 构建完成
echo.

REM 检查是否需要构建应用模块
if exist "tiga-platform-app" (
    echo [4/4] 📦 构建应用模块 (tiga-platform-app)...
    cd tiga-platform-app
    call mvn clean install -DskipTests
    if %errorlevel% neq 0 (
        echo ❌ tiga-platform-app 构建失败
        exit /b 1
    )
    cd ..
    echo ✅ tiga-platform-app 构建完成
) else (
    echo [4/4] ⏭️  跳过应用模块构建（tiga-platform-app目录不存在）
)

echo.
echo ========================================
echo   🎉 所有模块构建成功！
echo ========================================
echo.
echo 生成的JAR文件：
dir /s /b *.jar | findstr target | findstr /v original

echo.
echo 下一步：
echo   - 阅读 README.md 了解如何使用
echo   - 阅读 MIGRATION.md 了解如何迁移
echo   - 运行测试：mvn test
