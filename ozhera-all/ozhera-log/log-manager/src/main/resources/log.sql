

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert
-- ----------------------------
DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `milog_app_id` bigint(0) NULL DEFAULT NULL COMMENT 'milogApp table primary key',
  `app` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `log_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `contacts` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `feishu_groups` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `job_id` int(0) NULL DEFAULT NULL COMMENT 'task Id',
  `flink_job_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `arguments` json NULL,
  `status` tinyint(0) UNSIGNED NOT NULL DEFAULT 0,
  `flink_cluster` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ctime` bigint(0) NOT NULL,
  `utime` bigint(0) NOT NULL,
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `alert_app_IDX`(`app`) USING BTREE,
  INDEX `alert_name_IDX`(`name`) USING BTREE,
  INDEX `alert_app_name_IDX`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alert_condition
-- ----------------------------
DROP TABLE IF EXISTS `alert_condition`;
CREATE TABLE `alert_condition`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `alert_rule_id` bigint(0) UNSIGNED NOT NULL,
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `value` int(0) UNSIGNED NOT NULL DEFAULT 0,
  `alert_level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `period` bigint(0) UNSIGNED NOT NULL DEFAULT 0,
  `sort_order` bigint(0) UNSIGNED NULL DEFAULT 0,
  `send_alert_time` bigint(0) UNSIGNED NULL DEFAULT 0,
  `ctime` bigint(0) UNSIGNED NOT NULL,
  `utime` bigint(0) UNSIGNED NOT NULL,
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alert_log
-- ----------------------------
DROP TABLE IF EXISTS `alert_log`;
CREATE TABLE `alert_log`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `alert_id` bigint(0) UNSIGNED NOT NULL,
  `app_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `start_time` bigint(0) UNSIGNED NULL DEFAULT 0,
  `end_time` bigint(0) UNSIGNED NULL DEFAULT 0,
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ctime` bigint(0) UNSIGNED NOT NULL,
  `utime` bigint(0) UNSIGNED NOT NULL,
  `alert_count` int(0) NULL DEFAULT 0,
  `alert_level` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `log_path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `alert_log_app_name_IDX`(`app_name`) USING BTREE,
  INDEX `alert_log_ip_IDX`(`ip`) USING BTREE,
  INDEX `alert_log_app_name_ip__IDX`(`app_name`, `ip`) USING BTREE,
  INDEX `alert_log_start_time_IDX`(`start_time`) USING BTREE,
  INDEX `alert_log_app_name_ip_starttime_IDX`(`app_name`, `ip`, `start_time`) USING BTREE,
  INDEX `alert_log_app_name_starttime_IDX`(`app_name`, `start_time`) USING BTREE,
  INDEX `alert_log_ip_starttime_IDX`(`ip`, `start_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120935 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `alert_id` bigint(0) UNSIGNED NOT NULL,
  `regex` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ctime` bigint(0) UNSIGNED NOT NULL,
  `utime` bigint(0) UNSIGNED NOT NULL,
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'none',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `alert_rule_alert_id_IDX`(`alert_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hera_app_base_info
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_base_info`;
CREATE TABLE `hera_app_base_info`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `bind_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Binding id',
  `bind_type` int(0) NOT NULL COMMENT 'Binding type (0 appId 1 iamTreeId)',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app name',
  `app_cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app cn name',
  `app_type` int(0) NOT NULL COMMENT 'Application type - associated indicator monitoring template (0 business application 1 host application)',
  `app_language` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'language type',
  `platform_type` int(0) NOT NULL COMMENT 'platform type',
  `app_sign_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Application index unique identification',
  `iam_tree_id` int(0) NOT NULL COMMENT 'iam tree id (alarm interface must)',
  `envs_map` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'environment list',
  `auto_capacity` int(0) NULL DEFAULT NULL COMMENT 'Automatic expansion 1 yes, 0 no',
  `status` int(0) NULL DEFAULT NULL COMMENT 'state',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT 'create time',
  `update_time` timestamp(0) NULL DEFAULT NULL COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_uniqe_app`(`bind_id`, `platform_type`) USING BTREE,
  INDEX `idx_app_name`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hera_app_env
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_env`;
CREATE TABLE `hera_app_env`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `hera_app_id` bigint(0) NOT NULL COMMENT 'hera_app_base_info primary key',
  `app_id` bigint(0) NOT NULL COMMENT 'The primary key for a real app',
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app name',
  `env_id` bigint(0) NOT NULL COMMENT 'Environment ID (from synchronization information)',
  `env_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Environment name',
  `ip_list` json NULL COMMENT 'IP information (all that is stored here is final information)',
  `ctime` bigint(0) NOT NULL COMMENT 'Creation time (millisecond timestamp)',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT 'Update time (millisecond timestamp)',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hera_app_excess_info
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_excess_info`;
CREATE TABLE `hera_app_excess_info`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `app_base_id` int(0) NULL DEFAULT NULL,
  `tree_ids` json NULL,
  `node_ips` json NULL,
  `managers` json NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `app_base_id_index`(`app_base_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hera_trace_etl_config
-- ----------------------------
DROP TABLE IF EXISTS `hera_trace_etl_config`;
CREATE TABLE `hera_trace_etl_config`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `base_info_id` int(0) NULL DEFAULT NULL COMMENT 'hera_base_info primary key id',
  `exclude_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Apply action filtering',
  `exclude_httpserver_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Application operations for HTTP server-side filtering',
  `exclude_thread` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Thread name filtering',
  `exclude_sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'SQL filtering',
  `exclude_http_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'URL filtering',
  `exclude_ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'user-agent filering',
  `http_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'HTTP slow query threshold',
  `dubbo_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'dubbo slow query threshold',
  `mysql_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'MySQL slow query threshold',
  `trace_filter` int(0) NULL DEFAULT NULL COMMENT 'The percentage of the trace that needs to be deposited into the ES',
  `trace_duration_threshold` int(0) NULL DEFAULT NULL COMMENT 'The threshold at which trace is deposited into ES',
  `trace_debug_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'trace is stored in ES''s debug identifier, corresponding to the HERA Context key',
  `http_status_error` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Which HTTP status codes are not displayed in the exception list',
  `exception_error` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Which exceptions are not exception requests',
  `grpc_code_error` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Which GRPC codes do not count as exception requests',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT 'Valid 0 Invalid 1 Valid',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
  `update_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_analyse_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_dashboard`;
CREATE TABLE `milog_analyse_dashboard`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `store_id` bigint(0) NULL DEFAULT NULL,
  `space_id` bigint(0) NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `updater` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` bigint(0) NULL DEFAULT NULL,
  `update_time` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_analyse_dashboard_graph_ref
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_dashboard_graph_ref`;
CREATE TABLE `milog_analyse_dashboard_graph_ref`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dashboard_id` bigint(0) NULL DEFAULT NULL,
  `graph_id` bigint(0) NULL DEFAULT NULL,
  `point` json NULL,
  `private_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30012 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_analyse_graph
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_graph`;
CREATE TABLE `milog_analyse_graph`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `field_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `space_id` bigint(0) NULL DEFAULT NULL,
  `store_id` bigint(0) NULL DEFAULT NULL,
  `graph_type` int(0) NULL DEFAULT NULL,
  `graph_param` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `updater` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` bigint(0) NULL DEFAULT NULL,
  `update_time` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_analyse_graph_type
-- ----------------------------
DROP TABLE IF EXISTS `milog_analyse_graph_type`;
CREATE TABLE `milog_analyse_graph_type`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `type` int(0) NULL DEFAULT NULL,
  `calculate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `classify` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of milog_analyse_graph_type
-- ----------------------------
INSERT INTO `milog_analyse_graph_type` VALUES (1, '饼图', 1, NULL, '比例');
INSERT INTO `milog_analyse_graph_type` VALUES (2, '折线图', 2, NULL, '折线图和面积图');
INSERT INTO `milog_analyse_graph_type` VALUES (3, '垂直条形图', 3, NULL, '条形图');
INSERT INTO `milog_analyse_graph_type` VALUES (4, '水平条形图', 4, NULL, '条形图');
INSERT INTO `milog_analyse_graph_type` VALUES (5, '面积图', 5, NULL, '折线图和面积图');
INSERT INTO `milog_analyse_graph_type` VALUES (8, '圆环图', 8, NULL, '比例');
INSERT INTO `milog_analyse_graph_type` VALUES (9, '南丁格尔玫瑰图', 9, NULL, '比例');
INSERT INTO `milog_analyse_graph_type` VALUES (10, '分时柱状图', 10, NULL, '分时柱状图');

-- ----------------------------
-- Table structure for milog_app_middleware_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_middleware_rel`;
CREATE TABLE `milog_app_middleware_rel`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `milog_app_id` bigint(0) NOT NULL COMMENT 'milog app primary key',
  `middleware_id` bigint(0) NOT NULL COMMENT 'The middleware configuration table ID',
  `tail_id` bigint(0) NOT NULL COMMENT 'Collect the trail ID of the log path',
  `config` json NULL COMMENT 'Configuration information, in JSON format',
  `ctime` bigint(0) NOT NULL COMMENT 'create time',
  `utime` bigint(0) NOT NULL COMMENT 'update time',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'creator',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'updater',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 166964 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_app_topic_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_topic_rel`;
CREATE TABLE `milog_app_topic_rel`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT 'create time',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT 'update time',
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT 'Tenant ID',
  `app_id` bigint(0) NOT NULL COMMENT 'app id',
  `iam_tree_id` bigint(0) NULL DEFAULT NULL COMMENT 'mione app Iam treeId',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app name',
  `operator` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'operator',
  `mq_config` json NULL COMMENT 'MQ configuration information, in JSON format',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app source',
  `type` smallint(0) NULL DEFAULT NULL COMMENT '0.mione project',
  `tree_ids` json NULL COMMENT 'The IDS of the mounted tree of the project',
  `node_ips` json NULL COMMENT 'The IPS of the physical machine where the application resides',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90198 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_es_cluster
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_cluster`;
CREATE TABLE `milog_es_cluster`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Cluster type',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Cluster nameCluster nameCluster name',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Room',
  `cluster_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Converge the cluster name on the cloud',
  `addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES address',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES username',
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES password',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `dt_catalog` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `dt_database` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region',
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT 'update time',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT 'create time',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
  `labels` json NULL COMMENT 'labels',
  `con_way` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Connection method:pwd,token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_es_index
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index`;
CREATE TABLE `milog_es_index`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `cluster_id` bigint(0) NULL DEFAULT NULL COMMENT 'The ID of the cluster to which it belongs',
  `log_type` int(0) NULL DEFAULT NULL COMMENT 'log type',
  `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1525057282467068561 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_count
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_count`;
CREATE TABLE `milog_log_count`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tail_id` bigint(0) NULL DEFAULT NULL COMMENT 'tail id',
  `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
  `day` date NULL DEFAULT NULL COMMENT 'The log data is generated on the day yyyy-MM-dd',
  `number` bigint(0) NULL DEFAULT NULL COMMENT 'Number of logs',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 174461 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_num_alert
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_num_alert`;
CREATE TABLE `milog_log_num_alert`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `day` date NULL DEFAULT NULL,
  `number` bigint(0) NULL DEFAULT NULL,
  `app_id` bigint(0) NULL DEFAULT NULL,
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `alert_user` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ctime` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `day+appId`(`day`, `app_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_search_save
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_search_save`;
CREATE TABLE `milog_log_search_save`  (
  `id` bigint(0) NOT NULL,
  `store_id` bigint(0) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `is_fix_time` int(0) NULL DEFAULT NULL COMMENT '1 - the time parameter is saved; 0 - Not saved',
  `common` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'remark',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
  `updater` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
  `create_time` bigint(0) NULL DEFAULT NULL,
  `update_time` bigint(0) NULL DEFAULT NULL,
  `start_time` bigint(0) NULL DEFAULT NULL COMMENT 'Search start time',
  `end_time` bigint(0) NULL DEFAULT NULL COMMENT 'Search end time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template`;
CREATE TABLE `milog_log_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `ctime` bigint(20) DEFAULT NULL COMMENT 'create time',
  `utime` bigint(20) DEFAULT NULL COMMENT 'update time',
  `template_name` varchar(255) NOT NULL COMMENT 'Log template name',
  `type` int(11) DEFAULT NULL COMMENT 'Log template type 0 - custom log, 1 - app; 2-nginx',
  `support_area` varchar(255) DEFAULT NULL COMMENT 'Support the computer room',
  `order_col` int(11) DEFAULT NULL COMMENT 'sort',
  `supported_consume` smallint(2) NOT NULL DEFAULT '1' COMMENT 'Whether consumption is supported, 1 is supported by default',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of milog_log_template
-- ----------------------------
INSERT INTO `milog_log_template` VALUES (9, 1656038440000, 1656038440000, 'Single-line application log', 8, 'cn,ams,in,alsg,mos', 150);

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT 'create time',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT 'update time',
  `template_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template ID',
  `properties_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template property name; 1- Required; 2- Recommendations; 3- Hide',
  `properties_type` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template property type',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 94 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of milog_log_template_detail
-- ----------------------------
INSERT INTO `milog_log_template_detail` VALUES (93, 1628508945923, 1628508945923, '9', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1', 'date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword');

-- ----------------------------
-- Table structure for milog_logstail
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstail`;
CREATE TABLE `milog_logstail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT 'create time',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT 'update time',
  `creator` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
  `updater` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
  `space_id` bigint(0) NULL DEFAULT NULL COMMENT 'spaceId',
  `store_id` bigint(0) NULL DEFAULT NULL COMMENT 'storeId',
  `tail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app alias',
  `milog_app_id` bigint(0) NULL DEFAULT NULL COMMENT 'milog primary key',
  `app_id` bigint(0) NULL DEFAULT NULL COMMENT 'app id',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app name',
  `app_type` smallint(0) NULL DEFAULT NULL COMMENT '0.mione project',
  `machine_type` smallint(0) NULL DEFAULT NULL COMMENT 'Machine Type 0. Container 1. Physical machine',
  `env_id` int(0) NULL DEFAULT NULL COMMENT 'Environment ID',
  `env_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Environment name',
  `parse_type` int(0) NULL DEFAULT NULL COMMENT 'Log parsing type: 1: service application log, 2. delimiter, 3: single line, 4: multiple line, 5: custom',
  `parse_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'For delimiter, the field specifies the delimiter; For customization, this field specifies the log read script',
  `log_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Comma split, multiple log file paths,e.g.:/home/work/log/xxx/server.log',
  `log_split_express` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log slicing expression',
  `value_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'A list of values, multiple separated by commas',
  `ips` json NULL COMMENT 'ip list',
  `motor_rooms` json NULL COMMENT 'Apply the information of the computer room',
  `filter` json NULL COMMENT 'filter config',
  `en_es_index` json NULL COMMENT 'the index configuration',
  `deploy_way` int(0) NULL DEFAULT NULL COMMENT 'deployment type：1-mione; 2-miline; 3-k8s',
  `first_line_reg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Custom line regex'
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_logstore
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstore`;
CREATE TABLE `milog_logstore`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间',
  `space_id` bigint(0) NOT NULL COMMENT 'spaceId',
  `logstoreName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '日志存储名称',
  `store_period` int(0) NULL DEFAULT NULL COMMENT '存储周期:1-3-5-7',
  `shard_cnt` int(0) NULL DEFAULT NULL COMMENT '存储分片数',
  `key_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'key列表，多个用逗号分隔',
  `column_type_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '段类型，多个用逗号分隔',
  `log_type` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '1:app,2:ngx..',
  `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index:milog_logstoreName',
  `es_cluster_id` bigint(0) NULL DEFAULT NULL,
  `machine_room` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '机房信息',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `mq_resource_id` bigint(0) NULL DEFAULT NULL COMMENT '资源表中mq的Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90034 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_middleware_config
-- ----------------------------
DROP TABLE IF EXISTS `milog_middleware_config`;
CREATE TABLE `milog_middleware_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` smallint(0) NOT NULL COMMENT '配置 1. rocketmq 2.talos',
  `region_en` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '不同的机房',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '别名',
  `name_server` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'nameServer地址',
  `service_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '域名',
  `ak` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ak',
  `sk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sk',
  `authorization` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '授权信息(http接口请求头需要)',
  `org_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组织Id',
  `team_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户组Id',
  `is_default` smallint(0) NULL DEFAULT 0 COMMENT '是否默认当不选择mq的时候采用这个配置(1.是 0.否)',
  `ctime` bigint(0) NOT NULL COMMENT '创建时间',
  `utime` bigint(0) NOT NULL COMMENT '更新时间',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建人',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '更新人',
  `labels` json NULL COMMENT '标签',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
CREATE TABLE `milog_store_space_auth`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `store_id` bigint(0) NOT NULL,
  `space_id` bigint(0) NOT NULL,
  `ctime` bigint(0) NOT NULL,
  `utime` bigint(0) NULL DEFAULT NULL,
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `updater` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `doc_link` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `owner` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `update_time` bigint(0) NULL DEFAULT NULL,
  `create_time` bigint(0) NULL DEFAULT NULL,
  `order_number` bigint(0) NULL DEFAULT NULL,
  `is_top` int(0) NULL DEFAULT NULL,
  `is_key` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
