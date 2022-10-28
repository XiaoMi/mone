CREATE TABLE `mione_menu` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `priority` int(32) DEFAULT NULL,
  `role` varchar(1024) DEFAULT NULL,
  `menu` text,
  `version` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

ALTER TABLE `project_env`
  ADD COLUMN `test_service` VARCHAR(500) NULL DEFAULT NULL;

ALTER TABLE `project_env_deploy_setting`
  ADD COLUMN `docker_params` TEXT NULL DEFAULT NULL;

CREATE TABLE `scepter_group` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `ctime` bigint(14) DEFAULT NULL,
  `utime` bigint(14) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `scepter_config` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL COMMENT 'group id',
  `type` tinyint(1) DEFAULT NULL COMMENT '1: nacos 2: api',
  `restrore_config` varchar(5000) DEFAULT NULL COMMENT '恢复配置',
  `downgrade_config` varchar(5000) DEFAULT NULL COMMENT '降级',
  `api_id` bigint(14) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0: 正常 1: 降级中',
  `passed` tinyint(1) DEFAULT NULL COMMENT '是否通过 1: 通过 0: 不通过',
  `reviewer` varchar(255) DEFAULT NULL COMMENT '审批人',
  `group_name` varchar(50) DEFAULT NULL,
  `data_id` varchar(50) DEFAULT NULL,
  `ctime` bigint(14) DEFAULT NULL,
  `utime` bigint(14) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `scepter_log` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `author` varchar(255) DEFAULT NULL,
  `log` varchar(255) DEFAULT NULL,
  `ctime` bigint(13) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
