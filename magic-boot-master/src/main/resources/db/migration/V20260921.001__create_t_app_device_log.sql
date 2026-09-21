-- PDA App 运行日志上传：索引表（配套脚本 data/dongxinheping/api/东信和平/App管理/日志管理/上传运行日志.ms）
-- 接口：POST /api/app/log/upload（multipart：file=日志zip + deviceId + appVersion，无鉴权、无重试补传）
-- zip 原始包落盘 {upload.dir}/app-log/{deviceId}/{deviceId}-{yyyyMMdd-HHmmss}.zip，本表仅作索引；
-- upload.dir 默认 D:/mb/，app-log 目录不在 userfiles 公网映射内，日志含内网报文不提供下载
-- 幂等：CREATE TABLE IF NOT EXISTS，可重复执行；Flyway 在 dongxinheping 部署关闭，本目录仅作 DDL 存档
-- 2026-09-21 已在 192.168.2.90 执行；现场生产库需另行执行

CREATE TABLE IF NOT EXISTS t_app_device_log (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    device_id varchar(64) NOT NULL COMMENT '设备唯一ID(App首次启动生成并持久化,白名单[A-Za-z0-9_-])',
    app_version varchar(32) DEFAULT NULL COMMENT 'App展示用版本号',
    file_name varchar(128) DEFAULT NULL COMMENT '落盘文件名 {deviceId}-{yyyyMMdd-HHmmss}.zip',
    file_size bigint DEFAULT NULL COMMENT 'zip字节数',
    uploaded_at datetime NOT NULL COMMENT '服务端接收时间',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '索引行创建时间',
    PRIMARY KEY (id),
    KEY idx_device_uploaded_at (device_id, uploaded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='PDA App运行日志上传索引(东信和平)';

-- 保留策略在脚本内实现：按设备保留最近 20 个包（超限删文件+删索引行）
