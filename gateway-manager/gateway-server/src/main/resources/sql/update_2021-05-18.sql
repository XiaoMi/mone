ALTER TABLE `nginx_service`
    ADD COLUMN `port` BIGINT(64) NULL;

ALTER TABLE `nginx_service`
    ADD COLUMN `env_id` BIGINT(64) NULL;

ALTER TABLE `nginx_service`
    ADD COLUMN `sdk` TINYINT(1) NULL;
