ALTER TABLE `approval`
    ADD COLUMN `applicant_name` VARCHAR(64) NULL,
    ADD COLUMN `commit_id` VARCHAR(128) NULL,
    ADD COLUMN `env_id` BIGINT(64) NULL;

ALTER TABLE `project_env_deploy_setting`
    ADD COLUMN `replicate` BIGINT(64) NULL,
    ADD COLUMN `health_check_url` VARCHAR(128) NULL;

CREATE TABLE `nginx_service` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `service_name` varchar(128) DEFAULT NULL,
  `upstream_name` varchar(128) DEFAULT NULL,
  `my_group` varchar(128) DEFAULT NULL,
  `config_path` varchar(1024) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `project_pipeline`
    ADD COLUMN `deploy_setting` JSON NULL,
    ADD COLUMN `deploy_result` JSON NULL,
    ADD COLUMN `deploy_info` JSON NULL;

ALTER TABLE `project_code_check_record`
    ADD COLUMN `pipeline_id` BIGINT(64) NULL;

