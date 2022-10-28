CREATE TABLE `opt_record` (
  `id` bigint(11)  AUTO_INCREMENT,
  `client_ip` varchar(24) DEFAULT NULL COMMENT '来源ip',
  `opt_id` varchar(24) NOT NULL COMMENT '操作人',
  `resource_url` varchar(255) NOT NULL COMMENT '资源url',
  `resource_desc` varchar(255) COMMENT '资源描述',
  `opt_time` datetime DEFAULT NULL COMMENT '操作时间',
  `req_method` varchar(12) DEFAULT NULL COMMENT '请求方式',
  `in_param` text DEFAULT NULL COMMENT '调用入参',
  `return_code` varchar(12) DEFAULT NULL COMMENT '返回码',
  `out_param` text DEFAULT NULL COMMENT '调用出参',
  `duration` int(11) COMMENT '调用耗时',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;