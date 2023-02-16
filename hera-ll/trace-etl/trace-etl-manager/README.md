# 概述
# 依赖

（1）mysql，建表语句如下：
```
CREATE TABLE `hera_trace_etl_config` (
`id` int NOT NULL AUTO_INCREMENT,
`base_info_id` int DEFAULT NULL COMMENT 'hera_base_info表的id',
`exclude_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用操作过滤',
`exclude_httpserver_method` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'httpServer端过滤的应用操作',
`exclude_thread` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '线程名称过滤',
`exclude_sql` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'sql过滤',
`exclude_http_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'url过滤',
`exclude_ua` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'user-agent过滤',
`http_slow_threshold` int DEFAULT NULL COMMENT 'http慢查询阈值',
`dubbo_slow_threshold` int DEFAULT NULL COMMENT 'dubbo慢查询阈值',
`mysql_slow_threshold` int DEFAULT NULL COMMENT 'mysql慢查询阈值',
`trace_filter` int DEFAULT NULL COMMENT 'trace需要存入es的百分比',
`trace_duration_threshold` int DEFAULT NULL COMMENT 'trace存入es的耗时阈值',
`trace_debug_flag` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'trace存入es的debug标识，对应heraContext的key',
`http_status_error` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些http状态码不显示在异常列表',
`exception_error` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些exception不算异常请求',
`grpc_code_error` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '哪些grpc_code不算异常请求',
`status` varchar(2) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '是否有效 0无效  1有效',
`create_time` datetime DEFAULT NULL,
`update_time` datetime DEFAULT NULL,
`create_user` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
`update_user` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
```

（2）RocketMQ

（3）Nacos

# 如何部署

## 使用maven构建
在项目根目录下（trace-etl）执行：

`mvn clean install -U -P opensource -DskipTests`

会在trace-etl-manager模块下生成target目录，target目录中的trace-etl-manager-1.0.0-SNAPSHOT.jar就是运行的jar文件。
## 运行
执行：

`java -jar trace-etl-manager-1.0.0-SNAPSHOT.jar`

就可以运行trace-etl-manager。