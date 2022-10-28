ALTER TABLE `filter_info` ADD COLUMN `groups` VARCHAR(2048) NULL;
ALTER TABLE `project_env_deploy_setting` ADD COLUMN `dockerfile_path` VARCHAR(2048) NOT NULL DEFAULT '';
