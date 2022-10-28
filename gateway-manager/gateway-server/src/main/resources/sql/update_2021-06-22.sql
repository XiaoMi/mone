ALTER TABLE `project_env` ADD COLUMN web_host VARCHAR(100);
ALTER TABLE `project_env` ADD COLUMN staging_tag VARCHAR(100);
ALTER TABLE `project_env` ADD COLUMN many_staging INT(10);
ALTER TABLE `project_pipeline`
    ADD COLUMN `tag_name` varchar(128) NULL;
