CREATE DATABASE  IF NOT EXISTS `nacos_standalone`  DEFAULT CHARACTER SET utf8mb4 ;

USE `nacos_standalone`;

-- ----------------------------
-- Table structure for approval
-- ----------------------------
DROP TABLE IF EXISTS `approval`;
CREATE TABLE `approval` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'app_name',
  `approve_type` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT 'approve_type（10：nacos config）',
  `operate_type` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'operate_type（1: add, 2: update, 3: delete）',
  `relate_key` varchar(255) NOT NULL DEFAULT '' COMMENT 'relate_key',
  `relate_info` varchar(255) NOT NULL DEFAULT '' COMMENT 'relate_info',
  `status` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'status（10: commit, 20: pass, 30: Reject）',
  `applicant` varchar(255) NOT NULL DEFAULT '' COMMENT 'Applicant',
  `apply_remark` varchar(255) NOT NULL DEFAULT '' COMMENT 'Application note',
  `approver` varchar(255) NOT NULL DEFAULT '' COMMENT 'Approver',
  `new_data` longtext DEFAULT NULL COMMENT 'Modified data',
  `old_data` longtext DEFAULT NULL COMMENT 'Original data',
  `operator` varchar(255) NOT NULL COMMENT 'operator',
  `operate_remark` varchar(255) NOT NULL DEFAULT '' COMMENT 'operate_remark',
  `approve_time` datetime NOT NULL ON UPDATE current_timestamp() COMMENT 'approve_time',
  `create_time` datetime NOT NULL COMMENT 'create_time',
  `update_time` datetime NOT NULL COMMENT 'update_time',
  `del` tinyint(3) NOT NULL DEFAULT 0 COMMENT 'deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8mb3 COMMENT='approve';

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `content` longtext COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'create time',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'update time',
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source user',
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `c_desc` varchar(256) COLLATE utf8mb3_bin DEFAULT NULL,
  `c_use` varchar(256) COLLATE utf8mb3_bin DEFAULT NULL,
  `effect` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL,
  `type` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL,
  `c_schema` text COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`),
  KEY `configinfo_appname_key_idx` (`app_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7149265 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info';

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'datum_id',
  `content` longtext COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `gmt_modified` datetime NOT NULL COMMENT 'update time',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant_id';

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'create time',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'update time',
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source user',
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_beta';

-- ----------------------------
-- Table structure for config_info_extend
-- ----------------------------
DROP TABLE IF EXISTS `config_info_extend`;
CREATE TABLE `config_info_extend` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `config_info_id` bigint(20) unsigned NOT NULL COMMENT 'config_info table id',
  `data_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'dataId',
  `env_id` varchar(255) DEFAULT '' COMMENT 'evn id',
  `env_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'env_name',
  `config_type` int(10) DEFAULT NULL COMMENT 'config_type',
  `create_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'create_time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_idex_configinfoid` (`config_info_id`) USING BTREE,
  KEY `idx_configinfoextend_configtype` (`config_type`)
) ENGINE=InnoDB AUTO_INCREMENT=401 DEFAULT CHARSET=utf8mb3 COMMENT='extension table';

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8mb3_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'create time',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'update time',
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source user',
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_tag';

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_tag_relation';

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID, The null character represents the entire cluster',
  `quota` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Quota. 0 indicates the default value',
  `usage` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Usage amount',
  `max_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'The maximum size of a single configuration is expressed in bytes. 0 indicates that the default value is used',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Maximum number of aggregation sub-configurations, 0 indicates that the default value is used',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'The upper limit of the subconfiguration size of a single aggregate data, in bytes, where 0 indicates the default value',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Maximum number of changes in history',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'create time',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='Capacity information of clusters and groups';

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL,
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8mb3_bin NOT NULL,
  `md5` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp(),
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL,
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL,
  `op_type` char(10) COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7149722 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant';

-- ----------------------------
-- Table structure for naming_instance
-- ----------------------------
DROP TABLE IF EXISTS `naming_instance`;
CREATE TABLE `naming_instance` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `instance_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'instance_id',
  `service_name` varchar(255) DEFAULT NULL COMMENT 'service_name',
  `dubbo_service` varchar(255) DEFAULT NULL COMMENT 'dubbo_service',
  `dubbo_group` varchar(255) DEFAULT NULL COMMENT 'dubbo group',
  `dubbo_version` varchar(255) DEFAULT NULL COMMENT 'dubbo_versioin',
  `full_service` varchar(255) DEFAULT NULL COMMENT 'full_service',
  `application` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'application',
  `side` varchar(64) DEFAULT NULL COMMENT 'consumer,provider',
  `ip` varchar(255) DEFAULT NULL COMMENT 'address',
  `port` int(11) unsigned DEFAULT NULL COMMENT 'port',
  `weight` double(16,2) unsigned DEFAULT NULL COMMENT 'weight',
  `healthy` bit(1) NOT NULL DEFAULT b'1' COMMENT 'healthy',
  `enabled` bit(1) NOT NULL DEFAULT b'1' COMMENT 'enabled',
  `ephemeral` bit(1) NOT NULL DEFAULT b'1' COMMENT 'ephemeral',
  `cluster_name` varchar(255) DEFAULT NULL COMMENT 'cluster_name',
  `namespace_id` varchar(255) DEFAULT NULL COMMENT 'nacos namespace',
  `group_name` varchar(255) DEFAULT NULL COMMENT 'nacos group',
  `metadata` varchar(1500) DEFAULT NULL COMMENT 'metadata',
  `md5` varchar(255) DEFAULT NULL COMMENT 'md5',
  `last_beat_time` datetime DEFAULT NULL COMMENT 'last_beat_time',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `update_time` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'update_time',
  `del` bit(1) DEFAULT b'0' COMMENT 'delete',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_idex_instanceId` (`instance_id`) USING BTREE,
  KEY `idx_service_name` (`service_name`) USING BTREE,
  KEY `idx_side_service` (`side`,`dubbo_service`) USING BTREE,
  KEY `idx_side_application_fullservice` (`side`,`application`,`full_service`) USING BTREE,
  KEY `idx_side_fullservice` (`side`,`full_service`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=44479122 DEFAULT CHARSET=utf8mb3 STATS_AUTO_RECALC=0 COMMENT='dubbo meta data table';

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `role` varchar(50) NOT NULL,
  `resource` varchar(512) NOT NULL,
  `action` varchar(8) NOT NULL,
  UNIQUE KEY `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `username` varchar(50) NOT NULL,
  `role` varchar(50) NOT NULL,
  UNIQUE KEY `idx_user_role` (`username`,`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 STATS_AUTO_RECALC=1;

-- ----------------------------
-- Table structure for swim_lane
-- ----------------------------
DROP TABLE IF EXISTS `swim_lane`;
CREATE TABLE `swim_lane` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL COMMENT 'name',
  `app_env_json` text DEFAULT NULL COMMENT 'Application chain',
  `flow_control_tag` varchar(255) NOT NULL COMMENT 'Flow control tag',
  `status` tinyint(1) NOT NULL COMMENT 'status',
  `condition_json` text DEFAULT NULL COMMENT 'condition',
  `swim_lane_group_id` int(11) NOT NULL COMMENT 'Group ID of Eternal Way',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for swim_lane_group
-- ----------------------------
DROP TABLE IF EXISTS `swim_lane_group`;
CREATE TABLE `swim_lane_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT 'Lane group name',
  `app_list` varchar(1024) DEFAULT NULL,
  `descp` varchar(255) DEFAULT '' COMMENT 'Lane description',
  `creator` varchar(255) NOT NULL DEFAULT '' COMMENT 'creator',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'create_time',
  `type` int(4) unsigned zerofill NOT NULL COMMENT 'Lane type',
  `entrance_app` varchar(255) NOT NULL DEFAULT 'tesla' COMMENT 'Lane app',
  `prefix_header` varchar(255) NOT NULL DEFAULT '' COMMENT 'Swim lane prefix sign',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COMMENT='Swim lane group name';

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'quota，0 default',
  `usage` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'usage',
  `max_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Maximum size limit for individual configuration, in bytes, 0 indicates default value.',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'Maximum number of aggregator configurations',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'The maximum size limit for individual sub-configuration of aggregated data, in bytes. 0 indicates using the default value.',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'The largest number of changes in history.',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'create time',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='Tenant capacity information table';

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) COLLATE utf8mb3_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT 'create time',
  `gmt_modified` bigint(20) NOT NULL COMMENT 'update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant_info';

-- ----------------------------
-- Table structure for threadpool_config
-- ----------------------------
DROP TABLE IF EXISTS `threadpool_config`;
CREATE TABLE `threadpool_config` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(256) NOT NULL DEFAULT '' COMMENT 'app_name',
  `pool_name` varchar(256) NOT NULL DEFAULT '' COMMENT 'pool_name',
  `core_pool_size` int(11) DEFAULT NULL COMMENT 'core_pool_size',
  `maximum_pool_size` int(11) DEFAULT NULL COMMENT 'maximum_pool_size',
  `keep_alive_time` int(11) DEFAULT NULL COMMENT 'keep_alive_time',
  `capacity` int(11) DEFAULT NULL COMMENT 'capacity',
  `reject` int(11) DEFAULT NULL COMMENT 'reject',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_threadpool_config_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb3 COMMENT='thread pool';

-- ----------------------------
-- Table structure for threadpool_config_history
-- ----------------------------
DROP TABLE IF EXISTS `threadpool_config_history`;
CREATE TABLE `threadpool_config_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `config_id` int(10) NOT NULL COMMENT 'config_id',
  `user` varchar(256) NOT NULL DEFAULT '' COMMENT 'user',
  `action` varchar(64) DEFAULT NULL COMMENT 'action',
  `content` varchar(256) DEFAULT NULL COMMENT 'content',
  `data_id` varchar(256) DEFAULT NULL COMMENT 'dataid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_threadpool_config_history_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=648 DEFAULT CHARSET=utf8mb3 COMMENT='history';

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(500) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `nacos_standalone`.`users` (`username`, `password`, `enabled`) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

INSERT INTO `nacos_standalone`.`roles` (`username`, `role`) VALUES ('nacos', 'ROLE_ADMIN');