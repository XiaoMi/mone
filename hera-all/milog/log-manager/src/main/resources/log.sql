

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
  `milog_app_id` bigint(0) NULL DEFAULT NULL COMMENT 'milogApp表主键',
  `app` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `log_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `contacts` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `feishu_groups` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `job_id` int(0) NULL DEFAULT NULL COMMENT '数据工厂任务Id',
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
  `bind_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '绑定的id',
  `bind_type` int(0) NOT NULL COMMENT '绑定类型(0 appId 1 iamTreeId)',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用名称',
  `app_cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用中文名称',
  `app_type` int(0) NOT NULL COMMENT '应用类型-关联指标监控模版（0业务应用 1主机应用）',
  `app_language` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '语言类型',
  `platform_type` int(0) NOT NULL COMMENT '平台类型',
  `app_sign_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用指标唯一性标识',
  `iam_tree_id` int(0) NOT NULL COMMENT 'iam_tree_id(报警接口必须)',
  `envs_map` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '环境列表',
  `auto_capacity` int(0) NULL DEFAULT NULL COMMENT '自动扩容 1是，0否',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL COMMENT '更新时间',
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
  `hera_app_id` bigint(0) NOT NULL COMMENT 'hera_app_base_info表的主键',
  `app_id` bigint(0) NOT NULL COMMENT '真实应用的主键',
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用名称',
  `env_id` bigint(0) NOT NULL COMMENT '环境id(来自于同步信息)',
  `env_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '环境名',
  `ip_list` json NULL COMMENT 'ip信息（这里存储的都是最终的信息）',
  `ctime` bigint(0) NOT NULL COMMENT '创建时间（毫秒时间戳）',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间(毫秒时间戳)',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
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
  `base_info_id` int(0) NULL DEFAULT NULL COMMENT 'hera_base_info表的id',
  `exclude_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用操作过滤',
  `exclude_httpserver_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'httpServer端过滤的应用操作',
  `exclude_thread` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '线程名称过滤',
  `exclude_sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sql过滤',
  `exclude_http_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'url过滤',
  `exclude_ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'user-agent过滤',
  `http_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'http慢查询阈值',
  `dubbo_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'dubbo慢查询阈值',
  `mysql_slow_threshold` int(0) NULL DEFAULT NULL COMMENT 'mysql慢查询阈值',
  `trace_filter` int(0) NULL DEFAULT NULL COMMENT 'trace需要存入es的百分比',
  `trace_duration_threshold` int(0) NULL DEFAULT NULL COMMENT 'trace存入es的耗时阈值',
  `trace_debug_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'trace存入es的debug标识，对应heraContext的key',
  `http_status_error` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '哪些http状态码不显示在异常列表',
  `exception_error` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '哪些exception不算异常请求',
  `grpc_code_error` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '哪些grpc_code不算异常请求',
  `status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '是否有效 0无效  1有效',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '修改人',
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
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `milog_app_id` bigint(0) NOT NULL COMMENT 'milog app表主键\r\n',
  `middleware_id` bigint(0) NOT NULL COMMENT '中间件配置表ID\r\n',
  `tail_id` bigint(0) NOT NULL COMMENT '采集日志路径tailId\r\n',
  `config` json NULL COMMENT '配置信息，json格式',
  `ctime` bigint(0) NOT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NOT NULL COMMENT '更新时间',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建人\r\n',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 166964 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_app_topic_rel
-- ----------------------------
DROP TABLE IF EXISTS `milog_app_topic_rel`;
CREATE TABLE `milog_app_topic_rel`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间\r\n',
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户Id\r\n',
  `app_id` bigint(0) NOT NULL COMMENT '应用id\r\n',
  `iam_tree_id` bigint(0) NULL DEFAULT NULL COMMENT 'mione应用Iam treeId',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app名称\r\n',
  `operator` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作者\r\n',
  `mq_config` json NULL COMMENT 'mq配置信息，json格式',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app来源\r\n',
  `type` smallint(0) NULL DEFAULT NULL COMMENT '0.mione 项目 1.mis项目\r\n',
  `tree_ids` json NULL COMMENT 'mis 项目的挂载的树ids',
  `node_ips` json NULL COMMENT '应用所在的物理机ips',
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
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '集群类型\r\n',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '集群名称\r\n集群名称\r\n集群名称\r\n',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '机房',
  `cluster_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '融合云上集群名',
  `addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES地址\r\n',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES用户名\r\n',
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES密码\r\n',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `dt_catalog` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `dt_database` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地区\r\n',
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间\r\n',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人\r\n',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人\r\n',
  `labels` json NULL COMMENT '标签',
  `con_way` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '连接方式:pwd,token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60009 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_es_index
-- ----------------------------
DROP TABLE IF EXISTS `milog_es_index`;
CREATE TABLE `milog_es_index`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cluster_id` bigint(0) NULL DEFAULT NULL COMMENT '所属集群id\r\n',
  `log_type` int(0) NULL DEFAULT NULL COMMENT '日志类型\r\n',
  `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es索引名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1525057282467068561 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_count
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_count`;
CREATE TABLE `milog_log_count`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tail_id` bigint(0) NULL DEFAULT NULL COMMENT 'tail的ID',
  `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es索引名',
  `day` date NULL DEFAULT NULL COMMENT '日志数据产生日yyyy-MM-dd',
  `number` bigint(0) NULL DEFAULT NULL COMMENT '日志条数',
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
  `is_fix_time` int(0) NULL DEFAULT NULL COMMENT '1-保存了时间参数；0-没有保存',
  `common` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
  `updater` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后更新人',
  `create_time` bigint(0) NULL DEFAULT NULL,
  `update_time` bigint(0) NULL DEFAULT NULL,
  `start_time` bigint(0) NULL DEFAULT NULL COMMENT '搜索开始时间',
  `end_time` bigint(0) NULL DEFAULT NULL COMMENT '搜索结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template`;
CREATE TABLE `milog_log_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ctime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `utime` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `template_name` varchar(255) NOT NULL COMMENT '日志模板名称',
  `type` int(11) DEFAULT NULL COMMENT '日志模板类型0-自定义日志,1-app;2-nginx',
  `support_area` varchar(255) DEFAULT NULL COMMENT '支持机房',
  `order_col` int(11) DEFAULT NULL COMMENT '排序',
  `supported_consume` smallint(2) NOT NULL DEFAULT '1' COMMENT '是否支持消费，默认支持1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of milog_log_template
-- ----------------------------
INSERT INTO `milog_log_template` VALUES (9, 1656038440000, 1656038440000, '单行应用日志', 8, 'cn,ams,in,alsg,mos', 150);

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间',
  `template_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板ID\r\n',
  `properties_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性名；1-必选；2-建议；3-隐藏',
  `properties_type` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性类型\r\n',
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
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间\r\n',
  `creator` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人\r\n',
  `updater` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人\r\n',
  `space_id` bigint(0) NULL DEFAULT NULL COMMENT 'spaceId',
  `store_id` bigint(0) NULL DEFAULT NULL COMMENT 'storeId',
  `tail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用别名\r\n',
  `milog_app_id` bigint(0) NULL DEFAULT NULL COMMENT 'milog表主键',
  `app_id` bigint(0) NULL DEFAULT NULL COMMENT '应用id\r\n',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用名\r\n',
  `app_type` smallint(0) NULL DEFAULT NULL COMMENT '0.mione项目 1.mis项目',
  `machine_type` smallint(0) NULL DEFAULT NULL COMMENT 'mis应用 机器类型 0.容器 1.物理机',
  `env_id` int(0) NULL DEFAULT NULL COMMENT '环境Id\r\n',
  `env_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '环境名称\r\n',
  `parse_type` int(0) NULL DEFAULT NULL COMMENT '日志解析类型：1:服务应用日志，2.分隔符，3：单行，4：多行，5：自定义',
  `parse_script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '对于分隔符，该字段指定分隔符；对于自定义，该字段指定日志读取脚本\r\n',
  `log_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '逗号分割，多个日志文件路径,e.g.:/home/work/log/xxx/server.log\r\n',
  `log_split_express` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志切分表达式',
  `value_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'value列表，多个用逗号分隔\r\n',
  `ips` json NULL COMMENT 'ip列表\r\n',
  `motor_rooms` json NULL COMMENT 'mis 应用机房信息',
  `filter` json NULL COMMENT 'filter配置\r\n',
  `en_es_index` json NULL COMMENT 'mis应用索引配置',
  `deploy_way` int(0) NULL DEFAULT NULL COMMENT '部署方式：1-mione; 2-miline; 3-k8s',
  `first_line_reg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '自定义行首正则'
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_logstore
-- ----------------------------
DROP TABLE IF EXISTS `milog_logstore`;
CREATE TABLE `milog_logstore`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间\r\n',
  `space_id` bigint(0) NOT NULL COMMENT 'spaceId',
  `logstoreName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '日志存储名称\r\n',
  `store_period` int(0) NULL DEFAULT NULL COMMENT '存储周期:1-3-5-7',
  `shard_cnt` int(0) NULL DEFAULT NULL COMMENT '存储分片数\r\n',
  `key_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'key列表，多个用逗号分隔\r\n',
  `column_type_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '段类型，多个用逗号分隔\r\n',
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
  `region_en` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '不同的机房\r\n',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '别名\r\n',
  `name_server` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'nameServer地址\r\n',
  `service_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '域名\r\n',
  `ak` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ak',
  `sk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sk',
  `authorization` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '授权信息(http接口请求头需要)\r\n',
  `org_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组织Id\r\n',
  `team_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户组Id\r\n',
  `is_default` smallint(0) NULL DEFAULT 0 COMMENT '是否默认当不选择mq的时候采用这个配置(1.是 0.否)',
  `ctime` bigint(0) NOT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NOT NULL COMMENT '更新时间\r\n',
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '创建人\r\n',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '更新人\r\n',
  `labels` json NULL COMMENT '标签',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_region_zone
-- ----------------------------
DROP TABLE IF EXISTS `milog_region_zone`;
CREATE TABLE `milog_region_zone`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `region_name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region英文名',
  `region_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region中文名',
  `zone_name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone英文名',
  `zone_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone中文名',
  `ctime` bigint(0) NULL DEFAULT NULL,
  `utime` bigint(0) NULL DEFAULT NULL,
  `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for milog_space
-- ----------------------------
DROP TABLE IF EXISTS `milog_space`;
CREATE TABLE `milog_space`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(0) NULL DEFAULT NULL COMMENT '创建时间\r\n',
  `utime` bigint(0) NULL DEFAULT NULL COMMENT '更新时间\r\n',
  `tenant_id` int(0) NULL DEFAULT NULL COMMENT '租户Id\r\n',
  `space_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '项目空间名称\r\n',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '来源：开源',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者\r\n',
  `dept_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者所在三级部门',
  `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注说明\r\n',
  `create_dept_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `perm_dept_id` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

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
