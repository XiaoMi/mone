CREATE TABLE `mione_release_config` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(32) DEFAULT NULL,
  `project_id` bigint(64) DEFAULT NULL,
  `count` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `gw_statistics` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(64) DEFAULT NULL,
  `gw_key` varchar(50) DEFAULT NULL,
  `gw_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
