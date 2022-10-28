alter table plugin_info add project_id int;
alter table plugin_info add flow_key varchar(100);
alter table filter_info add project_id int;

CREATE TABLE `approval` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `key` varchar(128) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `projectId` int(32) DEFAULT NULL,
  `applicantId` int(32) DEFAULT NULL,
  `auditorId` int(32) DEFAULT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `project` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `desc` varchar(100) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `gitAddress` varchar(200) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;


CREATE TABLE `project_role` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `projectId` int(32) DEFAULT NULL,
  `accountId` int(32) DEFAULT NULL,
  `roleType` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


CREATE TABLE `filter_info` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `version` varchar(20) NOT NULL,
  `author` varchar(128) DEFAULT NULL,
  `desc` varchar(128) DEFAULT NULL,
  `git_address` varchar(128) NOT NULL,
  `params` varchar(512) DEFAULT NULL,
  `data` mediumblob,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `creator` varchar(128) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `filter_info_UN` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;


