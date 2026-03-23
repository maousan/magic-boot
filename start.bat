@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: ============================================
:: Magic-Boot 服务启动脚本
:: ============================================

set "APP_NAME=Magic-Boot"
set "MAIN_MODULE=magic-boot-master"
set "JAR_FILE=%MAIN_MODULE%/target/magic-boot.jar"

:: 解析参数
set "PROFILE="
set "SKIP_TESTS=true"
set "BUILD_FIRST=true"

:parse_args
if "%~1"=="" goto :end_parse
if /i "%~1"=="-p" (
    set "PROFILE=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="--profile" (
    set "PROFILE=%~2"
    shift
    shift
    goto :parse_args
)
if /i "%~1"=="-b" (
    set "BUILD_FIRST=true"
    shift
    goto :parse_args
)
if /i "%~1"=="--build" (
    set "BUILD_FIRST=true"
    shift
    goto :parse_args
)
if /i "%~1"=="-nb" (
    set "BUILD_FIRST=false"
    shift
    goto :parse_args
)
if /i "%~1"=="--no-build" (
    set "BUILD_FIRST=false"
    shift
    goto :parse_args
)
if /i "%~1"=="-h" goto :show_help
if /i "%~1"=="--help" goto :show_help
echo 未知参数: %~1
goto :show_help

:end_parse

echo.
echo ========================================
echo   %APP_NAME% 服务启动
echo ========================================
if defined PROFILE (
    echo   Profile: %PROFILE%
) else (
    echo   Profile: 未指定
)
echo   Build: %BUILD_FIRST%
echo ========================================
echo.

:: 切换到脚本所在目录（项目根目录）
cd /d "%~dp0"

:: 如果需要先构建
if "%BUILD_FIRST%"=="true" (
    echo [构建] 正在构建项目...
    if "%SKIP_TESTS%"=="true" (
        call mvn clean package -DskipTests -pl %MAIN_MODULE% -am
    ) else (
        call mvn clean package -pl %MAIN_MODULE% -am
    )
    if errorlevel 1 (
        echo [错误] 构建失败
        exit /b 1
    )
    echo [构建] 完成
    echo.
)

:: 检查 JAR 文件是否存在
if not exist "%JAR_FILE%" (
    echo [错误] 未找到 JAR 文件: %JAR_FILE%
    echo [提示] 请使用 -b 参数先构建项目
    exit /b 1
)

:: 启动服务
echo [启动] 正在启动服务...
if defined PROFILE (
    java -jar "%JAR_FILE%" --spring.profiles.active=%PROFILE%
) else (
    java -jar "%JAR_FILE%"
)

goto :eof

:show_help
echo.
echo 用法: %~nx0 [选项]
echo.
echo 选项:
echo   -p, --profile PROFILE   指定运行环境 (dev/demo/online)
echo   -b, --build             启动前先构建项目 (默认)
echo   -nb, --no-build         跳过构建，直接启动
echo   -h, --help              显示帮助信息
echo.
echo 示例:
echo   %~nx0                     构建并启动 (默认 profile)
echo   %~nx0 -p dev              构建并使用 dev profile
echo   %~nx0 -p demo             构建并使用 demo profile
echo   %~nx0 -nb                 跳过构建直接启动
echo   %~nx0 -p online -b        构建后使用 online profile
echo.
exit /b 0
