ALTER TABLE `debug_record`
    ADD COLUMN `gateway_svr_url` VARCHAR(500) NULL;

ALTER TABLE `project_env_build_setting`
    ADD COLUMN `java_home` VARCHAR(1000) NULL;