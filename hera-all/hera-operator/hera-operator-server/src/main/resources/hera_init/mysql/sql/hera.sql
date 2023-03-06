CREATE
DATABASE  IF NOT EXISTS `hera`  DEFAULT CHARACTER SET utf8mb4 ;

USE
`hera`;

-- trace-etl

CREATE TABLE `hera_trace_etl_config`
(
    `id`                        int NOT NULL AUTO_INCREMENT,
    `base_info_id`              int                              DEFAULT NULL COMMENT 'hera_base_info表的id',
    `exclude_method`            varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用操作过滤',
    `exclude_httpserver_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'httpServer端过滤的应用操作',
    `exclude_thread`            varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '线程名称过滤',
    `exclude_sql`               varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'sql过滤',
    `exclude_http_url`          varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'url过滤',
    `exclude_ua`                varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'user-agent过滤',
    `http_slow_threshold`       int                              DEFAULT NULL COMMENT 'http慢查询阈值',
    `dubbo_slow_threshold`      int                              DEFAULT NULL COMMENT 'dubbo慢查询阈值',
    `mysql_slow_threshold`      int                              DEFAULT NULL COMMENT 'mysql慢查询阈值',
    `trace_filter`              int                              DEFAULT NULL COMMENT 'trace需要存入es的百分比',
    `trace_duration_threshold`  int                              DEFAULT NULL COMMENT 'trace存入es的耗时阈值',
    `trace_debug_flag`          varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'trace存入es的debug标识，对应heraContext的key',
    `http_status_error`         varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些http状态码不显示在异常列表',
    `exception_error`           varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些exception不算异常请求',
    `grpc_code_error`           varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些grpc_code不算异常请求',
    `status`                    varchar(2) COLLATE utf8mb4_bin   DEFAULT '1' COMMENT '是否有效 0无效  1有效',
    `create_time`               datetime                         DEFAULT NULL,
    `update_time`               datetime                         DEFAULT NULL,
    `create_user`               varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `update_user`               varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- mimonitor

DROP TABLE IF EXISTS `alert_group`;
CREATE TABLE `alert_group`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(64) NOT NULL COMMENT '名称',
    `desc`        varchar(256) DEFAULT NULL COMMENT '备注',
    `chat_id`     varchar(125) DEFAULT NULL COMMENT '飞书ID',
    `creater`     varchar(64)  DEFAULT NULL COMMENT '创建人',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `rel_id`      bigint(20) DEFAULT '0' COMMENT '关联ID',
    `type`        varchar(32)  DEFAULT 'alert' COMMENT '告警类型',
    `deleted`     int(1) DEFAULT '0' COMMENT '0正常,1删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_group_member
-- ----------------------------
DROP TABLE IF EXISTS `alert_group_member`;
CREATE TABLE `alert_group_member`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `member_id`      bigint(20) DEFAULT '0' COMMENT '会员ID',
    `alert_group_id` bigint(20) DEFAULT '0' COMMENT '告警组ID',
    `creater`        varchar(64) DEFAULT NULL COMMENT '创建人',
    `create_time`    timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`    timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `member`         varchar(64) DEFAULT '' COMMENT '用户',
    `deleted`        int(1) DEFAULT '0' COMMENT '0正常,1删除',
    PRIMARY KEY (`id`),
    KEY              `idx_member_id` (`member_id`),
    KEY              `idx_alert_group_id` (`alert_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_alarm_rule
-- ----------------------------
DROP TABLE IF EXISTS `app_alarm_rule`;
CREATE TABLE `app_alarm_rule`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `alarm_id`    int(11) DEFAULT NULL COMMENT '报警id，对应prometheus报警接口的报警Id',
    `alert`       varchar(255) NOT NULL COMMENT '报警名称',
    `cname`       varchar(255) DEFAULT NULL COMMENT '报警别名',
    `metric_type` int(11) DEFAULT NULL COMMENT '指标类型0预置指标 1用户自定义指标',
    `expr`        text         DEFAULT NULL COMMENT '表达式',
    `for_time`    varchar(50)  NOT NULL COMMENT '持续时间',
    `labels`      text         DEFAULT NULL COMMENT 'label',
    `annotations` varchar(255) DEFAULT NULL COMMENT '告警描述信息',
    `rule_group`  varchar(50)  DEFAULT NULL COMMENT 'rule-group',
    `priority`    varchar(20)  DEFAULT NULL COMMENT '告警级别',
    `alert_team`  text         DEFAULT NULL COMMENT '告警组json',
    `env`         varchar(100) DEFAULT NULL COMMENT '环境',
    `op`          varchar(5)   DEFAULT NULL COMMENT '操作符',
    `value`       float(255, 2
) DEFAULT NULL COMMENT '阈值',
 `data_count` int(11) DEFAULT NULL COMMENT '最近数据点次数',
 `send_interval` varchar(20) DEFAULT NULL COMMENT '告警发送间隔',
 `project_id` int(11) DEFAULT NULL COMMENT '项目id',
 `strategy_id` int(11) unsigned DEFAULT '0' COMMENT '策略id',
 `iam_id` int(11) DEFAULT NULL COMMENT 'iamId',
 `template_id` int(11) DEFAULT NULL COMMENT '模版id',
 `rule_type` int(11) DEFAULT NULL COMMENT '规则类型 0模版规则 1应用配置规则 ',
 `rule_status` int(11) DEFAULT NULL COMMENT '0 生效 1暂停',
 `remark` varchar(255) DEFAULT NULL COMMENT '备注',
 `creater` varchar(64) DEFAULT NULL COMMENT '创建人',
 `status` int(11) DEFAULT NULL COMMENT '状态0有效 1删除',
 `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
 `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_alarm_rule_template
-- ----------------------------
DROP TABLE IF EXISTS `app_alarm_rule_template`;
CREATE TABLE `app_alarm_rule_template`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `name`          varchar(255) NOT NULL COMMENT '模版名称',
    `type`          int(11) NOT NULL COMMENT '类型 0 system 1 user',
    `remark`        varchar(255) DEFAULT NULL COMMENT '备注',
    `creater`       varchar(64)  DEFAULT NULL COMMENT '创建人',
    `status`        int(11) DEFAULT NULL COMMENT '状态：0有效1删除',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`   timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `strategy_type` int(11) DEFAULT '0' COMMENT '策略类型',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



-- ----------------------------
-- Table structure for app_alarm_strategy
-- ----------------------------
DROP TABLE IF EXISTS `app_alarm_strategy`;
CREATE TABLE `app_alarm_strategy`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `iamId`         int(11) DEFAULT '0',
    `appId`         int(11) NOT NULL,
    `appName`       varchar(100) DEFAULT NULL COMMENT '应用名称',
    `strategy_type` int(11) DEFAULT NULL COMMENT '策略类别',
    `strategy_name` varchar(100) DEFAULT NULL COMMENT '策略名称',
    `desc`          varchar(255) DEFAULT NULL COMMENT '描述',
    `creater`       varchar(64)  DEFAULT NULL COMMENT '创建人',
    `create_time`   timestamp NULL DEFAULT NULL,
    `update_time`   timestamp NULL DEFAULT NULL,
    `status`        tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
    `alert_team`    text         DEFAULT NULL COMMENT '报警组',
    `group3`        varchar(32)  DEFAULT '' COMMENT '三级组织',
    `group4`        varchar(32)  DEFAULT '' COMMENT '四级组织',
    `group5`        varchar(32)  DEFAULT '' COMMENT '五级组织',
    `envs`          text         DEFAULT NULL COMMENT '环境设置',
    `alert_members` text         DEFAULT NULL COMMENT '报警人列表',
    `at_members`    text         DEFAULT NULL COMMENT 'At人员列表',
    `services`      text         DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_capacity_auto_adjust
-- ----------------------------
DROP TABLE IF EXISTS `app_capacity_auto_adjust`;
CREATE TABLE `app_capacity_auto_adjust`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `app_id`        int(11) NOT NULL,
    `pipeline_id`   int(11) NOT NULL COMMENT '流水线（环境）id',
    `container`     varchar(255) DEFAULT NULL COMMENT '容器名称',
    `status`        int(3) DEFAULT NULL COMMENT '0可用，1不可用',
    `min_instance`  int(8) DEFAULT NULL COMMENT '最小实例数',
    `max_instance`  int(8) DEFAULT NULL COMMENT '最大实例数',
    `auto_capacity` int(3) DEFAULT NULL COMMENT '自动扩容 1是 0否',
    `depend_on`     int(3) DEFAULT NULL COMMENT '扩容依据 0 cpu 1内存 2cpu及内存 depend_on',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`   timestamp NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique-pipleline` (`app_id`,`pipeline_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_capacity_auto_adjust_record
-- ----------------------------
DROP TABLE IF EXISTS `app_capacity_auto_adjust_record`;
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
DROP TABLE IF EXISTS `app_grafana_mapping`;
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
DROP TABLE IF EXISTS `app_monitor`;
CREATE TABLE `app_monitor`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `project_id`     int(11) DEFAULT NULL COMMENT '项目id',
    `iam_tree_id`    int(11) DEFAULT NULL COMMENT 'iamTreeId',
    `project_name`   varchar(255) DEFAULT NULL COMMENT '项目名称',
    `app_source`     int(8) DEFAULT '0' COMMENT 'app来源 0-开源',
    `owner`          varchar(30)  DEFAULT NULL COMMENT '所属者',
    `care_user`      varchar(30)  DEFAULT NULL COMMENT '关注者',
    `alarm_level`    int(11) DEFAULT NULL COMMENT '报警级别',
    `total_alarm`    int(11) DEFAULT NULL COMMENT '应用报警总数',
    `exception_num`  int(11) DEFAULT NULL COMMENT '异常数',
    `slow_query_num` int(11) DEFAULT NULL COMMENT '慢查询数',
    `status`         int(11) DEFAULT NULL COMMENT '状态 0有效1删除',
    `base_info_id`   int(11) DEFAULT NULL COMMENT '基本信息id',
    `create_time`    timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`    timestamp NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_quality_market
-- ----------------------------
DROP TABLE IF EXISTS `app_quality_market`;
CREATE TABLE `app_quality_market`
(
    `id`           int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动递增id',
    `market_name`  varchar(255) NOT NULL DEFAULT '' COMMENT '大盘名称',
    `creator`      varchar(100)          DEFAULT '' COMMENT '创建者',
    `service_list` TEXT                  DEFAULT NULL COMMENT '应用列表分号分割多个',
    `last_updater` varchar(100)          DEFAULT '' COMMENT '上一次更新人',
    `remark`       varchar(255)          DEFAULT '' COMMENT '备注',
    `create_time`  datetime              DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY            `key_market_name` (`market_name`),
    KEY            `key_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_scrape_job
-- ----------------------------
DROP TABLE IF EXISTS `app_scrape_job`;
CREATE TABLE `app_scrape_job`
(
    `id`          int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `iam_id`      int(11) NOT NULL COMMENT 'iam树的id',
    `user`        varchar(64)  NOT NULL DEFAULT '' COMMENT '操作用户',
    `job_json`    text                  DEFAULT NULL COMMENT '抓取配置json',
    `message`     varchar(255) NOT NULL DEFAULT '' COMMENT '请求返回的信息',
    `data`        varchar(255)          DEFAULT '' COMMENT '成功则是请求返回的抓取id',
    `job_name`    varchar(64)           DEFAULT NULL COMMENT '抓取的job的名字',
    `status`      tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT 'job状态 0创建失败 1创建成功 2 已删除',
    `job_desc`    varchar(255)          DEFAULT '' COMMENT 'job描述',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_service_market
-- ----------------------------
DROP TABLE IF EXISTS `app_service_market`;
CREATE TABLE `app_service_market`
(
    `id`           int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `market_name`  varchar(150) NOT NULL DEFAULT '' COMMENT '大盘名称',
    `belong_team`  varchar(150) NOT NULL DEFAULT '' COMMENT '所属团队',
    `creator`      varchar(50)           DEFAULT '' COMMENT '创建者',
    `service_list` TEXT COMMENT '应用列表分号分割多个',
    `last_updater` varchar(50)           DEFAULT '' COMMENT '上一次更新人',
    `remark`       varchar(255)          DEFAULT '' COMMENT '备注',
    `service_type` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '服务类别',
    `create_time`  datetime              DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY            `key_market_name` (`market_name`),
    KEY            `key_creator` (`creator`),
    KEY            `key_belong_team` (`belong_team`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_tesla_alarm_rule
-- ----------------------------
DROP TABLE IF EXISTS `app_tesla_alarm_rule`;
CREATE TABLE `app_tesla_alarm_rule`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) DEFAULT NULL COMMENT '警报名称',
    `tesla_group` varchar(100) NOT NULL COMMENT 'tesla组',
    `alert_type`  varchar(50)  DEFAULT NULL COMMENT '报警类型',
    `exper`       text         DEFAULT NULL COMMENT '表达式',
    `op`          varchar(2)   DEFAULT NULL COMMENT '操作符',
    `value`       float(11, 2
) DEFAULT NULL COMMENT '阈值',
 `duration` varchar(20) DEFAULT NULL COMMENT '持续时间',
 `remark` varchar(255) DEFAULT NULL COMMENT '备注',
 `type` int(11) DEFAULT NULL COMMENT '类型',
 `status` int(11) DEFAULT NULL COMMENT '状态',
 `creater` varchar(64) DEFAULT NULL COMMENT '创建人',
 `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
 `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for app_tesla_feishu_mapping
-- ----------------------------
DROP TABLE IF EXISTS `app_tesla_feishu_mapping`;
CREATE TABLE `app_tesla_feishu_mapping`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `tesla_group`     varchar(50) NOT NULL COMMENT 'tesla分组名',
    `feishu_group_id` varchar(50) NOT NULL COMMENT '飞书群id',
    `remark`          varchar(255) DEFAULT NULL COMMENT '备注',
    `creater`         varchar(64)  DEFAULT NULL COMMENT '创建者',
    `status`          int(11) DEFAULT NULL COMMENT '状态',
    `create_time`     timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`     timestamp NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_base_info
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_base_info`;
CREATE TABLE `hera_app_base_info`  (
       `id` int NOT NULL AUTO_INCREMENT,
       `bind_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '绑定的id',
       `bind_type` int NOT NULL COMMENT '绑定类型(0 appId 1 iamTreeId)',
       `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用名称',
       `app_cname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用中文名称',
       `app_type` int NOT NULL COMMENT '应用类型-关联指标监控模版（0业务应用 1主机应用）',
       `app_language` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '语言类型',
       `platform_type` int NOT NULL COMMENT '平台类型',
       `app_sign_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用指标唯一性标识',
       `iam_tree_id` int NULL DEFAULT NULL COMMENT 'iam_tree_id(报警接口必须)',
       `envs_map` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '环境列表',
       `auto_capacity` int NULL DEFAULT NULL COMMENT '自动扩容 1是，0否',
       `status` int NULL DEFAULT NULL COMMENT '状态',
       `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
       `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
       PRIMARY KEY (`id`) USING BTREE,
       UNIQUE INDEX `idx_uniqe_app`(`bind_id`, `platform_type`) USING BTREE,
       INDEX `idx_app_name`(`app_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_excess_info
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_excess_info`;
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
DROP TABLE IF EXISTS `hera_app_env`;
CREATE TABLE `hera_app_env`  (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `hera_app_id` bigint NOT NULL COMMENT 'hera_app_base_info表的主键',
     `app_id` bigint NOT NULL COMMENT '真实应用的主键',
     `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '应用名称',
     `env_id` bigint NULL DEFAULT NULL COMMENT '环境id(来自于同步信息)',
     `env_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '环境名',
     `ip_list` json NULL COMMENT 'ip信息（这里存储的都是最终的信息）',
     `ctime` bigint NOT NULL COMMENT '创建时间（毫秒时间戳）',
     `creator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
     `utime` bigint NULL DEFAULT NULL COMMENT '更新时间(毫秒时间戳)',
     `updater` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for hera_app_role
-- ----------------------------
DROP TABLE IF EXISTS `hera_app_role`;
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
DROP TABLE IF EXISTS `hera_oper_log`;
CREATE TABLE `hera_oper_log`
(
    `id`               bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `oper_name`        varchar(64) NOT NULL COMMENT '操作者',
    `log_type`         int(1) DEFAULT '0' COMMENT '0概况，1明细',
    `before_parent_id` bigint(11) DEFAULT '0' COMMENT '明细所属概况id',
    `module_name`      varchar(64)  DEFAULT '' COMMENT '模块名称',
    `interface_name`   varchar(64)  DEFAULT '' COMMENT '接口名称',
    `interface_url`    varchar(128) DEFAULT '' COMMENT '接口链接',
    `action`           varchar(32)  DEFAULT '' COMMENT '行为',
    `before_data`      text         DEFAULT NULL COMMENT '操作前数据',
    `after_data`       text         DEFAULT NULL COMMENT '操作后数据',
    `create_time`      timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`      timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `data_type`        int(1) DEFAULT '0' COMMENT '0未知，1策略，2规则',
    `after_parent_id`  bigint(11) DEFAULT '0' COMMENT '明细所属概况id',
    `result_desc`      varchar(128) DEFAULT '' COMMENT '结果',
    PRIMARY KEY (`id`),
    KEY                `idx_before_parent_id` (`before_parent_id`),
    KEY                `idx_oper_name` (`oper_name`),
    KEY                `idx_after_parent_id` (`after_parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for rules
-- ----------------------------
DROP TABLE IF EXISTS `rules`;
CREATE TABLE `rules`
(
    `rule_id`          int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '规则id',
    `rule_name`        varchar(255) DEFAULT '' COMMENT '规则所属组的名称',
    `rule_fn`          varchar(255) DEFAULT '' COMMENT '类别',
    `rule_interval`    int(11) DEFAULT NULL COMMENT '规则计算间隔',
    `rule_alert`       varchar(255) DEFAULT '' COMMENT '告警名称',
    `rule_expr`        text         DEFAULT NULL COMMENT '表达式',
    `rule_for`         varchar(255) DEFAULT '' COMMENT '持续时间',
    `rule_labels`      varchar(255) DEFAULT '' COMMENT '规则维度信息',
    `rule_annotations` text         DEFAULT NULL COMMENT '规则描述信息',
    `principal`        varchar(255) DEFAULT NULL COMMENT '负责人邮箱前缀逗号分隔',
    `create_time`      date         DEFAULT NULL COMMENT '创建时间',
    `update_time`      date         DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`rule_id`),
    UNIQUE KEY `unique_key` (`rule_alert`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_scrape_job
-- ----------------------------
DROP TABLE IF EXISTS `app_scrape_job`;
CREATE TABLE `app_scrape_job`
(
    `id`          int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `iam_id`      int(11) NOT NULL COMMENT 'iam树的id',
    `user`        varchar(64)  NOT NULL DEFAULT '' COMMENT '操作用户',
    `job_json`    text                  DEFAULT NULL COMMENT '抓取配置json',
    `message`     varchar(255) NOT NULL DEFAULT '' COMMENT '请求返回的信息',
    `data`        varchar(255)          DEFAULT '' COMMENT '成功则是请求返回的抓取id',
    `job_name`    varchar(64)           DEFAULT NULL COMMENT '抓取的job的名字',
    `status`      tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT 'job状态 0创建失败 1创建成功 2 已删除',
    `job_desc`    varchar(255)          DEFAULT '' COMMENT 'job描述',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for rule_promql_template
-- ----------------------------
DROP TABLE IF EXISTS `rule_promql_template`;
CREATE TABLE `rule_promql_template`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL COMMENT '模版名称',
    `promql`      varchar(512) DEFAULT NULL COMMENT 'promql',
    `type`        int(11) NOT NULL COMMENT '类型 0 system 1 user',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `creater`     varchar(64)  DEFAULT '' COMMENT '创建人',
    `status`      int(11) DEFAULT NULL COMMENT '状态：0有效',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_creater` (`creater`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- ----------------------------
-- Table structure for app_monitor_config
-- ----------------------------
DROP TABLE IF EXISTS `app_monitor_config`;
CREATE TABLE `app_monitor_config`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `project_id`  int          NOT NULL COMMENT '项目id',
    `config_type` int          NOT NULL COMMENT '配置类型 0慢查询时间',
    `config_name` varchar(50)  NOT NULL COMMENT '配置名称',
    `value`       varchar(255) NOT NULL COMMENT '配置值',
    `status`      int          NOT NULL,
    `create_time` timestamp NULL DEFAULT NULL,
    `update_time` timestamp NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for mione_grafana_template
-- ----------------------------
DROP TABLE IF EXISTS `mione_grafana_template`;
CREATE TABLE `mione_grafana_template`
(
    `id`            int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `name`          varchar(64) NOT NULL DEFAULT '' COMMENT '模板名字',
    `template`      longtext             DEFAULT NULL COMMENT '模板json',
    `platform`      int(11) DEFAULT NULL COMMENT '平台',
    `language`      int(11) DEFAULT NULL COMMENT '语言',
    `app_type`      int(11) DEFAULT NULL COMMENT '应用类型',
    `panel_id_list` text                 DEFAULT NULL COMMENT '图表id列表',
    `url_param`     text                 DEFAULT NULL COMMENT 'url参数',
    `create_time`   timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`   timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint(1) DEFAULT '0' COMMENT '0未删除1删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1;

SET
FOREIGN_KEY_CHECKS = 1;

-- milog

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
    INDEX         `alert_log_app_name_ip_starttime_IDX`(`app_name`, `ip`, `start_time`) USING BTREE,
    INDEX         `alert_log_app_name_starttime_IDX`(`app_name`, `start_time`) USING BTREE,
    INDEX         `alert_log_ip_starttime_IDX`(`ip`, `start_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for alert_rule
-- ----------------------------
DROP TABLE IF EXISTS `alert_rule`;
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 167010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
    `type`        smallint(6) NULL DEFAULT NULL COMMENT '0.mione 项目',
    `tree_ids`    json NULL COMMENT 'mis 项目的挂载的树ids',
    `node_ips`    json NULL COMMENT '应用所在的物理机ips',
    `creator`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `updater`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
    `is_default`   smallint DEFAULT '0' COMMENT '是否默认，管理员操作的是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '日志收集进度';

-- ----------------------------
-- Table structure for milog_log_search_save
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_search_save`;
CREATE TABLE `milog_log_search_save`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `space_id`    int(11) NULL DEFAULT NULL,
    `store_id`    bigint(20) NULL DEFAULT NULL,
    `tail_id`     int(11) NULL DEFAULT NULL,
    `query_text`  varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
    `is_fix_time` int(11) NULL DEFAULT NULL COMMENT '1-保存了时间参数；0-没有保存',
    `start_time`  bigint(20) NULL DEFAULT NULL COMMENT '搜索开始时间',
    `end_time`    bigint(20) NULL DEFAULT NULL COMMENT '搜索结束时间',
    `common`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `sort`        bigint(20) NULL DEFAULT NULL COMMENT '分类 1-搜索词,2-tail,3-store',
    `order_num`   bigint(20) NULL DEFAULT NULL COMMENT '排序',
    `creator`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
    `updater`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '最后更新人',
    `create_time` bigint(20) NULL DEFAULT NULL,
    `update_time` bigint(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template`;
CREATE TABLE `milog_log_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ctime` bigint DEFAULT NULL COMMENT '创建时间',
  `utime` bigint DEFAULT NULL COMMENT '更新时间',
  `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '日志模板名称\r\n',
  `type` int DEFAULT NULL COMMENT '日志模板类型0-自定义日志,1-app,2-nginx',
  `support_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '支持机房',
  `order_col` int DEFAULT NULL COMMENT '排序',
  `supported_consume` smallint NOT NULL DEFAULT '1' COMMENT '是否支持消费，默认支持1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ctime`           bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
    `utime`           bigint(20) NULL DEFAULT NULL COMMENT '更新时间',
    `template_id`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板ID\r\n',
    `properties_key`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性名；1-必选,2-建议,3-隐藏',
    `properties_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志模板属性类型\r\n',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
    `deploy_way`        int(11) NULL DEFAULT NULL COMMENT '部署方式：1-mione, 2-miline, 3-k8s',
    `deploy_space`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'matrix服务部署空间',
    `first_line_reg`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '行首正则表达式',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- Table structure for milog_middleware_config
-- ----------------------------
DROP TABLE IF EXISTS `milog_middleware_config`;
CREATE TABLE `milog_middleware_config` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `type` smallint(6) NOT NULL COMMENT '配置 1. rocketmq 2.talos',
   `region_en` varchar(20) DEFAULT NULL COMMENT '不同的机房\r\n',
   `alias` varchar(255) DEFAULT NULL COMMENT '别名\r\n',
   `name_server` varchar(255) DEFAULT NULL COMMENT 'nameServer地址\r\n',
   `service_url` varchar(255) DEFAULT NULL COMMENT '域名\r\n',
   `ak` varchar(255) DEFAULT NULL COMMENT 'ak',
   `sk` varchar(255) DEFAULT NULL COMMENT 'sk',
   `broker_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'rocketmq地址',
   `token` varchar(255) DEFAULT NULL,
   `dt_catalog` varchar(255) DEFAULT NULL,
   `dt_database` varchar(255) DEFAULT NULL,
   `authorization` longtext DEFAULT NULL COMMENT '授权信息(http接口请求头需要)\r\n',
   `org_id` varchar(50) DEFAULT NULL COMMENT '组织Id\r\n',
   `team_id` varchar(50) DEFAULT NULL COMMENT '用户组Id\r\n',
   `is_default` smallint(2) DEFAULT '0' COMMENT '是否默认当不选择mq的时候采用这个配置(1.是 0.否)',
   `ctime` bigint(20) NOT NULL COMMENT '创建时间\r\n',
   `utime` bigint(20) NOT NULL COMMENT '更新时间\r\n',
   `creator` varchar(50) NOT NULL COMMENT '创建人\r\n',
   `updater` varchar(50) NOT NULL COMMENT '更新人\r\n',
   `labels` json DEFAULT NULL COMMENT '标签',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1;
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin;

-- ----------------------------
-- prometheus-agent
DROP TABLE IF EXISTS `prometheus_alert`;
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
    `status`          varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci   NOT NULL DEFAULT 'pending' COMMENT '配置是否下发成功：pending、success',
    `instances`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT '配置生效的实例，逗号分隔多个',
    `thresholds_op`   varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci             DEFAULT NULL COMMENT '多阈值操作符，支持 or （或）或 and （且）',
    `thresholds`      mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '告警阈值数组（简易模式使用该字段）',
    `type`            int                                                                     DEFAULT NULL COMMENT '模式，简易模式为 0，复杂模式为 1',
    `alert_member`    varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'alert_member',
    `alert_at_people` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'alert at people',
    `annotations`     varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '注释',
    `alert_group`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT 'group',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`cname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


DROP TABLE IF EXISTS `scrape_config`;
CREATE TABLE `scrape_config`
(
    `id`           int unsigned NOT NULL AUTO_INCREMENT,
    `prom_cluster` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'public' COMMENT 'prometheus 集群名称',
    `region`       varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '区域',
    `zone`         varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '可用区',
    `env`          varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '配置环境：staging,preview,production',
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT '任务当前状态（是否下发成功）：pending、success',
    `instances`    varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT '' COMMENT '采集任务的实例：逗号分隔多个',
    `job_name`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '采集任务的名称',
    `body`         text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'scrape_config 结构体 json 字符串',
    `created_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `created_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT NULL COMMENT '删除人',
    `deleted_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unq_job_name` (`job_name`,`deleted_by`) USING BTREE,
    KEY            `idx_prom_cluster` (`prom_cluster`),
    KEY            `idx_region` (`region`),
    KEY            `idx_zone` (`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


DROP TABLE IF EXISTS `silence`;
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
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT '配置是否下发成功：pending、success',
    `alert_id`     int                                                           NOT NULL DEFAULT '0' COMMENT 'alert id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


DROP TABLE IF EXISTS `silence_matcher`;
CREATE TABLE `silence_matcher`
(
    `silence_id` int                                                           NOT NULL COMMENT 'silence id',
    `name`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'name',
    `value`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'value',
    `is_regex`   tinyint(1) NOT NULL COMMENT 'if is regex matcher',
    `is_equal`   tinyint(1) NOT NULL COMMENT ' if is equal matcher'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;