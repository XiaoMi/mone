CREATE TABLE `m78_bot_db_table` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `workspace_id` bigint(20) NOT NULL,
                                    `table_name` varchar(128) NOT NULL,
                                    `creator` varchar(128) NOT NULL,
                                    `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                    `bot_id` bigint(20) DEFAULT NULL,
                                    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin


CREATE TABLE `m78_bot_db_table_rel` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `bot_id` bigint(20) NOT NULL,
                                        `db_table_id` bigint(20) NOT NULL,
                                        `creator` varchar(128) NOT NULL,
                                        `create_time` datetime DEFAULT NULL,
                                        `deleted` int(1) DEFAULT '0',
                                        PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                        KEY `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4