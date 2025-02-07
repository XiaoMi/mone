CREATE TABLE `m78_invoke_history_detail`
(
    `id`               BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type`             tinyint(3) NOT NULL COMMENT '类型, 1bot, 2flow, 3plugin',
    `relate_id`        BIGINT(20) DEFAULT NULL,
    `inputs`  text DEFAULT NULL,
    `outputs` text DEFAULT NULL,
    `invoke_time`      BIGINT(20) DEFAULT NULL,
    `invoke_way`       tinyint(3) NOT NULL COMMENT '调用方式, 1页面, 2接口, 3系统内部, 4调试等等',
    `invoke_user_name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                `idx_invoke_user_name` (`invoke_user_name`),
    KEY                `idx_invoke_time` (`invoke_time`),
    KEY                `idx_relate_id` (`relate_id`),
    KEY                `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_invoke_summary_perday`
(
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type`          tinyint(3) NOT NULL COMMENT '类型, 1bot, 2flow, 3plugin',
    `relate_id`     BIGINT(20) DEFAULT NULL,
    `invoke_counts` BIGINT(20) DEFAULT NULL,
    `invoke_users`  BIGINT(20) DEFAULT NULL,
    `invoke_day`    BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY             `idx_invoke_day` (`invoke_day`),
    KEY             `idx_relate_id` (`relate_id`),
    KEY             `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;