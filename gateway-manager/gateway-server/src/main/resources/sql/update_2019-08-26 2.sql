ALTER TABLE filter_info ADD is_system tinyint(1) DEFAULT 0 NULL;

CREATE TABLE `user_rate` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `the_rate_id` int(32) DEFAULT NULL,
  `account_id` int(11) unsigned NOT NULL,
  `type` int(32) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_rate_FK` (`account_id`),
  CONSTRAINT `user_rate_FK` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='用户评分表';
