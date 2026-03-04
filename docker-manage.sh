#!/bin/bash

# Magic-Boot Docker 一键部署脚本
# Author: Magic-Boot Team
# Version: 1.0.0

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示帮助信息
show_help() {
    cat << EOF
Magic-Boot Docker 管理脚本

用法: $0 [命令] [选项]

命令:
    start       启动所有服务（默认）
    stop        停止所有服务
    restart     重启所有服务
    status      查看服务状态
    logs        查看日志（可指定服务名）
    build       重新构建应用镜像
    clean       清理所有容器和数据卷
    backup      备份数据库
    restore     恢复数据库
    help        显示帮助信息

选项:
    --with-tools    包含Redis管理工具
    --production    生产环境模式（包含Nginx）

示例:
    $0 start                    # 启动开发环境
    $0 start --with-tools       # 启动开发环境+Redis管理工具
    $0 start --production       # 启动生产环境
    $0 logs app                 # 查看应用日志
    $0 backup                   # 备份数据库
    $0 clean                    # 清理所有数据

EOF
}

# 检查Docker和Docker Compose
check_requirements() {
    log_info "检查系统环境..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    log_success "环境检查通过"
}

# 创建必要的目录
create_directories() {
    log_info "创建必要的目录..."
    
    mkdir -p data/magic-api
    mkdir -p uploads
    mkdir -p logs
    mkdir -p db
    mkdir -p nginx/conf.d
    mkdir -p nginx/ssl
    
    log_success "目录创建完成"
}

# 启动服务
start_services() {
    local profile=""
    
    if [[ "$*" == *"--with-tools"* ]]; then
        profile="--profile tools"
        log_info "将启动 Redis 管理工具"
    fi
    
    if [[ "$*" == *"--production"* ]]; then
        profile="--profile production"
        log_info "将启动生产环境配置（包含Nginx）"
    fi
    
    check_requirements
    create_directories
    
    log_info "启动 Docker 服务..."
    docker compose $profile up -d
    
    log_success "服务启动完成！"
    echo ""
    show_access_info
}

# 停止服务
stop_services() {
    log_info "停止所有服务..."
    docker compose down
    log_success "服务已停止"
}

# 重启服务
restart_services() {
    stop_services
    echo ""
    start_services "$@"
}

# 查看状态
show_status() {
    log_info "服务状态："
    docker compose ps
}

# 查看日志
show_logs() {
    local service=$1
    
    if [ -z "$service" ]; then
        docker compose logs -f
    else
        docker compose logs -f "$service"
    fi
}

# 构建镜像
build_image() {
    log_info "重新构建应用镜像..."
    docker compose build app --no-cache
    log_success "镜像构建完成"
}

# 清理环境
clean_environment() {
    log_warning "⚠️  此操作将删除所有容器、网络和数据卷！"
    read -p "确认继续吗？(yes/no): " confirm
    
    if [ "$confirm" == "yes" ]; then
        log_info "清理环境中..."
        docker compose down -v --rmi all
        log_success "环境清理完成"
    else
        log_info "操作已取消"
    fi
}

# 备份数据库
backup_database() {
    local backup_file="backup_$(date +%Y%m%d_%H%M%S).sql"
    
    log_info "备份数据库到文件: $backup_file"
    docker compose exec -T mysql mysqldump \
        -u root -proot123456 \
        --single-transaction \
        --routines \
        --triggers \
        magic-boot > "$backup_file"
    
    log_success "数据库备份完成: $backup_file"
}

# 恢复数据库
restore_database() {
    local backup_file=$1
    
    if [ -z "$backup_file" ]; then
        log_error "请指定备份文件路径"
        echo "用法: $0 restore <backup_file.sql>"
        exit 1
    fi
    
    if [ ! -f "$backup_file" ]; then
        log_error "备份文件不存在: $backup_file"
        exit 1
    fi
    
    log_warning "⚠️  此操作将覆盖当前数据库！"
    read -p "确认继续吗？(yes/no): " confirm
    
    if [ "$confirm" == "yes" ]; then
        log_info "恢复数据库..."
        docker compose exec -T mysql mysql \
            -u root -proot123456 \
            magic-boot < "$backup_file"
        log_success "数据库恢复完成"
    else
        log_info "操作已取消"
    fi
}

# 显示访问信息
show_access_info() {
    cat << EOF

${GREEN}╔════════════════════════════════════════════════════════╗${NC}
${GREEN}║          Magic-Boot 服务已启动成功！                    ║${NC}
${GREEN}╚════════════════════════════════════════════════════════╝${NC}

${BLUE}📱 访问地址：${NC}
   • 应用主页：    http://localhost:8081
   • Magic-API：   http://localhost:8081/magic/web
   • Druid监控：   http://localhost:8081/druid

${BLUE}🔑 登录凭证：${NC}
   • Magic-API：   admin / admin123456
   • Druid：       admin / 123456

${BLUE}💾 数据库连接：${NC}
   • MySQL：       localhost:3306
   • 数据库：      magic-boot
   • 用户名：      magic
   • 密码：        Magic@2026

${BLUE}🔴 Redis连接：${NC}
   • 地址：        localhost:6379
   • 密码：        Magic@Redis2026

${BLUE}📝 常用命令：${NC}
   • 查看日志：    ./docker-manage.sh logs [服务名]
   • 查看状态：    ./docker-manage.sh status
   • 停止服务：    ./docker-manage.sh stop
   • 备份数据库：  ./docker-manage.sh backup

${YELLOW}提示：首次启动请等待30-60秒让数据库完成初始化${NC}

EOF
}

# 主函数
main() {
    local command=${1:-start}
    
    case "$command" in
        start)
            start_services "$@"
            ;;
        stop)
            stop_services
            ;;
        restart)
            shift
            restart_services "$@"
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs "$2"
            ;;
        build)
            build_image
            ;;
        clean)
            clean_environment
            ;;
        backup)
            backup_database
            ;;
        restore)
            restore_database "$2"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "未知命令: $command"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
