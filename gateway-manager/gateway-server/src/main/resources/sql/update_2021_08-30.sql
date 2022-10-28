-- 操作日志表，没有的话才执行创建表sql
--CREATE TABLE `operation_log` (
--  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
--  `app_name` varchar(100) NOT NULL DEFAULT '' COMMENT '应用名称',
--  `user_name` varchar(100) NOT NULL DEFAULT '' COMMENT '操作人/用户名',
--  `data_id` varchar(100) NOT NULL DEFAULT '' COMMENT '资源标识',
--  `data_before` varchar(1000) DEFAULT '' COMMENT '操作前数据',
--  `data_after` text DEFAULT NULL COMMENT '操作后数据',
--  `create_time` bigint(64) DEFAULT '0' COMMENT '操作时间',
--  `type` int(11) DEFAULT '0' COMMENT '类型,1:创建, 2:删除, 3:更改',
--  `remark` varchar(1000) DEFAULT '' COMMENT '备注',
--  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 注意，如果data_after不是text类型，记得给修改成text类型
alter table operation_log MODIFY COLUMN data_after text DEFAULT null COMMENT '操作后数据';