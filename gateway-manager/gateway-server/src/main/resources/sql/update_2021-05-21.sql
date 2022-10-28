CREATE TABLE `metadata` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '元数据名称',
  `description` varchar(1023) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '元数据描述',
  `type` int(11) NOT NULL  COMMENT '元数据类型 1-分组集合group_cluster,2-域名domain',
  `ctime` bigint(20) NOT NULL COMMENT '创建时间（毫秒）',
  `utime` bigint(20) NOT NULL COMMENT '更新时间（毫秒）',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `metadata_relation` (
  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT,
  `source` bigint(21) NOT NULL COMMENT 'source 表主键id,只能是metadata表的主键',
  `target` bigint(21) NOT NULL COMMENT 'target 表主键id，可以是metadata表的主键或者其他表的主键',
        `type` int(11) NOT NULL COMMENT '元数据类型 1-group_cluster(metadata):api_gourp;2-group_cluster(metadata):domain(metadata);',
  PRIMARY KEY (`id`),
  KEY `idx_key` (`source`,`target`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
