CREATE TABLE `m78_flow_base` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `work_space_id` BIGINT(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `avatar_url`  varchar(255) DEFAULT NULL,
  `state` INT DEFAULT '0',
  `publish_status` int(1) NOT NULL DEFAULT '0',
  `publish_time`   BIGINT(20)  DEFAULT NULL,
  `run_status` int(1) NOT NULL DEFAULT '0',
  `user_name` varchar(255) DEFAULT NULL,
  `ctime`  BIGINT(20) DEFAULT NULL,
  `utime`  BIGINT(20) DEFAULT NULL,
  `desc`  varchar(1024) DEFAULT NULL,
  `inputs` JSON,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_flow_setting` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `flow_base_id` BIGINT(20) NOT NULL,
  `state` INT DEFAULT '0',
  `ctime`  BIGINT(20) DEFAULT NULL,
  `utime`  BIGINT(20) DEFAULT NULL,
  `nodes` JSON,
  `edges` JSON,
  PRIMARY KEY (`id`),
  unique key unq_flow_base_id (flow_base_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;