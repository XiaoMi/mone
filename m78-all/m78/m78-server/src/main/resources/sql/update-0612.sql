CREATE TABLE `m78_code_generation_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(20) NOT NULL COMMENT '创建时间',
  `utime` bigint(20) NOT NULL COMMENT '更新时间',
  `state` int(11) DEFAULT '0' COMMENT '状态',
  `project_name` varchar(255) NOT NULL COMMENT '服务名',
  `class_name` varchar(255) NOT NULL COMMENT '类名',
  `code_lines_count` int(11) NOT NULL COMMENT '代码行数',
  `method_name` varchar(255) NOT NULL COMMENT '方法名',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1;