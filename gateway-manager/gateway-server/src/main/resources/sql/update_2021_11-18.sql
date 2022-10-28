ALTER TABLE `project_env`  ADD COLUMN   `auto_deploy` tinyint(1) DEFAULT '0' COMMENT '是否自动部署 0-否，1-是';

CREATE TABLE `mione_docker_info_IP` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `ip` varchar(200) DEFAULT NULL,
  `pipelineId` bigint(64) DEFAULT NULL,
  `project_id` bigint(64) DEFAULT NULL,
  `env_id` bigint(64) DEFAULT NULL,
  `project_name` varchar(1000) DEFAULT NULL,
  `env_name` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
)  AUTO_INCREMENT=1
