ALTER TABLE `api_info` ADD COLUMN `priority` INT(32) NULL COMMENT 'priority';

CREATE TABLE `project_compilation` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` int(32) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `jar_name` varchar(512) DEFAULT NULL,
  `branch` varchar(128) DEFAULT NULL,
  `url` varchar(512) DEFAULT NULL,
  `deploy_status` int(32) DEFAULT NULL,
  `jar_key` varchar(512) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `step` int(32) DEFAULT NULL,
  `param_setting` varchar(1024) DEFAULT NULL,
  `profile` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8;

 CREATE TABLE `project_deploy_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `jar_id` bigint(64) DEFAULT NULL,
  `jar_name` varchar(128) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `operatio` varchar(128) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  `project_id` int(32) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

