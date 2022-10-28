ALTER TABLE `project_env`
    ADD COLUMN `tenement` VARCHAR(500) NULL;

CREATE TABLE `mione_custom_config` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `content` text,
  `version` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
