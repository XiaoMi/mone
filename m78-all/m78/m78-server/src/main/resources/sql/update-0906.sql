CREATE TABLE `m78_card_variable`
(
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
    `card_id`       BIGINT(20) DEFAULT NULL,
    `name`          varchar(64) DEFAULT NULL comment '参数名',
    `class_type`    varchar(64) DEFAULT NULL comment '参数类型',
    `default_value` varchar(64) DEFAULT NULL comment '默认值',
    `creator`       varchar(64) DEFAULT NULL comment '创建者',
    `updater`       varchar(64) DEFAULT NULL comment '更新者',
    `ctime`         BIGINT(20) DEFAULT NULL,
    `utime`         BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY             `idx_card_id` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_card`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `element_id`   BIGINT(20) DEFAULT NULL,
    `workspace_id` BIGINT(20) DEFAULT NULL,
    `name`         varchar(64) DEFAULT NULL comment '卡片名称',
    `type`         varchar(64) DEFAULT NULL comment '类型',
    `status`       tinyint(3) DEFAULT NULL comment '状态',
    `official`     tinyint(3) DEFAULT NULL comment '官方',
    `description`  varchar(64) DEFAULT NULL comment '描述',
    `creator`      varchar(64) DEFAULT NULL comment '创建者',
    `updater`      varchar(64) DEFAULT NULL comment '更新者',
    `ctime`        BIGINT(20) DEFAULT NULL,
    `utime`        BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY            `idx_element_id` (`element_id`),
    KEY            `idx_ctime` (`ctime`),
    KEY            `idx_creator` (`creator`),
    KEY            `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `m78_card_element`
(
    `id`                 BIGINT(20) NOT NULL AUTO_INCREMENT,
    `card_id`            BIGINT(20) DEFAULT NULL,
    `workspace_id`       BIGINT(20) DEFAULT NULL,
    `unique_key`         varchar(64) DEFAULT NULL comment '唯一key',
    `type`               varchar(64) DEFAULT NULL comment '类型',
    `property`           text        DEFAULT NULL comment '配置',
    `children`           text        DEFAULT NULL comment '子element',
    PRIMARY KEY (`id`),
    KEY                  `idx_workspace_id` (`workspace_id`),
    KEY                  `idx_card_id` (`card_id`),
    KEY                  `idx_uniqueKey` (`unique_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;