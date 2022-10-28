CREATE TABLE `gateway_web`.`review`  (
  `id` int(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `project_id` bigint(20) NOT NULL COMMENT '项目id',
  `project_name` varchar(255)  NOT NULL COMMENT '项目名字',
  `commit_id` varchar(255) NOT NULL COMMENT 'commit id',
  `url` varchar(255) NOT NULL COMMENT 'url',
  `submitter` varchar(255)  NOT NULL COMMENT '申请人',
  `reviewer` varchar(255)  NOT NULL COMMENT '审核人员',
  `operator` varchar(255)  NULL DEFAULT NULL COMMENT '实际审核人员',
  `operate_time` bigint(20) NULL DEFAULT NULL COMMENT '审核时间',
  `status` tinyint(1) NOT NULL,
  `remarks` varchar(255)  NULL DEFAULT NULL,
  `ctime` bigint(20) NOT NULL,
  `utime` bigint(20) NOT NULL,
  `version` int(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;