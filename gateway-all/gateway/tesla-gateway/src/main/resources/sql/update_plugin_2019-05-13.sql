USE `gateway_web`;

CREATE TABLE `plugin_info` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `data_id` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `creator` varchar(128) DEFAULT NULL,
  `desc` varchar(128) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;



CREATE TABLE `plugin_data` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `plugin_id` int(32) DEFAULT NULL,
  `data` mediumblob,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` varchar(128) DEFAULT NULL,
  `stauts` int(32) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;


CREATE TABLE `tesla_ds` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `type` int(32) DEFAULT NULL,
  `driver_class` varchar(128) DEFAULT NULL,
  `data_source_url` varchar(128) DEFAULT NULL,
  `user_name` varchar(128) DEFAULT NULL,
  `pass_wd` varchar(128) DEFAULT NULL,
  `pool_size` int(32) DEFAULT NULL,
  `max_pool_size` int(32) DEFAULT NULL,
  `min_pool_size` int(32) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `state` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `creator` varchar(128) DEFAULT NULL,
  `jar_path` varchar(128) DEFAULT NULL,
  `ioc_package` varchar(128) DEFAULT NULL,
  `app_name` varchar(128) DEFAULT NULL,
  `reg_address` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `gateway_server_info` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `server_name` varchar(128) DEFAULT NULL,
  `host` varchar(128) DEFAULT NULL,
  `port` int(32) DEFAULT NULL,
  `group` varchar(128) DEFAULT NULL,
  `key` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

alter table api_info add plugin_name varchar(100);
alter table api_info add ds_ids varchar(200);
alter table api_info add ip_anti_brush_limit int(11) not null default 0 comment 'ip防刷限制';
alter table api_info add uid_anti_brush_limit int(11) not null default 0 comment 'uid防刷限制';
alter table api_info add cache_expire int(11) NOT NULL DEFAULT '0' COMMENT '缓存过期时间(毫秒)' after timeout;