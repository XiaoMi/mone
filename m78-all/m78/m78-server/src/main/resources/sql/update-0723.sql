CREATE TABLE `m78_multi_modal_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `work_space_id` BIGINT(20) DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  `type` tinyint(3) NOT NULL COMMENT '多模态类型',
  `ai_model` varchar(255) NOT NULL,
  `deleted`  int(1)   default 0  not null comment '是否删除 0-否 1-是',
  `run_status` int(1) NOT NULL DEFAULT '0',
  `rst_message` varchar(255) DEFAULT NULL comment '调用结果',
  `user_name` varchar(255) DEFAULT NULL,
  `ctime`  BIGINT(20) DEFAULT NULL,
  `utime`  BIGINT(20) DEFAULT NULL,
  `setting` JSON,
  `multi_modal_resource_output` JSON comment '多模态资源输出，图片、音频等',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;