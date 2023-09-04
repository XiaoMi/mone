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
    `milog_app_id`   bigint(20) NULL DEFAULT NULL COMMENT 'milogApp表主键',
    `app`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `app_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `contacts`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `feishu_groups`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `job_id`         int(10) NULL DEFAULT NULL COMMENT '数据工厂任务Id',
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
    `milog_app_id`   bigint(20) NULL DEFAULT NULL COMMENT 'milogApp表主键',
    `app`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `app_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `contacts`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `feishu_groups`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `job_id`         int(10) NULL DEFAULT NULL COMMENT '数据工厂任务Id',
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
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `milog_app_id`  bigint(20) NOT NULL COMMENT 'milog app表主键\r\n',
    `middleware_id` bigint(20) NOT NULL COMMENT '中间件配置表ID\r\n',
    `tail_id`       bigint(20) NOT NULL COMMENT '采集日志路径tailId\r\n',
    `config`        json NULL COMMENT '配置信息，json格式',
    `ctime`         bigint(20) NOT NULL COMMENT '创建时间\r\n',
    `utime`         bigint(20) NOT NULL COMMENT '更新时间',
    `creator`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建人\r\n',
    `updater`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_app_topic_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_topic_rel`;
CREATE TABLE `milog_app_topic_rel`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`       bigint(20) NULL DEFAULT NULL COMMENT '创建时间\r\n',
    `utime`       bigint(20) NULL DEFAULT NULL COMMENT '更新时间\r\n',
    `tenant_id`   bigint(20) NULL DEFAULT NULL COMMENT '租户Id\r\n',
    `app_id`      bigint(20) NOT NULL COMMENT '应用id\r\n',
    `iam_tree_id` bigint(20) NULL DEFAULT NULL COMMENT 'mione应用Iam treeId',
    `app_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app名称\r\n',
    `operator`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作者\r\n',
    `mq_config`   json NULL COMMENT 'mq配置信息，json格式',
    `source`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'app来源\r\n',
    `type`        smallint(6) NULL DEFAULT NULL COMMENT '0.mione 项目 1.mis项目\r\n',
    `tree_ids`    json NULL COMMENT 'mis 项目的挂载的树ids',
    `node_ips`    json NULL COMMENT '应用所在的物理机ips',
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
    `tag`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '集群类型\r\n',
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '集群名称\r\n集群名称\r\n集群名称\r\n',
    `region`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '机房',
    `cluster_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '融合云上集群名',
    `addr`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES地址\r\n',
    `user`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES用户名\r\n',
    `pwd`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES密码\r\n',
    `token`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_catalog`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_database`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `area`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地区\r\n',
    `ctime`        bigint(20) NULL DEFAULT NULL COMMENT '创建时间\r\n',
    `utime`        bigint(20) NULL DEFAULT NULL COMMENT '更新时间\r\n',
    `creator`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人\r\n',
    `updater`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人\r\n',
    `labels`       json NULL COMMENT '标签',
    `con_way`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '连接方式:pwd,token',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_es_index
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index`;
CREATE TABLE `milog_es_index`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT '所属集群id\r\n',
    `log_type`   int(11) NULL DEFAULT NULL COMMENT '日志类型\r\n',
    `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es索引名',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1525057282467098526 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_es_index_online
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index_online`;
CREATE TABLE `milog_es_index_online`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT '所属集群id\r\n',
    `log_type`   int(11) NULL DEFAULT NULL COMMENT '日志类型\r\n',
    `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es索引名',
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
    `tail_id`  bigint(20) NULL DEFAULT NULL COMMENT 'tail的ID',
    `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es索引名',
    `day`      date NULL DEFAULT NULL COMMENT '日志数据产生日yyyy-MM-dd',
    `number`   bigint(20) NULL DEFAULT NULL COMMENT '日志条数',
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
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `tailId`          bigint(20) NULL DEFAULT NULL COMMENT 'tailId',
    `agent_id`        bigint(20) NULL DEFAULT NULL COMMENT 'agentId',
    `ip`              varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'ip',
    `file_row_number` int(32) NULL DEFAULT NULL COMMENT '日志文件行号',
    `pointer`         int(32) NULL DEFAULT NULL,
    `collect_time`    bigint(20) NULL DEFAULT NULL COMMENT '日志收集时间',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '日志收集进度' ROW_FORMAT = Compact;

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
    `is_fix_time` int(11) NULL DEFAULT NULL COMMENT '1-保存了时间参数；0-没有保存',
    `start_time`  bigint(20) NULL DEFAULT NULL COMMENT '搜索开始时间',
    `end_time`    bigint(20) NULL DEFAULT NULL COMMENT '搜索结束时间',
    `common`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `sort`        bigint(20) NULL DEFAULT NULL COMMENT '分类;1-搜索词,2-tail,3-store',
    `order_num`   bigint(20) NULL DEFAULT NULL COMMENT '排序',
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后更新人',
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
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ctime`         bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    `utime`         bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
    `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '日志模板名称\r\n',
    `type`          int(11) NULL DEFAULT NULL COMMENT '日志模板类型0-自定义日志;1-app;2-nginx',
    `support_area`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支持机房',
    `order_col`     int(11) NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
    `template_id`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板ID\r\n',
    `properties_key`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性名；1-必选；2-建议；3-隐藏',
    `properties_type` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性类型\r\n',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60086 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_logstail
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstail`;
CREATE TABLE `milog_logstail`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`             bigint(20) NULL DEFAULT NULL COMMENT '创建时间\r\n',
    `utime`             bigint(20) NULL DEFAULT NULL COMMENT '更新时间\r\n',
    `creator`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人\r\n',
    `updater`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人\r\n',
    `space_id`          bigint(20) NULL DEFAULT NULL COMMENT 'spaceId',
    `store_id`          bigint(20) NULL DEFAULT NULL COMMENT 'storeId',
    `tail`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用别名\r\n',
    `milog_app_id`      bigint(20) NULL DEFAULT NULL COMMENT 'milog表主键',
    `app_id`            bigint(20) NULL DEFAULT NULL COMMENT '应用id\r\n',
    `app_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用名\r\n',
    `app_type`          smallint(4) NULL DEFAULT NULL COMMENT '0.mione项目 1.mis项目',
    `machine_type`      smallint(4) NULL DEFAULT NULL COMMENT 'mis应用 机器类型 0.容器 1.物理机',
    `env_id`            int(11) NULL DEFAULT NULL COMMENT '环境Id\r\n',
    `env_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '环境名称\r\n',
    `parse_type`        int(11) NULL DEFAULT NULL COMMENT '日志解析类型：1:服务应用日志，2.分隔符，3：单行，4：多行，5：自定义',
    `parse_script`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '对于分隔符，该字段指定分隔符；对于自定义，该字段指定日志读取脚本\r\n',
    `log_path`          varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '逗号分割，多个日志文件路径,e.g.:/home/work/log/xxx/server.log\r\n',
    `log_split_express` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志切分表达式',
    `value_list`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'value列表，多个用逗号分隔\r\n',
    `ips`               json NULL COMMENT 'ip列表\r\n',
    `motor_rooms`       json NULL COMMENT 'mis 应用机房信息',
    `filter`            json NULL COMMENT 'filter配置\r\n',
    `en_es_index`       json NULL COMMENT 'mis应用索引配置',
    `deploy_way`        int(11) NULL DEFAULT NULL COMMENT '部署方式：1-mione; 2-miline; 3-k8s',
    `deploy_space`      varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'matrix服务部署空间',
    `first_line_reg`    varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '行首正则表达式',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_logstore
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstore`;
CREATE TABLE `milog_logstore`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`            bigint(20) NULL DEFAULT NULL COMMENT '创建时间\r\n',
    `utime`            bigint(20) NULL DEFAULT NULL COMMENT '更新时间\r\n',
    `space_id`         bigint(20) NOT NULL COMMENT 'spaceId',
    `logstoreName`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '日志存储名称\r\n',
    `store_period`     int(255) NULL DEFAULT NULL COMMENT '存储周期:1-3-5-7',
    `shard_cnt`        int(255) NULL DEFAULT NULL COMMENT '存储分片数\r\n',
    `key_list`         varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'key列表，多个用逗号分隔\r\n',
    `column_type_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '段类型，多个用逗号分隔\r\n',
    `log_type`         varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '1:app,2:ngx..',
    `es_index`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index:milog_logstoreName',
    `es_cluster_id`    bigint(20) NULL DEFAULT NULL,
    `machine_room`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '机房信息',
    `creator`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `mq_resource_id`   bigint(20) NULL DEFAULT NULL COMMENT '资源表中mq的Id',
    `is_matrix_app`    int(11) NULL DEFAULT 0 COMMENT '是否是matrix应用：0=false，1=true',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90092 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_matrix_esinfo
-- ----------------------------
DROP TABLE IF EXISTS `milog_matrix_esinfo`;
CREATE TABLE `milog_matrix_esinfo`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `cluster`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志配置集群',
    `es_catalog`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '集群catalog',
    `es_database` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '默认default',
    `es_token`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '查询dt用的ESToken',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_middleware_config
-- ----------------------------
DROP TABLE IF EXISTS `milog_middleware_config`;
CREATE TABLE `milog_middleware_config`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`          smallint(6) NOT NULL COMMENT '配置 1. rocketmq 2.talos',
    `region_en`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '不同的机房\r\n',
    `alias`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '别名\r\n',
    `name_server`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'nameServer地址\r\n',
    `service_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '域名\r\n',
    `ak`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ak',
    `sk`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sk',
    `token`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_catalog`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_database`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `authorization` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '授权信息(http接口请求头需要)\r\n',
    `org_id`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组织Id\r\n',
    `team_id`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户组Id\r\n',
    `is_default`    smallint(2) NULL DEFAULT 0 COMMENT '是否默认当不选择mq的时候采用这个配置(1.是 0.否)',
    `ctime`         bigint(20) NOT NULL COMMENT '创建时间\r\n',
    `utime`         bigint(20) NOT NULL COMMENT '更新时间\r\n',
    `creator`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建人\r\n',
    `updater`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '更新人\r\n',
    `labels`        json NULL COMMENT '标签',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for milog_region_zone
-- ----------------------------
DROP TABLE IF EXISTS `milog_region_zone`;
CREATE TABLE `milog_region_zone`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `region_name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region英文名',
    `region_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region中文名',
    `zone_name_en`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone英文名',
    `zone_name_cn`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone中文名',
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
    `ctime`          bigint(20) NULL DEFAULT NULL COMMENT '创建时间\r\n',
    `utime`          bigint(20) NULL DEFAULT NULL COMMENT '更新时间\r\n',
    `tenant_id`      int(20) NULL DEFAULT NULL COMMENT '租户Id\r\n',
    `space_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '项目空间名称\r\n',
    `source`         varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '来源：开源',
    `creator`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者\r\n',
    `dept_id`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者所在三级部门',
    `updater`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
    `description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注说明\r\n',
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
