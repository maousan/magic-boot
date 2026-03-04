@echo off
REM Magic-Boot Docker 管理脚本 (Windows版本)
REM Author: Magic-Boot Team
REM Version: 1.0.0

setlocal enabledelayedexpansion

REM 颜色代码（Windows 10+）
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM 解析命令
set COMMAND=%1
if "%COMMAND%"=="" set COMMAND=start

REM 执行对应的函数
if "%COMMAND%"=="start" goto :start
if "%COMMAND%"=="stop" goto :stop
if "%COMMAND%"=="restart" goto :restart
if "%COMMAND%"=="status" goto :status
if "%COMMAND%"=="logs" goto :logs
if "%COMMAND%"=="build" goto :build
if "%COMMAND%"=="clean" goto :clean
if "%COMMAND%"=="backup" goto :backup
if "%COMMAND%"=="help" goto :help
if "%COMMAND%"=="--help" goto :help
if "%COMMAND%"=="-h" goto :help

echo %RED%[ERROR]%NC% 未知命令: %COMMAND%
echo.
goto :help

:start
    call :print_info "检查系统环境..."
    
    where docker >nul 2>&1
    if errorlevel 1 (
        call :print_error "Docker 未安装，请先安装 Docker Desktop"
        exit /b 1
    )
    
    call :print_success "环境检查通过"
    call :print_info "创建必要的目录..."
    
    if not exist "data\magic-api" mkdir data\magic-api
    if not exist "uploads" mkdir uploads
    if not exist "logs" mkdir logs
    if not exist "db" mkdir db
    if not exist "nginx\conf.d" mkdir nginx\conf.d
    if not exist "nginx\ssl" mkdir nginx\ssl
    
    call :print_success "目录创建完成"
    call :print_info "启动 Docker 服务..."
    
    REM 检查是否有参数
    set COMPOSE_OPTS=
    if "%2"=="--with-tools" set COMPOSE_OPTS=--profile tools
    if "%2"=="--production" set COMPOSE_OPTS=--profile production
    
    docker compose %COMPOSE_OPTS% up -d
    
    call :print_success "服务启动完成！"
    echo.
    call :show_access_info
    goto :eof

:stop
    call :print_info "停止所有服务..."
    docker compose down
    call :print_success "服务已停止"
    goto :eof

:restart
    call :stop
    timeout /t 2 /nobreak >nul
    call :start %2
    goto :eof

:status
    call :print_info "服务状态："
    docker compose ps
    goto :eof

:logs
    set SERVICE=%2
    if "%SERVICE%"=="" (
        docker compose logs -f
    ) else (
        docker compose logs -f %SERVICE%
    )
    goto :eof

:build
    call :print_info "重新构建应用镜像..."
    docker compose build app --no-cache
    call :print_success "镜像构建完成"
    goto :eof

:clean
    call :print_warning "⚠️  此操作将删除所有容器、网络和数据卷！"
    set /p CONFIRM="确认继续吗？(yes/no): "
    
    if "%CONFIRM%"=="yes" (
        call :print_info "清理环境中..."
        docker compose down -v --rmi all
        call :print_success "环境清理完成"
    ) else (
        call :print_info "操作已取消"
    )
    goto :eof

:backup
    for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
    for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
    set BACKUP_FILE=backup_%mydate%_%mytime%.sql
    
    call :print_info "备份数据库到文件: %BACKUP_FILE%"
    docker compose exec -T mysql mysqldump -u root -proot123456 --single-transaction --routines --triggers magic-boot > %BACKUP_FILE%
    call :print_success "数据库备份完成: %BACKUP_FILE%"
    goto :eof

:help
    echo.
    echo Magic-Boot Docker 管理脚本
    echo.
    echo 用法: %~nx0 [命令] [选项]
    echo.
    echo 命令:
    echo     start       启动所有服务（默认）
    echo     stop        停止所有服务
    echo     restart     重启所有服务
    echo     status      查看服务状态
    echo     logs        查看日志（可指定服务名）
    echo     build       重新构建应用镜像
    echo     clean       清理所有容器和数据卷
    echo     backup      备份数据库
    echo     help        显示帮助信息
    echo.
    echo 选项:
    echo     --with-tools    包含Redis管理工具
    echo     --production    生产环境模式（包含Nginx）
    echo.
    echo 示例:
    echo     %~nx0 start                    # 启动开发环境
    echo     %~nx0 start --with-tools       # 启动开发环境+Redis管理工具
    echo     %~nx0 logs app                 # 查看应用日志
    echo     %~nx0 backup                   # 备份数据库
    echo.
    goto :eof

:print_info
    echo %BLUE%[INFO]%NC% %~1
    goto :eof

:print_success
    echo %GREEN%[SUCCESS]%NC% %~1
    goto :eof

:print_warning
    echo %YELLOW%[WARNING]%NC% %~1
    goto :eof

:print_error
    echo %RED%[ERROR]%NC% %~1
    goto :eof

:show_access_info
    echo.
    echo %GREEN%╔════════════════════════════════════════════════════════╗%NC%
    echo %GREEN%║          Magic-Boot 服务已启动成功！                    ║%NC%
    echo %GREEN%╚════════════════════════════════════════════════════════╝%NC%
    echo.
    echo %BLUE%📱 访问地址：%NC%
    echo    • 应用主页：    http://localhost:8081
    echo    • Magic-API：   http://localhost:8081/magic/web
    echo    • Druid监控：   http://localhost:8081/druid
    echo.
    echo %BLUE%🔑 登录凭证：%NC%
    echo    • Magic-API：   admin / admin123456
    echo    • Druid：       admin / 123456
    echo.
    echo %BLUE%💾 数据库连接：%NC%
    echo    • MySQL：       localhost:3306
    echo    • 数据库：      magic-boot
    echo    • 用户名：      magic
    echo    • 密码：        Magic@2026
    echo.
    echo %BLUE%🔴 Redis连接：%NC%
    echo    • 地址：        localhost:6379
    echo    • 密码：        Magic@Redis2026
    echo.
    echo %BLUE%📝 常用命令：%NC%
    echo    • 查看日志：    %~nx0 logs [服务名]
    echo    • 查看状态：    %~nx0 status
    echo    • 停止服务：    %~nx0 stop
    echo    • 备份数据库：  %~nx0 backup
    echo.
    echo %YELLOW%提示：首次启动请等待30-60秒让数据库完成初始化%NC%
    echo.
    goto :eof
