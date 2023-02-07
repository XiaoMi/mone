CREATE TABLE `task_execute_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` int(10) NOT NULL DEFAULT '0' COMMENT '任务id',
  `content` varchar(1000) NOT NULL DEFAULT '' COMMENT '任务的参数和结果',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator_name` varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务执行历史记录';