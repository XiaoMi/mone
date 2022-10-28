ALTER TABLE `project_pipeline`
    ADD COLUMN `close_status` int(4) NULL;

    ALTER TABLE `project_pipeline`
    ADD COLUMN `ftime` bigint(64) NULL;