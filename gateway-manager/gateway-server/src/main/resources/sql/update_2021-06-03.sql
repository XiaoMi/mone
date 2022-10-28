CREATE TABLE `mione_auditing` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `operator` varchar(200) DEFAULT NULL,
  `type` varchar(200) DEFAULT NULL,
  `attachment` text,
  `version` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;