CREATE TABLE `m78_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `utime` bigint(20) NOT NULL,
  `ctime` bigint(20) NOT NULL,
  `code` json DEFAULT NULL,
  `creator` varchar(128) NOT NULL,
  `type` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `desc` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;