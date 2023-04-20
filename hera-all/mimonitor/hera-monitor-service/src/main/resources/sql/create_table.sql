CREATE TABLE `app_grafana_mapping` (
                                       `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                       `app_name` varchar(100) NOT NULL,
                                       `mione_env` varchar(20) DEFAULT NULL,
                                       `grafana_url` varchar(200) NOT NULL,
                                       `create_time` timestamp NULL DEFAULT NULL,
                                       `update_time` timestamp NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `appNameIndex` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;