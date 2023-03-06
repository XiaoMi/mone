CREATE DATABASE  IF NOT EXISTS `nacos_standalone`  DEFAULT CHARACTER SET utf8mb4 ;

USE `nacos_standalone`;

-- ----------------------------
-- Table structure for approval
-- ----------------------------
DROP TABLE IF EXISTS `approval`;
CREATE TABLE `approval` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_name` varchar(255) NOT NULL DEFAULT '' COMMENT '应用名',
  `approve_type` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '审批类型（10：nacos配置）',
  `operate_type` tinyint(3) NOT NULL DEFAULT 0 COMMENT '操作类型（1：新增，2：修改，3：删除）',
  `relate_key` varchar(255) NOT NULL DEFAULT '' COMMENT '关联key',
  `relate_info` varchar(255) NOT NULL DEFAULT '' COMMENT '关联信息',
  `status` tinyint(3) NOT NULL DEFAULT 0 COMMENT '状态（10：提交，20：通过，30：驳回）',
  `applicant` varchar(255) NOT NULL DEFAULT '' COMMENT '申请人',
  `apply_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '申请备注',
  `approver` varchar(255) NOT NULL DEFAULT '' COMMENT '审批人',
  `new_data` longtext DEFAULT NULL COMMENT '修改后数据',
  `old_data` longtext DEFAULT NULL COMMENT '修改前数据',
  `operator` varchar(255) NOT NULL COMMENT '处理人',
  `operate_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '处理备注',
  `approve_time` datetime NOT NULL ON UPDATE current_timestamp() COMMENT '审批时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `del` tinyint(3) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8mb3 COMMENT='审批';

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
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source user',
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
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
  `content` longtext COLLATE utf8mb3_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) COLLATE utf8mb3_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='增加租户字段';

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
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  `src_user` text COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source user',
  `src_ip` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='config_info_beta';

-- ----------------------------
-- Table structure for config_info_extend
-- ----------------------------
DROP TABLE IF EXISTS `config_info_extend`;
CREATE TABLE `config_info_extend` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_info_id` bigint(20) unsigned NOT NULL COMMENT 'config_info表主键',
  `data_id` varchar(255) NOT NULL DEFAULT '' COMMENT 'dataId',
  `env_id` varchar(255) DEFAULT '' COMMENT '环境evn id',
  `env_name` varchar(255) NOT NULL DEFAULT '' COMMENT '环境名称',
  `config_type` int(10) DEFAULT NULL COMMENT '配置类型',
  `create_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_idex_configinfoid` (`config_info_id`) USING BTREE,
  KEY `idx_configinfoextend_configtype` (`config_type`)
) ENGINE=InnoDB AUTO_INCREMENT=401 DEFAULT CHARSET=utf8mb3 COMMENT='扩展表';

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
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
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
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='集群、各Group容量信息表';

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
  `tenant_id` varchar(128) COLLATE utf8mb3_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7149722 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='多租户改造';

-- ----------------------------
-- Table structure for naming_instance
-- ----------------------------
DROP TABLE IF EXISTS `naming_instance`;
CREATE TABLE `naming_instance` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `instance_id` varchar(255) NOT NULL DEFAULT '' COMMENT '实例ID',
  `service_name` varchar(255) DEFAULT NULL COMMENT '服务名',
  `dubbo_service` varchar(255) DEFAULT NULL COMMENT 'dubbo_service',
  `dubbo_group` varchar(255) DEFAULT NULL COMMENT 'dubbo group',
  `dubbo_version` varchar(255) DEFAULT NULL COMMENT 'dubbo_versioin',
  `full_service` varchar(255) DEFAULT NULL COMMENT 'full_service',
  `application` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '应用名',
  `side` varchar(64) DEFAULT NULL COMMENT 'consumer,provider',
  `ip` varchar(255) DEFAULT NULL COMMENT '地址',
  `port` int(11) unsigned DEFAULT NULL COMMENT 'port',
  `weight` double(16,2) unsigned DEFAULT NULL COMMENT '权重',
  `healthy` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否健康',
  `enabled` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可用',
  `ephemeral` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否临时',
  `cluster_name` varchar(255) DEFAULT NULL COMMENT '集群名称',
  `namespace_id` varchar(255) DEFAULT NULL COMMENT 'nacos namespace',
  `group_name` varchar(255) DEFAULT NULL COMMENT 'nacos group',
  `metadata` varchar(1500) DEFAULT NULL COMMENT '元数据',
  `md5` varchar(255) DEFAULT NULL COMMENT 'md5',
  `last_beat_time` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT '修改时间',
  `del` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_idex_instanceId` (`instance_id`) USING BTREE,
  KEY `idx_service_name` (`service_name`) USING BTREE,
  KEY `idx_side_service` (`side`,`dubbo_service`) USING BTREE,
  KEY `idx_side_application_fullservice` (`side`,`application`,`full_service`) USING BTREE,
  KEY `idx_side_fullservice` (`side`,`full_service`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=44479122 DEFAULT CHARSET=utf8mb3 STATS_AUTO_RECALC=0 COMMENT='dubbo服务数据表';

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
  `name` varchar(255) NOT NULL COMMENT '泳道名',
  `app_env_json` text DEFAULT NULL COMMENT '应用链',
  `flow_control_tag` varchar(255) NOT NULL COMMENT '流控标签',
  `status` tinyint(1) NOT NULL COMMENT '状态',
  `condition_json` text DEFAULT NULL COMMENT '条件',
  `swim_lane_group_id` int(11) NOT NULL COMMENT ' 所属永道组id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for swim_lane_group
-- ----------------------------
DROP TABLE IF EXISTS `swim_lane_group`;
CREATE TABLE `swim_lane_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '泳道组名',
  `app_list` varchar(1024) DEFAULT NULL,
  `descp` varchar(255) DEFAULT '' COMMENT '泳道描述',
  `creator` varchar(255) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '创建时间',
  `type` int(4) unsigned zerofill NOT NULL COMMENT '泳道组类型',
  `entrance_app` varchar(255) NOT NULL DEFAULT 'tesla' COMMENT '泳道入口应用',
  `prefix_header` varchar(255) NOT NULL DEFAULT '' COMMENT '泳道前缀标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COMMENT='泳道组名';

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='租户容量信息表';

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
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='tenant_info';

-- ----------------------------
-- Table structure for threadpool_config
-- ----------------------------
DROP TABLE IF EXISTS `threadpool_config`;
CREATE TABLE `threadpool_config` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_name` varchar(256) NOT NULL DEFAULT '' COMMENT '应用名',
  `pool_name` varchar(256) NOT NULL DEFAULT '' COMMENT '线程池名',
  `core_pool_size` int(11) DEFAULT NULL COMMENT '核心线程数',
  `maximum_pool_size` int(11) DEFAULT NULL COMMENT '最大线程数',
  `keep_alive_time` int(11) DEFAULT NULL COMMENT '空闲时间',
  `capacity` int(11) DEFAULT NULL COMMENT '容量',
  `reject` int(11) DEFAULT NULL COMMENT '拒绝策略',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_threadpool_config_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb3 COMMENT='线程池';

-- ----------------------------
-- Table structure for threadpool_config_history
-- ----------------------------
DROP TABLE IF EXISTS `threadpool_config_history`;
CREATE TABLE `threadpool_config_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_id` int(10) NOT NULL COMMENT '配置id',
  `user` varchar(256) NOT NULL DEFAULT '' COMMENT '用户',
  `action` varchar(64) DEFAULT NULL COMMENT '行为',
  `content` varchar(256) DEFAULT NULL COMMENT '内容',
  `data_id` varchar(256) DEFAULT NULL COMMENT 'dataid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_threadpool_config_history_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=648 DEFAULT CHARSET=utf8mb3 COMMENT='历史记录';

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