CREATE
DATABASE  IF NOT EXISTS `hera`  DEFAULT CHARACTER SET utf8mb4 ;

USE
`hera`;

-- trace-etl

CREATE TABLE `hera_trace_etl_config`
(
    `id`                        int NOT NULL AUTO_INCREMENT,
    `base_info_id`              int                              DEFAULT NULL COMMENT 'hera_base_info id',
    `exclude_method`            varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Application operation filtering',
    `exclude_httpserver_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Application operations filtered by the httpServer end.',
    `exclude_thread`            varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Thread name filtering',
    `exclude_sql`               varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'SQL filtering',
    `exclude_http_url`          varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'URL filtering',
    `exclude_ua`                varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'User-agent filtering',
    `http_slow_threshold`       int                              DEFAULT NULL COMMENT 'http slow query threshold',
    `dubbo_slow_threshold`      int                              DEFAULT NULL COMMENT 'dubbo slow query threshold',
    `mysql_slow_threshold`      int                              DEFAULT NULL COMMENT 'mysql slow query threshold',
    `trace_filter`              int                              DEFAULT NULL COMMENT 'The percentage of trace to be stored in ES.',
    `trace_duration_threshold`  int                              DEFAULT NULL COMMENT 'The time threshold for storing trace in Elasticsearch.',
    `trace_debug_flag`          varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'The debug flag of trace is stored in ES, corresponding to the key of heraContext.',
    `http_status_error`         varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Which HTTP status codes are not displayed in the exception list?',
    `exception_error`           varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Which exceptions are not considered abnormal requests?',
    `grpc_code_error`           varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Which grpc_code is not considered an abnormal request?',
    `status`                    varchar(2) COLLATE utf8mb4_bin   DEFAULT '1' COMMENT 'Is valid 0 invalid, 1 valid.',
    `create_time`               datetime                         DEFAULT NULL,
    `update_time`               datetime                         DEFAULT NULL,
    `create_user`               varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT 'Creator',
    `update_user`               varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT 'Editor',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `hera_meta_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(10) DEFAULT NULL COMMENT 'Data types include APP, MYSQL, REDIS, ES, MQ, etc.',
  `meta_id` int(11) DEFAULT NULL COMMENT 'Metadata id, such as appId.',
  `meta_name` varchar(255) DEFAULT NULL COMMENT 'The name of the metadata, such as appName.',
  `env_id` int(11) DEFAULT NULL COMMENT 'Environment ID',
  `env_name` varchar(255) DEFAULT NULL COMMENT 'Environment name',
  `host` varchar(255) DEFAULT NULL COMMENT 'The instance corresponding to the metadata could be an IP, a domain name, or a host name.',
  `port` json DEFAULT NULL COMMENT 'Port exposed by metadata',
  `dubbo_service_meta` text DEFAULT NULL COMMENT 'dubbo Service information includes ServiceName, Group, and version.',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(125) DEFAULT NULL,
  `update_by` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_meta_id` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- mimonitor

CREATE TABLE `alert_group`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(64) NOT NULL COMMENT 'name',
    `desc`        varchar(256) DEFAULT NULL COMMENT 'comment',
    `chat_id`     varchar(125) DEFAULT NULL COMMENT 'feishu ID',
    `creater`     varchar(64)  DEFAULT NULL COMMENT 'creator',
    `create_time` timestamp NULL DEFAULT NULL COMMENT 'create time',
    `update_time` timestamp NULL DEFAULT NULL COMMENT 'update time',
    `rel_id`      bigint(20) DEFAULT '0' COMMENT 'relation ID',
    `type`        varchar(32)  DEFAULT 'alert' COMMENT 'Alarm type',
    `deleted`     int(1) DEFAULT '0' COMMENT '0 normal, 1 delete',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_group_member
-- ----------------------------
CREATE TABLE `alert_group_member`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `member_id`      bigint(20) DEFAULT '0' COMMENT 'member ID',
    `alert_group_id` bigint(20) DEFAULT '0' COMMENT 'Alarm group ID',
    `creater`        varchar(64) DEFAULT NULL COMMENT 'creator',
    `create_time`    timestamp NULL DEFAULT NULL COMMENT 'create time',
    `update_time`    timestamp NULL DEFAULT NULL COMMENT 'update time',
    `member`         varchar(64) DEFAULT '' COMMENT 'user',
    `deleted`        int(1) DEFAULT '0' COMMENT '0 normal, 1 delete',
    PRIMARY KEY (`id`),
    KEY              `idx_member_id` (`member_id`),
    KEY              `idx_alert_group_id` (`alert_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_alarm_rule
-- ----------------------------
CREATE TABLE `app_alarm_rule`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `alarm_id`    int(11) DEFAULT NULL COMMENT 'Alarm ID, corresponding to the alarm ID of the Prometheus alarm interface.',
    `alert`       varchar(255) NOT NULL COMMENT 'Police name',
    `cname`       varchar(255) DEFAULT NULL COMMENT 'Alias for reporting a crime',
    `metric_type` int(11) DEFAULT NULL COMMENT 'Indicator type 0 preset indicator 1 user-defined indicator.',
    `expr`        text         DEFAULT NULL COMMENT 'Expression',
    `for_time`    varchar(50)  NOT NULL COMMENT 'Duration',
    `labels`      text         DEFAULT NULL COMMENT 'label',
    `annotations` varchar(255) DEFAULT NULL COMMENT 'Alarm description information',
    `rule_group`  varchar(50)  DEFAULT NULL COMMENT 'rule-group',
    `priority`    varchar(20)  DEFAULT NULL COMMENT 'Alarm level',
    `alert_team`  text         DEFAULT NULL COMMENT 'Alarm group JSON',
    `env`         varchar(100) DEFAULT NULL COMMENT 'env',
    `op`          varchar(5)   DEFAULT NULL COMMENT 'Operator',
    `value`       float(255, 2
) DEFAULT NULL COMMENT 'Threshold',
 `data_count` int(11) DEFAULT NULL COMMENT 'Number of data points recently',
 `send_interval` varchar(20) DEFAULT NULL COMMENT 'Alarm sending interval',
 `project_id` int(11) DEFAULT NULL COMMENT 'Project ID',
 `strategy_id` int(11) unsigned DEFAULT '0' COMMENT 'Strategy ID',
 `iam_id` int(11) DEFAULT NULL COMMENT 'iamId',
 `template_id` int(11) DEFAULT NULL COMMENT 'Template ID',
 `rule_type` int(11) DEFAULT NULL COMMENT 'Rule type: 0 template rule, 1 application configuration rule.',
 `rule_status` int(11) DEFAULT NULL COMMENT '0 Active 1 Pause',
 `remark` varchar(255) DEFAULT NULL COMMENT 'Note',
 `creater` varchar(64) DEFAULT NULL COMMENT 'creator',
 `status` int(11) DEFAULT NULL COMMENT 'Status 0 valid 1 deleted',
 `create_time` timestamp NULL DEFAULT NULL COMMENT 'create time',
 `update_time` timestamp NULL DEFAULT NULL COMMENT 'update time',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_alarm_rule_template
-- ----------------------------
CREATE TABLE `app_alarm_rule_template`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `name`          varchar(255) NOT NULL COMMENT 'name',
    `type`          int(11) NOT NULL COMMENT 'type 0 system 1 user',
    `remark`        varchar(255) DEFAULT NULL COMMENT 'remark',
    `creater`       varchar(64)  DEFAULT NULL COMMENT 'creator',
    `status`        int(11) DEFAULT NULL COMMENT 'status：0 Effective 1 Deleted',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time`   timestamp NULL DEFAULT NULL COMMENT 'update_time',
    `strategy_type` int(11) DEFAULT '0' COMMENT 'strategy_type',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



-- ----------------------------
-- Table structure for app_alarm_strategy
-- ----------------------------
CREATE TABLE `app_alarm_strategy`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `iamId`         int(11) DEFAULT '0',
    `appId`         int(11) NOT NULL,
    `appName`       varchar(100) DEFAULT NULL COMMENT 'appName',
    `strategy_type` int(11) DEFAULT NULL COMMENT 'strategy_type',
    `strategy_name` varchar(100) DEFAULT NULL COMMENT 'strategy_name',
    `desc`          varchar(255) DEFAULT NULL COMMENT 'desc',
    `creater`       varchar(64)  DEFAULT NULL COMMENT 'creator',
    `create_time`   timestamp NULL DEFAULT NULL,
    `update_time`   timestamp NULL DEFAULT NULL,
    `status`        tinyint(2) NOT NULL DEFAULT '0' COMMENT 'status',
    `alert_team`    text         DEFAULT NULL COMMENT 'alert_team',
    `group3`        varchar(32)  DEFAULT '' COMMENT 'group3',
    `group4`        varchar(32)  DEFAULT '' COMMENT 'group4',
    `group5`        varchar(32)  DEFAULT '' COMMENT 'group5',
    `envs`          text         DEFAULT NULL COMMENT 'envs',
    `alert_members` text         DEFAULT NULL COMMENT 'alert_members',
    `at_members`    text         DEFAULT NULL COMMENT 'at_members',
    `services`      text         DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_capacity_auto_adjust
-- ----------------------------
CREATE TABLE `app_capacity_auto_adjust`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `app_id`        int(11) NOT NULL,
    `pipeline_id`   int(11) NOT NULL COMMENT 'pipeline id',
    `container`     varchar(255) DEFAULT NULL COMMENT 'container name',
    `status`        int(3) DEFAULT NULL COMMENT '0 Available，1 Not available.',
    `min_instance`  int(8) DEFAULT NULL COMMENT 'min instance',
    `max_instance`  int(8) DEFAULT NULL COMMENT 'max instance',
    `auto_capacity` int(3) DEFAULT NULL COMMENT 'auto capacity 1 yes 0 no',
    `depend_on`     int(3) DEFAULT NULL COMMENT 'depend_on 0 cpu 1 memory 2 both depend_on',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT 'create time',
    `update_time`   timestamp NULL DEFAULT NULL COMMENT 'update time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique-pipleline` (`app_id`,`pipeline_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_capacity_auto_adjust_record
-- ----------------------------
CREATE TABLE `app_capacity_auto_adjust_record`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `container`    varchar(255) DEFAULT NULL,
    `name_space`   varchar(255) DEFAULT NULL,
    `replicas`     int(8) DEFAULT NULL,
    `set_replicas` int(8) DEFAULT NULL,
    `env_id`       int(11) DEFAULT NULL,
    `status`       int(3) DEFAULT NULL,
    `time`         bigint(20) DEFAULT NULL,
    `create_time`  timestamp NULL DEFAULT NULL,
    `update_time`  timestamp NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_grafana_mapping
-- ----------------------------
CREATE TABLE `app_grafana_mapping`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `app_name`    varchar(100) NOT NULL,
    `mione_env`   varchar(20) DEFAULT NULL,
    `grafana_url` varchar(200) NOT NULL,
    `create_time` timestamp NULL DEFAULT NULL,
    `update_time` timestamp NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `appNameIndex` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_monitor
-- ----------------------------
CREATE TABLE `app_monitor`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `project_id`     int(11) DEFAULT NULL COMMENT 'project_id',
    `iam_tree_id`    int(11) DEFAULT NULL COMMENT 'iamTreeId',
    `iam_tree_type`  int(3) DEFAULT NULL COMMENT 'iam type，0:TPC、1:XiaoMI IAM',
    `project_name`   varchar(255) DEFAULT NULL COMMENT 'project_name',
    `app_source`     int(8) DEFAULT '0' COMMENT 'app_source 0-opensource',
    `owner`          varchar(128)  DEFAULT NULL COMMENT 'owner',
    `care_user`      varchar(30)  DEFAULT NULL COMMENT 'care_user',
    `alarm_level`    int(11) DEFAULT NULL COMMENT 'alarm_level',
    `total_alarm`    int(11) DEFAULT NULL COMMENT 'total_alarm',
    `exception_num`  int(11) DEFAULT NULL COMMENT 'exception_num',
    `slow_query_num` int(11) DEFAULT NULL COMMENT 'slow_query_num',
    `status`         int(11) DEFAULT NULL COMMENT 'status 0 Effective 1 deleted',
    `base_info_id`   int(11) DEFAULT NULL COMMENT 'base_info_id',
    `create_time`    timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time`    timestamp NULL DEFAULT NULL COMMENT 'update_time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_quality_market
-- ----------------------------
CREATE TABLE `app_quality_market`
(
    `id`           int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `market_name`  varchar(255) NOT NULL DEFAULT '' COMMENT 'market_name',
    `creator`      varchar(100)          DEFAULT '' COMMENT 'creator',
    `service_list` TEXT                  DEFAULT NULL COMMENT 'Multiple applications are separated by semicolons.',
    `last_updater` varchar(100)          DEFAULT '' COMMENT 'last_updater',
    `remark`       varchar(255)          DEFAULT '' COMMENT 'remark',
    `create_time`  datetime              DEFAULT NULL COMMENT 'create_time',
    `update_time`  datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    PRIMARY KEY (`id`),
    KEY            `key_market_name` (`market_name`),
    KEY            `key_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_scrape_job
-- ----------------------------
CREATE TABLE `app_scrape_job`
(
    `id`          int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `iam_id`      int(11) NOT NULL COMMENT 'iam id',
    `user`        varchar(64)  NOT NULL DEFAULT '' COMMENT 'user',
    `job_json`    text                  DEFAULT NULL COMMENT 'job_json',
    `message`     varchar(255) NOT NULL DEFAULT '' COMMENT 'message',
    `data`        varchar(255)          DEFAULT '' COMMENT 'Success is the fetch ID returned by the request.',
    `job_name`    varchar(64)           DEFAULT NULL COMMENT 'The name of the job being fetched',
    `status`      tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT 'Job status: 0 - creation failed, 1 - creation successful, 2 - deleted.',
    `job_desc`    varchar(255)          DEFAULT '' COMMENT 'job_desc',
    `create_time` datetime     NOT NULL COMMENT 'create_time',
    `update_time` datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_service_market
-- ----------------------------
CREATE TABLE `app_service_market`
(
    `id`           int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `market_name`  varchar(150) NOT NULL DEFAULT '' COMMENT 'market_name',
    `belong_team`  varchar(150) NOT NULL DEFAULT '' COMMENT 'belong_team',
    `creator`      varchar(50)           DEFAULT '' COMMENT 'creator',
    `service_list` TEXT COMMENT 'Multiple applications are separated by semicolons.',
    `last_updater` varchar(50)           DEFAULT '' COMMENT 'last_updater',
    `remark`       varchar(255)          DEFAULT '' COMMENT 'remark',
    `service_type` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'service_type',
    `create_time`  datetime              DEFAULT NULL COMMENT 'create_time',
    `update_time`  datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    PRIMARY KEY (`id`),
    KEY            `key_market_name` (`market_name`),
    KEY            `key_creator` (`creator`),
    KEY            `key_belong_team` (`belong_team`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_tesla_alarm_rule
-- ----------------------------
CREATE TABLE `app_tesla_alarm_rule`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) DEFAULT NULL COMMENT 'name',
    `tesla_group` varchar(100) NOT NULL COMMENT 'tesla_group',
    `alert_type`  varchar(50)  DEFAULT NULL COMMENT 'alert_type',
    `exper`       text         DEFAULT NULL COMMENT 'exper',
    `op`          varchar(2)   DEFAULT NULL COMMENT 'operation',
    `value`       float(11, 2
) DEFAULT NULL COMMENT 'Threshold',
 `duration` varchar(20) DEFAULT NULL COMMENT 'duration',
 `remark` varchar(255) DEFAULT NULL COMMENT 'remark',
 `type` int(11) DEFAULT NULL COMMENT 'type',
 `status` int(11) DEFAULT NULL COMMENT 'status',
 `creater` varchar(64) DEFAULT NULL COMMENT 'creator',
 `create_time` timestamp NULL DEFAULT NULL COMMENT 'create_time',
 `update_time` timestamp NULL DEFAULT NULL COMMENT 'update_time',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_tesla_feishu_mapping
-- ----------------------------
CREATE TABLE `app_tesla_feishu_mapping`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `tesla_group`     varchar(50) NOT NULL COMMENT 'tesla_group',
    `feishu_group_id` varchar(50) NOT NULL COMMENT 'feishu_group_id',
    `remark`          varchar(255) DEFAULT NULL COMMENT 'remark',
    `creater`         varchar(64)  DEFAULT NULL COMMENT 'creator',
    `status`          int(11) DEFAULT NULL COMMENT 'status',
    `create_time`     timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time`     timestamp NULL DEFAULT NULL COMMENT 'update_time',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_base_info
-- ----------------------------
CREATE TABLE `hera_app_base_info`  (
       `id` int NOT NULL AUTO_INCREMENT,
       `bind_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'bind id',
       `bind_type` int NOT NULL COMMENT 'bind_type(0 appId 1 iamTreeId)',
       `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app_name',
       `app_cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app_cname',
       `app_type` int NOT NULL COMMENT 'Application Type - Associated Indicator Monitoring Template（0 Business Application 1 rate limited or exceeded quota）',
       `app_language` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'language type',
       `platform_type` int NOT NULL COMMENT 'platform type',
       `app_sign_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app sign id',
       `iam_tree_id` int NULL DEFAULT NULL COMMENT 'iam_tree_id(The alarm interface is necessary.)',
       `iam_tree_type` int(3) NOT NULL COMMENT 'iam type，0:TPC、1:XIAOMI IAM',
       `envs_map` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'envs list',
       `auto_capacity` int NULL DEFAULT NULL COMMENT 'auto capacity 1 yes，0 no',
       `status` int NULL DEFAULT NULL COMMENT 'status',
       `create_time` timestamp NULL DEFAULT NULL COMMENT 'create_time',
       `update_time` timestamp NULL DEFAULT NULL COMMENT 'update_time',
       PRIMARY KEY (`id`) USING BTREE,
       UNIQUE INDEX `idx_uniqe_app`(`bind_id`, `platform_type`) USING BTREE,
       INDEX `idx_app_name`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_excess_info
-- ----------------------------
CREATE TABLE `hera_app_excess_info`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `app_base_id` bigint NULL DEFAULT NULL,
     `tree_ids` json NULL,
     `node_ips` json NULL,
     `managers` json NULL,
     `create_time` datetime NULL DEFAULT NULL,
     `update_time` datetime NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `app_base_id_index`(`app_base_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_env
-- ----------------------------
CREATE TABLE `hera_app_env`  (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `hera_app_id` bigint NOT NULL COMMENT 'hera_app_base_info table id',
     `app_id` bigint NOT NULL COMMENT 'app_id',
     `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app_name',
     `env_id` bigint NULL DEFAULT NULL COMMENT 'env_id(Comes from synchronous information)',
     `env_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'env_name',
     `ip_list` json NULL COMMENT 'ip_list（The information stored here is all final.）',
     `ctime` bigint NOT NULL COMMENT 'create time（Millisecond timestamp）',
     `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
     `utime` bigint NULL DEFAULT NULL COMMENT 'update time(Millisecond timestamp)',
     `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_role
-- ----------------------------
CREATE TABLE `hera_app_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `app_platform` int NOT NULL,
  `user` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `role` int NOT NULL,
  `status` int NOT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_app_role`(`app_id`, `app_platform`) USING BTREE,
  INDEX `idx_app_role_user`(`user`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_oper_log
-- ----------------------------
CREATE TABLE `hera_oper_log`
(
    `id`               bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `oper_name`        varchar(64) NOT NULL COMMENT 'operator',
    `log_type`         int(1) DEFAULT '0' COMMENT '0 Overview，1 detail',
    `before_parent_id` bigint(11) DEFAULT '0' COMMENT 'before_parent_id',
    `module_name`      varchar(64)  DEFAULT '' COMMENT 'module_name',
    `interface_name`   varchar(64)  DEFAULT '' COMMENT 'interface_name',
    `interface_url`    varchar(128) DEFAULT '' COMMENT 'interface_url',
    `action`           varchar(32)  DEFAULT '' COMMENT 'action',
    `before_data`      text         DEFAULT NULL COMMENT 'before_data',
    `after_data`       text         DEFAULT NULL COMMENT 'after_data',
    `create_time`      timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time`      timestamp NULL DEFAULT NULL COMMENT 'update_time',
    `data_type`        int(1) DEFAULT '0' COMMENT '0 Unknown，1 Strategy，2 Rules',
    `after_parent_id`  bigint(11) DEFAULT '0' COMMENT 'after_parent_id',
    `result_desc`      varchar(128) DEFAULT '' COMMENT 'result_desc',
    PRIMARY KEY (`id`),
    KEY                `idx_before_parent_id` (`before_parent_id`),
    KEY                `idx_oper_name` (`oper_name`),
    KEY                `idx_after_parent_id` (`after_parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for rules
-- ----------------------------
CREATE TABLE `rules`
(
    `rule_id`          int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'rule_id',
    `rule_name`        varchar(255) DEFAULT '' COMMENT 'rule_name',
    `rule_fn`          varchar(255) DEFAULT '' COMMENT 'type',
    `rule_interval`    int(11) DEFAULT NULL COMMENT 'rule_interval',
    `rule_alert`       varchar(255) DEFAULT '' COMMENT 'rule_alert name',
    `rule_expr`        text         DEFAULT NULL COMMENT 'rule_expr',
    `rule_for`         varchar(255) DEFAULT '' COMMENT 'duration',
    `rule_labels`      varchar(255) DEFAULT '' COMMENT 'Rule dimension information',
    `rule_annotations` text         DEFAULT NULL COMMENT 'Description of rules',
    `principal`        varchar(255) DEFAULT NULL COMMENT 'Comma separated prefixes of the person in charge email.',
    `create_time`      date         DEFAULT NULL COMMENT 'create_time',
    `update_time`      date         DEFAULT NULL COMMENT 'update_time',
    PRIMARY KEY (`rule_id`),
    UNIQUE KEY `unique_key` (`rule_alert`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for rule_promql_template
-- ----------------------------
CREATE TABLE `rule_promql_template`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL COMMENT 'Template Name',
    `promql`      varchar(512) DEFAULT NULL COMMENT 'promql',
    `type`        int(11) NOT NULL COMMENT 'type 0 system 1 user',
    `remark`      varchar(255) DEFAULT NULL COMMENT 'remark',
    `creater`     varchar(64)  DEFAULT '' COMMENT 'creator',
    `status`      int(11) DEFAULT NULL COMMENT 'status：0 Effective',
    `create_time` timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time` timestamp NULL DEFAULT NULL COMMENT 'update_time',
    PRIMARY KEY (`id`),
    KEY           `idx_creater` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_monitor_config
-- ----------------------------
CREATE TABLE `app_monitor_config`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `project_id`  int          NOT NULL COMMENT 'id',
    `config_type` int          NOT NULL COMMENT 'config_type 0 Slow query time',
    `config_name` varchar(50)  NOT NULL COMMENT 'config_name',
    `value`       varchar(255) NOT NULL COMMENT 'value',
    `status`      int          NOT NULL,
    `create_time` timestamp NULL DEFAULT NULL,
    `update_time` timestamp NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for mione_grafana_template
-- ----------------------------
CREATE TABLE `mione_grafana_template`
(
    `id`            int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`          varchar(64) NOT NULL DEFAULT '' COMMENT 'Template name',
    `template`      longtext             DEFAULT NULL COMMENT 'template json',
    `platform`      int(11) DEFAULT NULL COMMENT 'platform',
    `language`      int(11) DEFAULT NULL COMMENT 'language',
    `app_type`      int(11) DEFAULT NULL COMMENT 'app_type',
    `panel_id_list` text                 DEFAULT NULL COMMENT 'panel_id_list',
    `url_param`     text                 DEFAULT NULL COMMENT 'url_param',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT 'create_time',
    `update_time`   timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    `deleted`       tinyint(1) DEFAULT '0' COMMENT '0 Not deleted 1 deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1;

SET
FOREIGN_KEY_CHECKS = 1;

-- milog

-- ----------------------------
-- Table structure for alert
-- ----------------------------
CREATE TABLE `alert`
(
    `id`             bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`           varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `type`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `milog_app_id`   bigint(20) NULL DEFAULT NULL COMMENT 'milogApp table id',
    `app`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `app_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `log_path`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    `contacts`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `feishu_groups`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `job_id`         int(10) NULL DEFAULT NULL COMMENT 'data factory job Id',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_condition
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_log
-- ----------------------------
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
    INDEX         `alert_log_app_name_ip_starttime_IDX`(`app_name`, `ip`, `start_time`) USING BTREE,
    INDEX         `alert_log_app_name_starttime_IDX`(`app_name`, `start_time`) USING BTREE,
    INDEX         `alert_log_ip_starttime_IDX`(`ip`, `start_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
CREATE TABLE `alert_rule`
(
    `id`       bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `alert_id` bigint(20) UNSIGNED NOT NULL,
    `regex`    varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `ctime`    bigint(20) UNSIGNED NOT NULL,
    `utime`    bigint(20) UNSIGNED NOT NULL,
    `creator`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `name`     varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'none',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX      `alert_rule_alert_id_IDX`(`alert_id`) USING HASH
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_analyse_dashboard
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_analyse_dashboard_graph_ref
-- ----------------------------
CREATE TABLE `milog_analyse_dashboard_graph_ref`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `dashboard_id` bigint(20) NULL DEFAULT NULL,
    `graph_id`     bigint(20) NULL DEFAULT NULL,
    `point`        json NULL,
    `private_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_analyse_graph
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_analyse_graph_type
-- ----------------------------
CREATE TABLE `milog_analyse_graph_type`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `type`      int(11) NULL DEFAULT NULL,
    `calculate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `classify`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_app_middleware_rel
-- ----------------------------
CREATE TABLE `milog_app_middleware_rel`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `milog_app_id`  bigint(20) NOT NULL COMMENT 'milog app table id',
    `middleware_id` bigint(20) NOT NULL COMMENT 'middle ware ID',
    `tail_id`       bigint(20) NOT NULL COMMENT 'tailId',
    `config`        json NULL COMMENT 'config, json style',
    `ctime`         bigint(20) NOT NULL COMMENT 'create time',
    `utime`         bigint(20) NOT NULL COMMENT 'update time',
    `creator`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'creator',
    `updater`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'updater',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_app_topic_rel
-- ----------------------------
CREATE TABLE `milog_app_topic_rel`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`       bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`       bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `tenant_id`   bigint(20) NULL DEFAULT NULL COMMENT 'tenant_id',
    `app_id`      bigint(20) NOT NULL COMMENT 'app_id',
    `iam_tree_id` bigint(20) NULL DEFAULT NULL COMMENT 'Iam treeId',
    `app_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'app_name',
    `operator`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'operator',
    `mq_config`   json NULL COMMENT 'mq_config, json style',
    `source`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'app source',
    `type`        smallint(6) NULL DEFAULT NULL COMMENT '0.mione project',
    `tree_ids`    json NULL COMMENT 'mis ids',
    `node_ips`    json NULL COMMENT 'node ips',
    `creator`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_es_cluster
-- ----------------------------
CREATE TABLE `milog_es_cluster`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `tag`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cluster type',
    `name`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cluster name',
    `region`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region',
    `cluster_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cluster_name',
    `addr`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES addr',
    `user`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES user',
    `pwd`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ES pwd',
    `token`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_catalog`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `dt_database`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `area`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'area',
    `ctime`        bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`        bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `creator`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `labels`       json NULL COMMENT 'labels',
    `con_way`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'connect way: pwd,token',
    `is_default`   smallint DEFAULT '0' COMMENT 'is_default',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_es_index
-- ----------------------------
CREATE TABLE `milog_es_index`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'cluster_id',
    `log_type`   int(11) NULL DEFAULT NULL COMMENT 'log_type',
    `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index_name',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_log_count
-- ----------------------------
CREATE TABLE `milog_log_count`
(
    `id`       bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `tail_id`  bigint(20) NULL DEFAULT NULL COMMENT 'tail ID',
    `es_index` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index name',
    `day`      date NULL DEFAULT NULL COMMENT 'log data start yyyy-MM-dd',
    `number`   bigint(20) NULL DEFAULT NULL COMMENT 'number',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_log_num_alert
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_log_process
-- ----------------------------
CREATE TABLE `milog_log_process`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `tailId`          bigint(20) NULL DEFAULT NULL COMMENT 'tailId',
    `agent_id`        bigint(20) NULL DEFAULT NULL COMMENT 'agentId',
    `ip`              varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'ip',
    `file_row_number` int(32) NULL DEFAULT NULL COMMENT 'file_row_number',
    `pointer`         int(32) NULL DEFAULT NULL,
    `collect_time`    bigint(20) NULL DEFAULT NULL COMMENT 'collect_time',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'Log collection progress';

-- ----------------------------
-- Table structure for milog_log_search_save
-- ----------------------------
CREATE TABLE `milog_log_search_save`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `space_id`    int(11) NULL DEFAULT NULL,
    `store_id`    bigint(20) NULL DEFAULT NULL,
    `tail_id`     varchar(250) NULL DEFAULT NULL,
    `query_text`  varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `is_fix_time` int(11) NULL DEFAULT NULL COMMENT '1-Saved the time parameter.；0-Not saved',
    `start_time`  bigint(20) NULL DEFAULT NULL COMMENT 'search start time',
    `end_time`    bigint(20) NULL DEFAULT NULL COMMENT 'search end time',
    `common`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'common',
    `sort`        bigint(20) NULL DEFAULT NULL COMMENT 'type 1-search,2-tail,3-store',
    `order_num`   bigint(20) NULL DEFAULT NULL COMMENT 'sort',
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
CREATE TABLE `milog_log_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ctime` bigint DEFAULT NULL COMMENT 'ctime',
  `utime` bigint DEFAULT NULL COMMENT 'utime',
  `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'template_name',
  `type` int DEFAULT NULL COMMENT 'template type 0-Custom log,1-app,2-nginx',
  `support_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'support_area',
  `order_col` int DEFAULT NULL COMMENT 'sort',
  `supported_consume` smallint NOT NULL DEFAULT '1' COMMENT 'Whether to support consumption, default support is 1.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
CREATE TABLE `milog_log_template_detail`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `template_id`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'template_id',
    `properties_key`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'properties_key；1-Required,2-Suggestion,3-Hidden',
    `properties_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'properties_type',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_logstail
-- ----------------------------
CREATE TABLE `milog_logstail`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`             bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`             bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `creator`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `updater`           varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `space_id`          bigint(20) NULL DEFAULT NULL COMMENT 'spaceId',
    `store_id`          bigint(20) NULL DEFAULT NULL COMMENT 'storeId',
    `tail`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app alias name',
    `milog_app_id`      bigint(20) NULL DEFAULT NULL COMMENT 'milog table id',
    `app_id`            bigint(20) NULL DEFAULT NULL COMMENT 'app id',
    `app_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app_name',
    `app_type`          smallint(4) NULL DEFAULT NULL COMMENT '0.mione 1.mis',
    `machine_type`      smallint(4) NULL DEFAULT NULL COMMENT 'mis app machine type 0.container 1.physical machine',
    `env_id`            int(11) NULL DEFAULT NULL COMMENT 'env_id',
    `env_name`          varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'env_name',
    `parse_type`        int(11) NULL DEFAULT NULL COMMENT 'parse_type：1:Service application log，2.Separator，3：One line，4：Multiple lines，5：customize',
    `parse_script`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'For delimiters, this field specifies the delimiter; for custom, this field specifies the log reading script.',
    `log_path`          varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Comma-separated, multiple log file paths.,e.g.:/home/work/log/xxx/server.log',
    `log_split_express` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log split expression',
    `value_list`        varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Value list, separated by commas.',
    `ips`               json NULL COMMENT 'ip list',
    `motor_rooms`       json NULL COMMENT 'mis Application server room information',
    `filter`            json NULL COMMENT 'filter config',
    `en_es_index`       json NULL COMMENT 'mis Application index configuration',
    `deploy_way`        int(11) NULL DEFAULT NULL COMMENT 'deploy way: 1-mione, 2-miline, 3-k8s',
    `deploy_space`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'matrix service deployment space',
    `first_line_reg`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Regular expression at the beginning of a line',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_logstore
-- ----------------------------
CREATE TABLE `milog_logstore`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`            bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`            bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `space_id`         bigint(20) NOT NULL COMMENT 'spaceId',
    `logstoreName`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'log store Name',
    `store_period`     int(255) NULL DEFAULT NULL COMMENT 'store_period:1-3-5-7',
    `shard_cnt`        int(255) NULL DEFAULT NULL COMMENT 'Number of storage shards',
    `key_list`         varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'key list, Multiple separated by commas',
    `column_type_list` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'column type, Multiple separated by commas',
    `log_type`         varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '1:app,2:ngx..',
    `es_index`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'es index:milog_logstoreName',
    `es_cluster_id`    bigint(20) NULL DEFAULT NULL,
    `machine_room`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'machine info',
    `creator`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `mq_resource_id`   bigint(20) NULL DEFAULT NULL COMMENT 'resource mq Id',
    `is_matrix_app`    int(11) NULL DEFAULT 0 COMMENT 'is matrix app: 0=false，1=true',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_matrix_esinfo
-- ----------------------------
CREATE TABLE `milog_matrix_esinfo`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `cluster`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cluster',
    `es_catalog`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'catalog',
    `es_database` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'default',
    `es_token`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'ESToken',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_middleware_config
-- ----------------------------
CREATE TABLE `milog_middleware_config` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
   `type` smallint(6) NOT NULL COMMENT 'config type 1. rocketmq 2.talos',
   `region_en` varchar(20) DEFAULT NULL COMMENT 'region',
   `alias` varchar(255) DEFAULT NULL COMMENT 'alias',
   `name_server` varchar(255) DEFAULT NULL COMMENT 'nameServer addr',
   `service_url` varchar(255) DEFAULT NULL COMMENT 'domain',
   `ak` varchar(255) DEFAULT NULL COMMENT 'ak',
   `sk` varchar(255) DEFAULT NULL COMMENT 'sk',
   `broker_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'rocketmq addr',
   `token` varchar(255) DEFAULT NULL,
   `dt_catalog` varchar(255) DEFAULT NULL,
   `dt_database` varchar(255) DEFAULT NULL,
   `authorization` longtext DEFAULT NULL COMMENT 'Authorization information (required for HTTP interface request headers)',
   `org_id` varchar(50) DEFAULT NULL COMMENT 'Organization Id',
   `team_id` varchar(50) DEFAULT NULL COMMENT 'team Id',
   `is_default` smallint(2) DEFAULT '0' COMMENT 'Does this configuration apply by default when mq is not selected?(1.yes 0.no)',
   `ctime` bigint(20) NOT NULL COMMENT 'ctime',
   `utime` bigint(20) NOT NULL COMMENT 'utime',
   `creator` varchar(50) NOT NULL COMMENT 'creator',
   `updater` varchar(50) NOT NULL COMMENT 'updater',
   `labels` json DEFAULT NULL COMMENT 'labels',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1;
-- ----------------------------
-- Table structure for milog_region_zone
-- ----------------------------
CREATE TABLE `milog_region_zone`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `region_name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region name en',
    `region_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'region_name_cn',
    `zone_name_en`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone_name_en',
    `zone_name_cn`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'zone_name_cn',
    `ctime`          bigint(20) NULL DEFAULT NULL,
    `utime`          bigint(20) NULL DEFAULT NULL,
    `creator`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_space
-- ----------------------------
CREATE TABLE `milog_space`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `ctime`          bigint(20) NULL DEFAULT NULL COMMENT 'ctime',
    `utime`          bigint(20) NULL DEFAULT NULL COMMENT 'utime',
    `tenant_id`      int(20) NULL DEFAULT NULL COMMENT 'tenant_id',
    `space_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'space_name',
    `source`         varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'source: opensource',
    `creator`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'creator',
    `dept_id`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Department of the creator location',
    `updater`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'updater',
    `description`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'description',
    `create_dept_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `perm_dept_id`   varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_store_space_auth
-- ----------------------------
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- prometheus-agent
CREATE TABLE `prometheus_alert`
(
    `id`              int                                                            NOT NULL AUTO_INCREMENT COMMENT 'alert id',
    `name`            varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'alert name',
    `cname`           varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'alert cname',
    `expr`            varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'expr',
    `labels`          varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'labels',
    `alert_for`       varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci   NOT NULL COMMENT 'for',
    `env`             varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT NULL COMMENT 'config environment',
    `enabled`         tinyint(1) NOT NULL DEFAULT '0' COMMENT 'enabled',
    `priority`        tinyint                                                        NOT NULL COMMENT 'priority',
    `created_by`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'creator',
    `created_time`    timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time`    timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted_by`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'delete user',
    `deleted_time`    timestamp NULL DEFAULT NULL COMMENT 'deleted time',
    `prom_cluster`    varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT 'public' COMMENT 'prometheus cluster name',
    `status`          varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci   NOT NULL DEFAULT 'pending' COMMENT 'Was the configuration successfully sent: pending、success',
    `instances`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT 'Instances where the configuration takes effect, separated by commas.',
    `thresholds_op`   varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci             DEFAULT NULL COMMENT 'Multiple threshold operators, supporting "or" or "and".',
    `thresholds`      mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT 'Alarm threshold array (use this field in simple mode)',
    `type`            int                                                                     DEFAULT NULL COMMENT 'Mode, simple mode is 0, complex mode is 1.',
    `alert_member`    varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'alert_member',
    `alert_at_people` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'alert at people',
    `annotations`     varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'annotations',
    `alert_group`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT 'group',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`cname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


CREATE TABLE `scrape_config`
(
    `id`           int unsigned NOT NULL AUTO_INCREMENT,
    `prom_cluster` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'public' COMMENT 'prometheus cluster name',
    `region`       varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'region',
    `zone`         varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'zone',
    `env`          varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'config env: staging,preview,production',
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT 'Current status of the task (whether it has been successfully assigned): pending、success',
    `instances`    varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT '' COMMENT 'Example of collection tasks: separated by commas',
    `job_name`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'Name of the collection task',
    `body`         text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'scrape_config structure JSON string',
    `created_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'created_by',
    `created_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created_time',
    `updated_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'updated_time',
    `deleted_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT NULL COMMENT 'deleted_by',
    `deleted_time` timestamp NULL DEFAULT NULL COMMENT 'deleted_time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unq_job_name` (`job_name`,`deleted_by`) USING BTREE,
    KEY            `idx_prom_cluster` (`prom_cluster`),
    KEY            `idx_region` (`region`),
    KEY            `idx_zone` (`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


CREATE TABLE `silence`
(
    `id`           int                                                           NOT NULL AUTO_INCREMENT COMMENT 'silence id',
    `uuid`         varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'silence uuid',
    `comment`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'creator',
    `start_time`   timestamp                                                     NOT NULL COMMENT 'silence start time',
    `end_time`     timestamp                                                     NOT NULL COMMENT 'silence end time',
    `created_by`   varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'creator',
    `created_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'updated time',
    `prom_cluster` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT 'public' COMMENT 'prometheus cluster name',
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT 'Was the configuration successfully deployed : pending、success',
    `alert_id`     int                                                           NOT NULL DEFAULT '0' COMMENT 'alert id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


CREATE TABLE `silence_matcher`
(
    `silence_id` int                                                           NOT NULL COMMENT 'silence id',
    `name`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'name',
    `value`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'value',
    `is_regex`   tinyint(1) NOT NULL COMMENT 'if is regex matcher',
    `is_equal`   tinyint(1) NOT NULL COMMENT ' if is equal matcher'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;