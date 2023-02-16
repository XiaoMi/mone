CREATE TABLE `scrape_config`
(
    `id`           int(4) unsigned NOT NULL AUTO_INCREMENT,
    `prom_cluster` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'public' COMMENT 'prometheus 集群名称',
    `region`       varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '区域',
    `zone`         varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '可用区',
    `env`          varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '配置环境：staging,preview,production',
    `status`       varchar(32) COLLATE utf8_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT '任务当前状态（是否下发成功）：pending、success',
    `instances`    varchar(255) COLLATE utf8_unicode_ci          DEFAULT '' COMMENT '采集任务的实例：逗号分隔多个',
    `job_name`     varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '采集任务的名称',
    `body`         text COLLATE utf8_unicode_ci         NOT NULL COMMENT 'scrape_config 结构体 json 字符串',
    `created_by`   varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
    `created_time` timestamp                            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` timestamp                            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted_by`   varchar(100) COLLATE utf8_unicode_ci          DEFAULT NULL COMMENT '删除人',
    `deleted_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY            `idx_prom_cluster` (`prom_cluster`),
    KEY            `idx_region` (`region`),
    KEY            `idx_zone` (`zone`),
    KEY            `idx_job_name` (`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `alert`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT COMMENT 'alert id',
    `name`            varchar(255) COLLATE utf8_unicode_ci  NOT NULL COMMENT 'alert name',
    `cname`           varchar(255) COLLATE utf8_unicode_ci           DEFAULT NULL COMMENT 'alert cname',
    `expr`            varchar(4096) COLLATE utf8_unicode_ci NOT NULL COMMENT 'expr',
    `labels`          varchar(4096) COLLATE utf8_unicode_ci NOT NULL COMMENT 'labels',
    `alert_for`       varchar(20) COLLATE utf8_unicode_ci   NOT NULL COMMENT 'for',
    `env`             varchar(100) COLLATE utf8_unicode_ci           DEFAULT NULL COMMENT 'config environment',
    `enabled`         tinyint(1) NOT NULL DEFAULT '0' COMMENT 'enabled',
    `priority`        tinyint(4) NOT NULL COMMENT 'priority',
    `created_by`      varchar(255) COLLATE utf8_unicode_ci  NOT NULL COMMENT 'creator',
    `created_time`    timestamp                             NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_time`    timestamp                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    `deleted_by`      varchar(255) COLLATE utf8_unicode_ci  NOT NULL COMMENT 'delete user',
    `deleted_time`    timestamp NULL DEFAULT NULL COMMENT 'deleted time',
    `prom_cluster`    varchar(100) COLLATE utf8_unicode_ci           DEFAULT 'public' COMMENT 'prometheus cluster name',
    `status`          varchar(32) COLLATE utf8_unicode_ci   NOT NULL DEFAULT 'pending' COMMENT '配置是否下发成功：pending、success',
    `instances`       varchar(255) COLLATE utf8_unicode_ci           DEFAULT '' COMMENT '配置生效的实例，逗号分隔多个',
    `thresholds_op`   varchar(8) COLLATE utf8_unicode_ci             DEFAULT NULL COMMENT '多阈值操作符，支持 or （或）或 and （且）',
    `thresholds`      mediumtext COLLATE utf8_unicode_ci COMMENT '告警阈值数组（简易模式使用该字段）',
    `type`            int(11) DEFAULT NULL COMMENT '模式，简易模式为 0，复杂模式为 1',
    `alert_member`    varchar(1024) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT 'alert_member',
    `alert_at_people` varchar(1024) COLLATE utf8_unicode_ci NOT NULL COMMENT 'alert at people',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `silence_matcher`
(
    `silence_id` int(11) NOT NULL COMMENT 'silence id',
    `name`       varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'name',
    `value`      varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'value',
    `is_regex`   tinyint(1) NOT NULL COMMENT 'if is regex matcher',
    `is_equal`   tinyint(1) NOT NULL COMMENT ' if is equal matcher'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


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
    `status`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci  NOT NULL DEFAULT 'pending' COMMENT '配置是否下发成功：pending、success',
    `alert_id`     int                                                           NOT NULL DEFAULT '0' COMMENT 'alert id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;