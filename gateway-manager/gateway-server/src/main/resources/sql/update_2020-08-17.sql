ALTER TABLE `project_env_deploy_setting`
  ADD COLUMN `volume` TEXT NULL DEFAULT '';

ALTER TABLE `apply_machine`
  ADD COLUMN `site_id` varchar(300) NULL DEFAULT '';