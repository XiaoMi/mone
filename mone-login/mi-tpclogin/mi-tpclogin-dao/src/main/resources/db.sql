-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile

CREATE TABLE `account_entity` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '唯一记录',
  `type` int(1) DEFAULT '0' COMMENT '类型',
  `status` int(1) DEFAULT '0' COMMENT '状态',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  `content` varchar(64) DEFAULT NULL COMMENT '内容',
  `creater_id` int(20) DEFAULT '0' COMMENT '创建人ID',
  `creater_acc` varchar(64) DEFAULT NULL COMMENT '创建人账号',
  `creater_type` int(1) DEFAULT '0' COMMENT '创建人类型',
  `updater_id` int(20) DEFAULT '0' COMMENT '更新人ID',
  `updater_acc` varchar(64) DEFAULT NULL COMMENT '更新人账号',
  `updater_type` int(1) DEFAULT '0' COMMENT '更新人类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(1) DEFAULT '0' COMMENT '0正常,1删除',
  `account` varchar(64) DEFAULT '' COMMENT '账号',
  `pwd` varchar(64) DEFAULT '' COMMENT '密码',
  `name` varchar(64) DEFAULT '' COMMENT '姓名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;