CREATE TABLE `m78_openapi_async_task`
(
    `id`                BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type`              tinyint(3) NOT NULL COMMENT '类型, 1bot, 2flow',
    `relate_id`         BIGINT(20) DEFAULT NULL,
    `inputs`            text DEFAULT NULL comment '调用结果',
    `outputs`           text DEFAULT NULL,
    `task_id`           varchar(64) DEFAULT NULL,
    `task_status`       tinyint(3) NOT NULL COMMENT '任务状态，1运行中，2成功，3失败',
    `callback_url`      varchar(255) DEFAULT NULL comment '回调url',
    `callback_status`   tinyint(3) NOT NULL COMMENT '回调状态，1未回调，2已回调',
    `invoke_start_time` BIGINT(20) DEFAULT NULL,
    `invoke_end_time`   BIGINT(20) DEFAULT NULL,
    `invoke_user_name`  varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                 `idx_task_id` (`task_id`),
    KEY                 `idx_invoke_start_time` (`invoke_start_time`),
    KEY                 `idx_relate_id` (`relate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;