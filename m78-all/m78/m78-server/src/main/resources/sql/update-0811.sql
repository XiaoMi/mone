CREATE TABLE `m78_file_management`
(
    `id`                int(11) NOT NULL AUTO_INCREMENT,
    `state`             int(11) DEFAULT NULL,
    `ctime`             bigint(20) DEFAULT NULL,
    `utime`             bigint(20) DEFAULT NULL,
    `FileID`            int(11) DEFAULT NULL,
    `FileName`          varchar(255) DEFAULT NULL,
    `FileType`          varchar(50)  DEFAULT NULL,
    `FileSize`          bigint(20) DEFAULT NULL,
    `UploadDate`        bigint(20) DEFAULT NULL,
    `LastModifiedDate`  bigint(20) DEFAULT NULL,
    `OwnerUserID`       int(11) DEFAULT NULL,
    `AccessPermissions` varchar(255) DEFAULT NULL,
    `StorageLocation`   varchar(255) DEFAULT NULL,
    `FileDescription`   text         DEFAULT NULL,
    `Status`            int(11) DEFAULT NULL,
    `content`           text         DEFAULT NULL,
    `moonshotId`        varchar(30)  DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;


CREATE TABLE `m78_recommend_carousel`
(
    `id`                BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title`             varchar(64)   DEFAULT NULL,
    `recommend_reasons` varchar(1024) DEFAULT NULL COMMENT '推荐理由',
    `type`              int(11) default 1 not null comment '类型，1bot，2plugin',
    `display_status`    int(1) default 1 not null comment '展示状态, 1展示，0不展示',
    `background_url`    varchar(255)  DEFAULT NULL,
    `bot_id`            bigint(20) NOT NULL,
    `ctime`             BIGINT(20) DEFAULT NULL,
    `utime`             BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;