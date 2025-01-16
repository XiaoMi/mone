CREATE TABLE `m78_card_bind`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
    `bot_id`      BIGINT(20) DEFAULT NULL,
    `card_id`     BIGINT(20) DEFAULT NULL,
    `relate_id`   BIGINT(20) DEFAULT NULL,
    `type`        varchar(64) DEFAULT NULL comment '类型',
    `bind_detail` text        DEFAULT NULL comment '绑定明细',
    `ctime`       BIGINT(20) DEFAULT NULL,
    `utime`       BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `idx_bot_id` (`bot_id`),
    KEY           `idx_card_id` (`card_id`),
    KEY           `idx_relate_id` (`relate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;