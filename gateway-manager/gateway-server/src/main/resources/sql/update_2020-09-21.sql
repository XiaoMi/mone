CREATE TABLE `mione_test_case_setting` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `service_name` varchar(1200) DEFAULT NULL,
  `test_case_param` text,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `method` varchar(1200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;