ALTER TABLE `project_env_deploy_setting`
    ADD COLUMN `jaeger` TINYINT(1) NULL AFTER `access_secret_env_key`;