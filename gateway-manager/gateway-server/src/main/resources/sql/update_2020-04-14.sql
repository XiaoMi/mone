CREATE TABLE `docker_image_info` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_name` varchar(128) NOT NULL DEFAULT "",
  `project_name` varchar(20) NOT NULL DEFAULT "",
  `desc` varchar(128) NOT NULL DEFAULT "",
  `git_address` varchar(128) NOT NULL DEFAULT "",
  `commit_id` varchar(128) NOT NULL DEFAULT "",
  `compilation_id` bigint(64) NOT NULL DEFAULT 0,
  `ctime` bigint(64) NOT NULL DEFAULT 0,
  `utime` bigint(64) NOT NULL DEFAULT 0,
  `creator` varchar(128) NOT NULL DEFAULT "",
  `updater` varchar(128) NOT NULL DEFAULT "",
  `status` int(32) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
