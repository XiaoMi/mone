CREATE TABLE `mione_switch` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `is_release` int(10) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;