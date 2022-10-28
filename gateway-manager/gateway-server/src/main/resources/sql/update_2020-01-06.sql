CREATE TABLE `project_java_doc` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(64) NOT NULL,
  `doc` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8