CREATE TABLE `z_desensitization_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
  `text_before` text NOT NULL COMMENT '原文本',
  `text_after` text COMMENT '脱敏后文本',
  `status` int(1) not null comment '状态, 0: 失败, 1：成功',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='文本脱敏记录';