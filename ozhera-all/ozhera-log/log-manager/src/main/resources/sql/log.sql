/*
 Navicat Premium Data Transfer

 Source Server         : milog
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 127.0.0.1:4100
 Source Schema         : milog

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 29/11/2022 11:36:26
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert
-- ----------------------------
DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert`
(
    `id`             bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`           varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `type`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `milog_app_id`   bigint(20) NULL DEFAULT NULL COMMENT 'milogApp table primary key',
    `app`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `app_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `contacts`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `feishu_groups`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `job_id`         int(10) NULL DEFAULT NULL COMMENT 'task Id',
    `flink_job_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `arguments`      json NULL,
    `status`         tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
    `flink_cluster`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`          bigint(64) NOT NULL,
    `utime`          bigint(64) NOT NULL,
    `creator`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `alert_app_IDX`(`app`) USING BTREE,
    INDEX            `alert_name_IDX`(`name`) USING BTREE,
    INDEX            `alert_app_name_IDX`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for alert_condition
-- ----------------------------
DROP TABLE IF EXISTS `alert_condition`;
CREATE TABLE `alert_condition`
(
    `id`              bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `alert_rule_id`   bigint(20) UNSIGNED NOT NULL,
    `operation`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `value`           int(10) UNSIGNED NOT NULL DEFAULT 0,
    `alert_level`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `period`          bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `sort_order`      bigint(20) UNSIGNED NULL DEFAULT 0,
    `send_alert_time` bigint(20) UNSIGNED NULL DEFAULT 0,
    `ctime`           bigint(20) UNSIGNED NOT NULL,
    `utime`           bigint(20) UNSIGNED NOT NULL,
    `creator`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for alert_copy1
-- ----------------------------
DROP TABLE IF EXISTS `alert_copy1`;
CREATE TABLE `alert_copy1`
(
    `id`             bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`           varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `type`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `milog_app_id`   bigint(20) NULL DEFAULT NULL COMMENT 'milogApp table primary key',
    `app`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `app_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `contacts`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `feishu_groups`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `job_id`         int(10) NULL DEFAULT NULL COMMENT 'task Id',
    `flink_job_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `arguments`      json NULL,
    `status`         tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
    `flink_cluster`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`          bigint(64) NOT NULL,
    `utime`          bigint(64) NOT NULL,
    `creator`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `alert_app_IDX`(`app`) USING BTREE,
    INDEX            `alert_name_IDX`(`name`) USING BTREE,
    INDEX            `alert_app_name_IDX`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for alert_log
-- ----------------------------
DROP TABLE IF EXISTS `alert_log`;
CREATE TABLE `alert_log`
(
    `id`          bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `alert_id`    bigint(20) UNSIGNED NOT NULL,
    `app_name`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `start_time`  bigint(20) UNSIGNED NULL DEFAULT 0,
    `end_time`    bigint(20) UNSIGNED NULL DEFAULT 0,
    `ip`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`       bigint(20) UNSIGNED NOT NULL,
    `utime`       bigint(20) UNSIGNED NOT NULL,
    `alert_count` int(11) NULL DEFAULT 0,
    `alert_level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `content`     varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `alert_log_app_name_IDX`(`app_name`) USING BTREE,
    INDEX         `alert_log_ip_IDX`(`ip`) USING BTREE,
    INDEX         `alert_log_app_name_ip__IDX`(`app_name`, `ip`) USING BTREE,
    INDEX         `alert_log_start_time_IDX`(`start_time`) USING BTREE,
    INDEX         `alert_log_app_name_ip_starttime_IDX`(`app_name`, `ip`, `start_time`) USING BTREE,
    INDEX         `alert_log_app_name_starttime_IDX`(`app_name`, `start_time`) USING BTREE,
    INDEX         `alert_log_ip_starttime_IDX`(`ip`, `start_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 121131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule`
(
    `id`       bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `alert_id` bigint(20) UNSIGNED NOT NULL,
    `regex`    varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`    bigint(20) UNSIGNED NOT NULL,
    `utime`    bigint(20) UNSIGNED NOT NULL,
    `creator`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `name`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'none',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX      `alert_rule_alert_id_IDX`(`alert_id`) USING HASH
) ENGINE = InnoDB AUTO_INCREMENT = 90004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for mibench_task
-- ----------------------------
DROP TABLE IF EXISTS `mibench_task`;
CREATE TABLE `mibench_task`
(
    `id`               int(32) NOT NULL AUTO_INCREMENT,
    `qps`              int(32) NULL DEFAULT NULL,
    `taskDefinitionId` int(32) NULL DEFAULT NULL,
    `time`             int(32) NULL DEFAULT NULL,
    `agentNum`         int(32) NULL DEFAULT NULL,
    `finishAgentNum`   int(32) NULL DEFAULT NULL,
    `ctime`            bigint(64) NULL DEFAULT NULL,
    `utime`            bigint(64) NULL DEFAULT NULL,
    `state`            int(32) NULL DEFAULT NULL,
    `version`          int(32) NULL DEFAULT NULL,
    `successNum`       bigint(64) NULL DEFAULT NULL,
    `failureNum`       bigint(64) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for mibench_task_definition
-- ----------------------------
DROP TABLE IF EXISTS `mibench_task_definition`;
CREATE TABLE `mibench_task_definition`
(
    `id`       int(32) NOT NULL AUTO_INCREMENT,
    `name`     varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `describe` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `request`  json NULL,
    `result`   text CHARACTER SET utf8 COLLATE utf8_bin NULL,
    `ctime`    bigint(64) NULL DEFAULT NULL,
    `utime`    bigint(64) NULL DEFAULT NULL,
    `state`    int(32) NULL DEFAULT NULL,
    `qps`      int(32) NULL DEFAULT NULL,
    `time`     int(32) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_analyse_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_dashboard`;
CREATE TABLE `milog_analyse_dashboard`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `store_id`    bigint(20) NULL DEFAULT NULL,
    `space_id`    bigint(20) NULL DEFAULT NULL,
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_analyse_dashboard_graph_ref
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_dashboard_graph_ref`;
CREATE TABLE `milog_analyse_dashboard_graph_ref`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `dashboard_id` bigint(20) NULL DEFAULT NULL,
    `graph_id`     bigint(20) NULL DEFAULT NULL,
    `point`        json NULL,
    `private_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30012 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_analyse_graph
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_graph`;
CREATE TABLE `milog_analyse_graph`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `field_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `space_id`    bigint(20) NULL DEFAULT NULL,
    `store_id`    bigint(20) NULL DEFAULT NULL,
    `graph_type`  int(11) NULL DEFAULT NULL,
    `graph_param` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_analyse_graph_type
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_graph_type`;
CREATE TABLE `milog_analyse_graph_type`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `type`      int(11) NULL DEFAULT NULL,
    `calculate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `classify`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_app_middleware_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_middleware_rel`;
CREATE TABLE `milog_app_middleware_rel`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `milog_app_id`  bigint(20) NOT NULL COMMENT 'milog app table primary key',
    `middleware_id` bigint(20) NOT NULL COMMENT 'Middleware configuration table ID',
    `tail_id`       bigint(20) NOT NULL COMMENT 'Collection log path tail ID',
    `config`        json NULL COMMENT 'Configuration information, json format',
    `ctime`         bigint(20) NOT NULL COMMENT 'create time',
    `utime`         bigint(20) NOT NULL COMMENT 'update time',
    `creator`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'creator',
    `updater`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'updater',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_app_topic_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_topic_rel`;
CREATE TABLE `milog_app_topic_rel`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`       bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`       bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `tenant_id`   bigint(20) NULL DEFAULT NULL COMMENT 'TenantId',
    `app_id`      bigint(20) NOT NULL COMMENT 'app id',
    `iam_tree_id` bigint(20) NULL DEFAULT NULL COMMENT 'mione appIam treeId',
    `app_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app name',
    `operator`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'operator',
    `mq_config`   json NULL COMMENT 'mq configuration information, json format',
    `source`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'app source',
    `type`        smallint(6) NULL DEFAULT NULL COMMENT '0.mione project 1.mis project',
    `tree_ids`    json NULL COMMENT 'The project''s mounted tree ids',
    `node_ips`    json NULL COMMENT 'The physical machine ips where the application is located',
    `creator`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92629 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_es_cluster
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_cluster`;
CREATE TABLE `milog_es_cluster`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `tag`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Cluster type',
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Cluster name',
    `region`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'engine room',
    `cluster_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Fusion cloud cluster name',
    `addr`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES address',
    `user`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES username',
    `pwd`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES password',
    `token`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_catalog`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_database`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `area`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'area',
    `ctime`        bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`        bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `creator`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `labels`       json NULL COMMENT 'labels',
    `con_way`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Connection method: pwd, token',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_es_index
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index`;
CREATE TABLE `milog_es_index`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'The cluster id to which it belongs',
    `log_type`   int(11) NULL DEFAULT NULL COMMENT 'log type',
    `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1525057282467098526 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_es_index_online
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index_online`;
CREATE TABLE `milog_es_index_online`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'The cluster id to which it belongs',
    `log_type`   int(11) NULL DEFAULT NULL COMMENT 'Log type',
    `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_lock
-- ----------------------------
DROP TABLE IF EXISTS `milog_lock`;
CREATE TABLE `milog_lock`
(
    `id`     bigint(20) NOT NULL,
    `ctime`  bigint(20) NULL DEFAULT NULL,
    `utime`  bigint(20) NULL DEFAULT NULL,
    `code`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `status` int(11) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_count
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_count`;
CREATE TABLE `milog_log_count`
(
    `id`       bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `tail_id`  bigint(20) NULL DEFAULT NULL COMMENT 'tail id',
    `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
    `day`      date NULL DEFAULT NULL COMMENT 'Log data generation date yyyy-MM-dd',
    `number`   bigint(20) NULL DEFAULT NULL COMMENT 'Number of logs',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 191782 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_num_alert
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_num_alert`;
CREATE TABLE `milog_log_num_alert`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `day`        date NULL DEFAULT NULL,
    `number`     bigint(20) NULL DEFAULT NULL,
    `app_id`     bigint(20) NULL DEFAULT NULL,
    `app_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `alert_user` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`      bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX        `day+appId`(`day`, `app_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_process
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_process`;
CREATE TABLE `milog_log_process`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary keyId',
    `tailId`          bigint(20) NULL DEFAULT NULL COMMENT 'tailId',
    `agent_id`        bigint(20) NULL DEFAULT NULL COMMENT 'agentId',
    `ip`              varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'ip',
    `file_row_number` int(32) NULL DEFAULT NULL COMMENT 'Log file line number',
    `pointer`         int(32) NULL DEFAULT NULL,
    `collect_time`    bigint(20) NULL DEFAULT NULL COMMENT 'Log collection time',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT 'creation time',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT 'Update time',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'Log collection progress' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_search_save
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_search_save`;
CREATE TABLE `milog_log_search_save`
(
    `id`          bigint(20) NOT NULL,
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `space_id`    int(11) NULL DEFAULT NULL,
    `store_id`    bigint(20) NULL DEFAULT NULL,
    `tail_id`     int(11) NULL DEFAULT NULL,
    `query_text`  varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `is_fix_time` int(11) NULL DEFAULT NULL COMMENT '1-The time parameter is saved; 0-It is not saved',
    `start_time`  bigint(20) NULL DEFAULT NULL COMMENT 'Search start time',
    `end_time`    bigint(20) NULL DEFAULT NULL COMMENT 'Search end time',
    `common`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Remark',
    `sort`        bigint(20) NULL DEFAULT NULL COMMENT 'Classification;1-search word,2-tail,3-store',
    `order_num`   bigint(20) NULL DEFAULT NULL COMMENT 'sort',
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template`;
CREATE TABLE `milog_log_template`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `ctime`         bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`         bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'log template name',
    `type`          int(11) NULL DEFAULT NULL COMMENT 'Log template type 0-custom log;1-app;2-nginx',
    `support_area`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Support computer room',
    `order_col`     int(11) NULL DEFAULT NULL COMMENT 'sort',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `template_id`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template ID',
    `properties_key`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template attribute name; 1-required; 2-suggestion; 3-hidden',
    `properties_type` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template attribute type',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60086 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_logstail
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstail`;
CREATE TABLE `milog_logstail`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`             bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`             bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `creator`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `space_id`          bigint(20) NULL DEFAULT NULL COMMENT 'spaceId',
    `store_id`          bigint(20) NULL DEFAULT NULL COMMENT 'storeId',
    `tail`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app alias',
    `milog_app_id`      bigint(20) NULL DEFAULT NULL COMMENT 'milog table primary key',
    `app_id`            bigint(20) NULL DEFAULT NULL COMMENT 'app id',
    `app_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app name',
    `app_type`          smallint(4) NULL DEFAULT NULL COMMENT '0.mione project ',
    `machine_type`      smallint(4) NULL DEFAULT NULL COMMENT 'Machine type 0.Container 1.Physical machine',
    `env_id`            int(11) NULL DEFAULT NULL COMMENT 'environment id',
    `env_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'environment name',
    `parse_type`        int(11) NULL DEFAULT NULL COMMENT 'Log parsing type: 1: Service application log, 2. Delimiter, 3: Single line, 4: Multi-line, 5: Custom',
    `parse_script`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'For Delimiter, this field specifies the delimiter; for Custom, this field specifies the log reading script',
    `log_path`          varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Comma separated, multiple log file paths,e.g.:/home/work/log/xxx/server.log',
    `log_split_express` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'log splitting expression',
    `value_list`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'value list, multiple comma-separated',
    `ips`               json NULL COMMENT 'ip list',
    `motor_rooms`       json NULL COMMENT 'Application room information',
    `filter`            json NULL COMMENT 'filter config',
    `en_es_index`       json NULL COMMENT 'app index configuration',
    `deploy_way`        int(11) NULL DEFAULT NULL COMMENT 'deployment type：1-mione; 2-miline; 3-k8s',
    `deploy_space`      varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'service deployment space',
    `first_line_reg`    varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Beginning of line regular expression',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_logstore
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstore`;
CREATE TABLE `milog_logstore`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`            bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`            bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `space_id`         bigint(20) NOT NULL COMMENT 'spaceId',
    `logstoreName`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'log store name',
    `store_period`     int(255) NULL DEFAULT NULL COMMENT 'Storage period:1-3-5-7',
    `shard_cnt`        int(255) NULL DEFAULT NULL COMMENT 'Number of storage shards',
    `key_list`         varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Key list, multiple separated by commas',
    `column_type_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Segment type, multiple separated by commas',
    `log_type`         varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '1:app,2:ngx..',
    `es_index`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index:milog_logstoreName',
    `es_cluster_id`    bigint(20) NULL DEFAULT NULL,
    `machine_room`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Computer room information',
    `creator`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `mq_resource_id`   bigint(20) NULL DEFAULT NULL COMMENT 'The Id of mq in the resource table',
    `is_matrix_app`    int(11) NULL DEFAULT 0 COMMENT 'Whether it is a matrix application: 0=false, 1=true',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90092 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_matrix_esinfo
-- ----------------------------
DROP TABLE IF EXISTS `milog_matrix_esinfo`;
CREATE TABLE `milog_matrix_esinfo`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `cluster`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log configuration cluster',
    `es_catalog`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Cluster catalog',
    `es_database` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'default',
    `es_token`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Query the ES Token used by dt',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_middleware_config
-- ----------------------------
DROP TABLE IF EXISTS `milog_middleware_config`;
CREATE TABLE `milog_middleware_config`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `type`          smallint(6) NOT NULL COMMENT 'config 1. rocketmq 2.talos',
    `region_en`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Different computer rooms',
    `alias`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'alias',
    `name_server`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'nameServer address',
    `service_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'domain name',
    `ak`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ak',
    `sk`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sk',
    `token`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_catalog`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_database`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `authorization` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Authorization information (required by http interface request header)',
    `org_id`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'OrganizationId',
    `team_id`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'User group ID',
    `is_default`    smallint(2) NULL DEFAULT 0 COMMENT 'Whether to use this configuration by default when mq is not selected (1. Yes 0. No)',
    `ctime`         bigint(20) NOT NULL COMMENT 'create time',
    `utime`         bigint(20) NOT NULL COMMENT 'update time',
    `creator`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'creator',
    `updater`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'updater',
    `labels`        json NULL COMMENT 'labels',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_region_zone
-- ----------------------------
DROP TABLE IF EXISTS `milog_region_zone`;
CREATE TABLE `milog_region_zone`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `region_name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region en',
    `region_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region cn',
    `zone_name_en`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone en',
    `zone_name_cn`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone cn',
    `ctime`          bigint(20) NULL DEFAULT NULL,
    `utime`          bigint(20) NULL DEFAULT NULL,
    `creator`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_space
-- ----------------------------
DROP TABLE IF EXISTS `milog_space`;
CREATE TABLE `milog_space`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`          bigint(20) NULL DEFAULT NULL COMMENT 'create time',
    `utime`          bigint(20) NULL DEFAULT NULL COMMENT 'update time',
    `tenant_id`      int(20) NULL DEFAULT NULL COMMENT 'TenantId',
    `space_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Project space name',
    `source`         varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Source: open source',
    `creator`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `dept_id`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'The creator’s third-level department',
    `updater`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'instruction manual',
    `create_dept_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `perm_dept_id`   varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_store_space_auth
-- ----------------------------
DROP TABLE IF EXISTS `milog_store_space_auth`;
CREATE TABLE `milog_store_space_auth`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `store_id` bigint(20) NOT NULL,
    `space_id` bigint(20) NOT NULL,
    `ctime`    bigint(20) NOT NULL,
    `utime`    bigint(20) NULL DEFAULT NULL,
    `creator`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `updater`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `description`  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `doc_link`     varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `owner`        varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `update_time`  bigint(20) NULL DEFAULT NULL,
    `create_time`  bigint(20) NULL DEFAULT NULL,
    `order_number` bigint(255) NULL DEFAULT NULL,
    `is_top`       int(11) NULL DEFAULT NULL,
    `is_key`       int(11) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

SET
FOREIGN_KEY_CHECKS = 1;
