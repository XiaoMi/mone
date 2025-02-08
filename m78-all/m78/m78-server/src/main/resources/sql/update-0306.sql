CREATE TABLE `m78_user_workspace_role`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `username`     varchar(128) NOT NULL,
    `workspace_id` bigint(20) NOT NULL,
    `role`         int(1) NOT NULL DEFAULT '0',
    `deleted`      int(1) DEFAULT '0',
    `create_time`  datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY            `idx_username` (`username`),
    KEY            `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;