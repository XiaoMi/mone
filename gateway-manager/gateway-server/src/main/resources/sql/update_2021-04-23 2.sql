CREATE TABLE `sonarqube_config`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL COMMENT '项目id',
  `branch` varchar(255)  NOT NULL COMMENT 'branch',
  `project_key` varchar(255)  NOT NULL COMMENT 'project key',
  `profile` varchar(255)  NOT NULL COMMENT 'maven profile',
  `status` tinyint(1) NOT NULL COMMENT ' 0表示on 其他表示off',
  `task_id` bigint(20) NOT NULL COMMENT 'mischedule task id',
  `ctime` bigint(20) NOT NULL,
  `utime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_project_id`(`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;