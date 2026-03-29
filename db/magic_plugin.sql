-- ----------------------------
-- Table structure for magic_plugin
-- ----------------------------
DROP TABLE IF EXISTS `magic_plugin`;
CREATE TABLE `magic_plugin`
(
    `id`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL,
    `plugin_id`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `plugin_name`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `version`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL,
    `description`  varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `author`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `plugin_class` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `status`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL,
    `jar_path`     varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `dependencies` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `provider`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `package_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL,
    `package_checksum` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `manifest_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `manifest_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `requires_magic_boot` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `install_source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `install_time` datetime NULL DEFAULT NULL,
    `create_time`  datetime                                                       NULL DEFAULT NULL,
    `update_time`  datetime                                                       NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_plugin_id` (`plugin_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;


-- 鎻掍欢閰嶇疆琛?
DROP TABLE IF EXISTS `magic_plugin_config`;
CREATE TABLE magic_plugin_config
(
    id           varchar(64) PRIMARY KEY,
    plugin_id    varchar(128),
    config_key   varchar(128),
    config_value text
);

-- 鎻掍欢鏃ュ織琛?
DROP TABLE IF EXISTS `magic_plugin_log`;
CREATE TABLE magic_plugin_log
(
    id          bigint AUTO_INCREMENT PRIMARY KEY,
    plugin_id   varchar(128),
    level       varchar(16),
    message     text,
    create_time datetime
);
