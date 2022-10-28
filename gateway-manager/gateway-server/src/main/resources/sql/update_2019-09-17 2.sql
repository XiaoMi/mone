ALTER TABLE `api_info` CHANGE COLUMN `description` `description` varchar(500) DEFAULT '' COMMENT 'api描述';
CREATE TABLE `user_collection` (
  `id` bigint(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `apiInfoId` int(11) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;