ALTER TABLE `project_pipeline`
    ADD COLUMN `version` INT(32);

ALTER TABLE `project_env_build_setting`
ADD COLUMN `xml_setting` INT(32) DEFAULT 0;