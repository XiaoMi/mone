CREATE TABLE `m78_bot_flow_rel` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `bot_id` bigint(20) NOT NULL,
                                    `flow_base_id` bigint(20) NOT NULL,
                                    `creator` varchar(128) NOT NULL,
                                    `create_time` datetime DEFAULT NULL,
                                    `deleted` int(1) NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `m78_bot_plugin_rel` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `bot_id` bigint(20) NOT NULL,
                                      `plugin_id` bigint(20) NOT NULL,
                                      `deleted` int(1) NOT NULL DEFAULT '0',
                                      `creator` varchar(128) NOT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
                                      KEY `idx_bot_id` (`bot_id`),
                                      KEY `idx_plugin_id` (`plugin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

CREATE TABLE `m78_bot_knowledge_rel` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `bot_id` bigint(20) NOT NULL,
                                         `knowledge_id_list` json DEFAULT NULL,
                                         `creator` varchar(128) DEFAULT NULL,
                                         `create_time` datetime DEFAULT NULL,
                                         PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin