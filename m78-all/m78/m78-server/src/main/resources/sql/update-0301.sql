CREATE TABLE `m78_workspace`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(64)  NOT NULL COMMENT 'workspace名称',
    `avatar_url`  varchar(255)          DEFAULT NULL,
    `remark`      varchar(1024)         DEFAULT NULL COMMENT '描述',
    `owner`       varchar(128) NOT NULL COMMENT '所有者',
    `deleted`     int(1) NOT NULL COMMENT '是否删除0-否 1-是',
    `creator`     varchar(128) NOT NULL COMMENT '创建人',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `updater`     varchar(128) NOT NULL DEFAULT "" COMMENT '更新人',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY           `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_category`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(128) NOT NULL COMMENT '类目名称',
    `deleted`     int(1) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_category_bot_rel`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `cat_id`      bigint(20) NOT NULL COMMENT '分类id',
    `bot_id`      bigint(20) NOT NULL COMMENT 'bot id',
    `deleted`     int(1) NOT NULL COMMENT '是否删除0-否 1-是',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
