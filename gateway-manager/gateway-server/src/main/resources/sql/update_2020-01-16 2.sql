CREATE TABLE `m_error` (
  `id` BIGINT(64) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(40) NOT NULL DEFAULT "",
  `service_name` varchar(50) NOT NULL DEFAULT "",
  `group` varchar(50) NOT NULL DEFAULT "",
  `utime` bigint(64) NOT NULL DEFAULT 0,
  `ctime` bigint(64) NOT NULL DEFAULT 0,
  `type` int(32) NOT NULL DEFAULT 0,
  `count` int(32) NOT NULL DEFAULT 0,
  `status` int(32) NOT NULL DEFAULT 0,
  `version` int(32) NOT NULL DEFAULT 0,
  `key` varchar(128) NOT NULL DEFAULT "",
  `content` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;