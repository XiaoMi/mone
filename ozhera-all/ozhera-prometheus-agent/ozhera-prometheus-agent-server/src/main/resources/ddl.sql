CREATE TABLE `scrape_config`
(
    `id`           int unsigned NOT NULL AUTO_INCREMENT,
    `prom_cluster` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'public' COMMENT 'prometheus cluster name',
    `region`       varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'region',
    `zone`         varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'availability zone',
    `env`          varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'config env：staging,preview,production',
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT 'current status of the task (whether the delivery was successful)：pending、success',
    `instances`    varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT '' COMMENT 'instances of collection tasks: comma separated',
    `job_name`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'name of the collection task',
    `body`         text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'scrape_config struct json string',
    `created_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'creator',
    `created_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted_by`   varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT NULL COMMENT 'deleter',
    `deleted_time` timestamp NULL DEFAULT NULL COMMENT 'deleted time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unq_job_name` (`job_name`,`deleted_by`) USING BTREE,
    KEY            `idx_prom_cluster` (`prom_cluster`),
    KEY            `idx_region` (`region`),
    KEY            `idx_zone` (`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

CREATE TABLE `prometheus_alert`
(
    `id`              int                                                            NOT NULL AUTO_INCREMENT COMMENT 'alert id',
    `name`            varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'alert name',
    `cname`           varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT NULL COMMENT 'alert cname',
    `expr`            varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'expr',
    `labels`          varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'labels',
    `alert_for`       varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci   NOT NULL COMMENT 'for',
    `env`             varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT NULL COMMENT 'config environment',
    `enabled`         tinyint(1) NOT NULL DEFAULT '0' COMMENT 'enabled',
    `priority`        tinyint                                                        NOT NULL COMMENT 'priority',
    `created_by`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'creator',
    `created_time`    timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time`    timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted_by`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL COMMENT 'delete user',
    `deleted_time`    timestamp NULL DEFAULT NULL COMMENT 'deleted time',
    `prom_cluster`    varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT 'public' COMMENT 'prometheus cluster name',
    `status`          varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci   NOT NULL DEFAULT 'pending' COMMENT 'Whether the configuration is successfully delivered：pending、success',
    `instances`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT 'instances of collection tasks: comma separated',
    `thresholds_op`   varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci             DEFAULT NULL COMMENT 'Multi-threshold operator, supports [or] , [and]',
    `thresholds`      mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT 'Alarm threshold array (use this field in simple mode)',
    `type`            int                                                                     DEFAULT NULL COMMENT 'Mode, simple mode is 0, complex mode is 1',
    `alert_member`    varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'alert_member',
    `alert_at_people` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'alert at people',
    `annotations`     varchar(4096) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '' COMMENT 'Comment',
    `alert_group`     varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci           DEFAULT '' COMMENT 'group',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


CREATE TABLE `silence_matcher`
(
    `silence_id` int                                                           NOT NULL COMMENT 'silence id',
    `name`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'name',
    `value`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'value',
    `is_regex`   tinyint(1) NOT NULL COMMENT 'if is regex matcher',
    `is_equal`   tinyint(1) NOT NULL COMMENT ' if is equal matcher'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;


CREATE TABLE `silence`
(
    `id`           int                                                           NOT NULL AUTO_INCREMENT COMMENT 'silence id',
    `uuid`         varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'silence uuid',
    `comment`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'creator',
    `start_time`   timestamp                                                     NOT NULL COMMENT 'silence start time',
    `end_time`     timestamp                                                     NOT NULL COMMENT 'silence end time',
    `created_by`   varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT 'creator',
    `created_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'updated time',
    `prom_cluster` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci          DEFAULT 'public' COMMENT 'prometheus cluster name',
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT 'Whether the configuration is successfully delivered：pending、success',
    `alert_id`     int                                                           NOT NULL DEFAULT '0' COMMENT 'alert id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;