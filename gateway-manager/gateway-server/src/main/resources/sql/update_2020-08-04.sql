ALTER TABLE `project_env_build_setting`
  ADD COLUMN `custom_params` VARCHAR(256) NULL DEFAULT '';


ALTER TABLE `project`
ADD COLUMN `deploy_limit` integer(32) NOT NULL DEFAULT 3;