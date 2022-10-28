ALTER TABLE `project_env_deploy_setting`
	ADD COLUMN `cmd` text NULL;
ALTER TABLE `project_env_deploy_setting`
	ADD COLUMN `access_key_env_key` varchar(255) NULL;
ALTER TABLE `project_env_deploy_setting`
	ADD COLUMN `access_secret_env_key` varchar(255) NULL;

ALTER TABLE `project_env_build_setting`
    ADD COLUMN `image_name` varchar(255) NULL;
ALTER TABLE `project_env_build_setting`
    ADD COLUMN `build_cmd` text NULL;
ALTER TABLE `project_env_build_setting`
    ADD COLUMN `docker_params` text NULL;

ALTER TABLE `docker_image_info`
    ADD COLUMN `type` INT(32) NULL;
ALTER TABLE `docker_image_info`
    ADD COLUMN `project_id` BIGINT(64) NULL;

CREATE TABLE `mione_project_api` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(64) DEFAULT NULL,
  `api_id` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1;