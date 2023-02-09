SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_env
-- ----------------------------
DROP TABLE IF EXISTS `api_env`;
CREATE TABLE `api_env` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '环境id',
  `env_name` varchar(255) NOT NULL COMMENT '环境名',
  `http_domain` varchar(255) DEFAULT NULL COMMENT 'http域名',
  `env_desc` varchar(255) DEFAULT NULL COMMENT '环境备注',
  `project_id` int(10) DEFAULT NULL COMMENT '所属项目id',
  `headers` text COMMENT '全局请求头',
  `req_param_form_data` text COMMENT '表单类body请求参数',
  `req_param_raw` text COMMENT 'raw格式参数',
  `sys_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for api_history_record
-- ----------------------------
DROP TABLE IF EXISTS `api_history_record`;
CREATE TABLE `api_history_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` int(10) NOT NULL DEFAULT '0' COMMENT '所属项目ID',
  `group_id` int(10) NOT NULL DEFAULT '0' COMMENT '所属分类ID',
  `api_id` int(10) NOT NULL DEFAULT '0' COMMENT '关联api_id',
  `api_protocal` int(10) NOT NULL DEFAULT '0' COMMENT 'api协议类型',
  `api_histiry_json` longtext COMMENT 'api具体数据',
  `update_user` varchar(255) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_msg` varchar(256) NOT NULL DEFAULT '' COMMENT '变更描述',
  `is_now` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是当前api',
  PRIMARY KEY (`id`),
  KEY `idx_api_id` (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2612 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for api_index
-- ----------------------------
DROP TABLE IF EXISTS `api_index`;
CREATE TABLE `api_index` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `index_id` int(10) NOT NULL DEFAULT '0' COMMENT '索引组id',
  `api_id` int(10) NOT NULL DEFAULT '0' COMMENT 'api id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_api_index` (`index_id`,`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COMMENT='索引组映射表';

-- ----------------------------
-- Table structure for api_mock_exp
-- ----------------------------
DROP TABLE IF EXISTS `api_mock_exp`;
CREATE TABLE `api_mock_exp` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `mock_exp_name` varchar(255) DEFAULT NULL,
  `mock_params` text,
  `mock_data` text,
  `mock_rule` text,
  `mock_data_type` int(4) DEFAULT NULL COMMENT 'mock数据输入方式',
  `params_md5` varchar(255) DEFAULT NULL,
  `api_id` int(10) NOT NULL,
  `is_default` tinyint(1) DEFAULT NULL COMMENT '是否是系统默认期望',
  `update_user` varchar(255) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL,
  `mock_request_raw` text COMMENT 'raw类型参数',
  `mock_request_param_type` int(10) NOT NULL DEFAULT '0' COMMENT '传递参数类型',
  `proxy_url` varchar(255) NOT NULL,
  `use_mock_script` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启mock脚本模式',
  `mock_script` text COMMENT 'mock脚本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2876 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for api_request_exp
-- ----------------------------
DROP TABLE IF EXISTS `api_request_exp`;
CREATE TABLE `api_request_exp` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` int(10) NOT NULL DEFAULT '0' COMMENT '对应的api的id',
  `request_param_exp_type` int(10) NOT NULL DEFAULT '0' COMMENT '请求参数示例的类型：1:java;2:curl;',
  `code_gen_exp` text COMMENT '生成的示例代码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_api_req_type` (`api_id`,`request_param_exp_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4263 DEFAULT CHARSET=utf8 COMMENT='请求示例';

-- ----------------------------
-- Table structure for api_response_exp
-- ----------------------------
DROP TABLE IF EXISTS `api_response_exp`;
CREATE TABLE `api_response_exp` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `api_id` int(10) NOT NULL DEFAULT '0' COMMENT '对应的api的id',
  `resp_gen_exp_type` int(10) NOT NULL DEFAULT '0' COMMENT '返回示例的类型 1:json',
  `resp_gen_exp` text COMMENT '生成的返回示例',
  PRIMARY KEY (`id`),
  KEY `uk_api_resp_type` (`api_id`,`resp_gen_exp_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2168 DEFAULT CHARSET=utf8 COMMENT='返回示例';

-- ----------------------------
-- Table structure for api_test_case
-- ----------------------------
DROP TABLE IF EXISTS `api_test_case`;
CREATE TABLE `api_test_case` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'case id',
  `account_id` int(10) NOT NULL DEFAULT '0' COMMENT '测试用户id',
  `api_id` int(10) NOT NULL COMMENT '对应api的id',
  `api_protocal` int(10) NOT NULL COMMENT 'api的类型',
  `http_method` varchar(255) NOT NULL DEFAULT '' COMMENT 'http请求的方法',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT 'http请求的url',
  `request_timeout` int(10) NOT NULL DEFAULT '0' COMMENT '请求超时时间',
  `http_headers` text NOT NULL COMMENT 'http请求头json',
  `case_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'case名',
  `http_domian` varchar(255) NOT NULL DEFAULT '' COMMENT 'http请求使用的环境id',
  `env_id` int(11) NOT NULL,
  `http_req_body_type` int(11) DEFAULT NULL COMMENT '请求体类型',
  `http_request_body` text COMMENT 'http请求参数内容',
  `dubbo_interface` varchar(255) NOT NULL DEFAULT '' COMMENT 'dubbo接口名',
  `dubbo_method_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'dubbo方法名',
  `dubbo_group` varchar(255) NOT NULL DEFAULT '' COMMENT '分组',
  `dubbo_version` varchar(255) NOT NULL DEFAULT '' COMMENT '版本',
  `dubbo_addr` varchar(255) NOT NULL DEFAULT '' COMMENT '指定ip,port',
  `dubbo_param_type` varchar(255) NOT NULL DEFAULT '' COMMENT 'dubbo参数类型',
  `dubbo_is_generic` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是泛型参数',
  `dubbo_retry_time` int(11) NOT NULL COMMENT 'dubbo接口重试次数',
  `dubbo_use_attachment` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否携带attachment',
  `dubbo_attachment` varchar(255) NOT NULL DEFAULT '' COMMENT 'dubbo接口的attachment',
  `dubbo_env` varchar(255) NOT NULL DEFAULT '' COMMENT 'dubbo请求的环境',
  `use_x5_filter` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用x5验权',
  `x5_app_key` varchar(255) NOT NULL DEFAULT '' COMMENT 'x5的appkey',
  `x5_app_id` varchar(255) NOT NULL DEFAULT '0' COMMENT 'x5的appid',
  `dubbo_param_body` text COMMENT 'dubbo接口参数体',
  `case_group_id` int(11) DEFAULT NULL COMMENT '请求所属case分组',
  `grpc_param_body` text NOT NULL COMMENT 'grpc请求参数',
  `grpc_package_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'grpc接口包名',
  `grpc_interface_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'grpc接口服务名',
  `grpc_method_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'grpc接口方法名',
  `grpc_server_addr` varchar(255) NOT NULL DEFAULT '' COMMENT 'grpc服务地址',
  `grpc_app_name` varchar(255) NOT NULL DEFAULT '' COMMENT 'grpc应用名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for api_test_log
-- ----------------------------
DROP TABLE IF EXISTS `api_test_log`;
CREATE TABLE `api_test_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `op_username` varchar(255) NOT NULL,
  `interface_name` varchar(255) NOT NULL DEFAULT '',
  `method_name` varchar(255) NOT NULL DEFAULT '',
  `api_group` varchar(255) NOT NULL DEFAULT '',
  `version` varchar(255) NOT NULL DEFAULT '',
  `env` varchar(255) NOT NULL DEFAULT '',
  `ip` varchar(255) NOT NULL DEFAULT '',
  `url` varchar(255) NOT NULL DEFAULT '',
  `param` varchar(255) NOT NULL DEFAULT '',
  `op_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=699 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bus_project
-- ----------------------------
DROP TABLE IF EXISTS `bus_project`;
CREATE TABLE `bus_project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '项目名',
  `description` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '描述',
  `ctime` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `utime` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `status` int(10) NOT NULL DEFAULT '0' COMMENT '接口状态',
  `version` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '版本',
  `bus_group_id` int(10) NOT NULL DEFAULT '0' COMMENT '是否公开',
  `is_public` tinyint(1) DEFAULT '1' COMMENT '是否是公开项目 1 是，0 不是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=120634 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='业务项目表';

-- ----------------------------
-- Table structure for bus_project_group
-- ----------------------------
DROP TABLE IF EXISTS `bus_project_group`;
CREATE TABLE `bus_project_group` (
  `group_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_name` varchar(255) NOT NULL DEFAULT '' COMMENT '组名',
  `group_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `status` tinyint(1) NOT NULL,
  `pub_group` int(10) NOT NULL DEFAULT '1' COMMENT '是否为公开项目组',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='业务项目组';

-- ----------------------------
-- Table structure for dubbo_push_data
-- ----------------------------
DROP TABLE IF EXISTS `dubbo_push_data`;
CREATE TABLE `dubbo_push_data` (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '上报数据的服务实例ip port ',
  `apiModuleList` json DEFAULT NULL COMMENT '模块层数据',
  `apiModuleInfo` json DEFAULT NULL COMMENT '方法集',
  `apiParamsResponseInfo` json DEFAULT NULL COMMENT '接口具体数据',
  PRIMARY KEY (`id`),
  UNIQUE KEY `address_idx` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Dubbo服务推送的数据';

-- ----------------------------
-- Table structure for eo_api
-- ----------------------------
DROP TABLE IF EXISTS `eo_api`;
CREATE TABLE `eo_api` (
  `apiID` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `apiName` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'api名',
  `apiURI` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'api的url',
  `apiProtocol` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT 'api协议',
  `apiRequestType` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '请求类型',
  `apiStatus` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT 'api状态 0弃用 1未完成 2 已完成 ',
  `apiUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'api更新时间',
  `groupID` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '组id',
  `projectID` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '项目id',
  `starred` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '星标状态',
  `removed` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '软删除',
  `removeTime` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `apiNoteType` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '文档类型',
  `apiRemark` text COLLATE utf8_bin COMMENT '文档内容',
  `apiDesc` text COLLATE utf8_bin COMMENT '内容',
  `apiRequestParamType` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '请求参数类型',
  `apiResponseParamType` tinyint(4) NOT NULL COMMENT '返回参数类型',
  `apiRequestRaw` text COLLATE utf8_bin COMMENT 'content',
  `apiResponseRaw` text COLLATE utf8_bin COMMENT '返回参数行',
  `updateUsername` varchar(66) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `dubboApiId` int(10) NOT NULL DEFAULT '0' COMMENT 'dubbo接口id',
  `gatewayApiId` int(10) NOT NULL DEFAULT '0' COMMENT '网关接口id',
  `apiEnv` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '接口环境',
  `httpControllerPath` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'http controller地址',
  `mavenAddr` text COLLATE utf8_bin COMMENT 'dubbo接口maven依赖地址',
  PRIMARY KEY (`apiID`) USING BTREE,
  UNIQUE KEY `uk_api` (`apiURI`,`apiRequestType`,`projectID`) USING BTREE,
  KEY `apiID` (`apiID`),
  KEY `projectID` (`projectID`)
) ENGINE=InnoDB AUTO_INCREMENT=3147 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='api总表';

-- ----------------------------
-- Table structure for eo_api_cache
-- ----------------------------
DROP TABLE IF EXISTS `eo_api_cache`;
CREATE TABLE `eo_api_cache` (
  `cacheID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `projectID` int(10) unsigned NOT NULL,
  `groupID` int(10) unsigned NOT NULL,
  `apiID` int(10) unsigned NOT NULL,
  `apiJson` longtext NOT NULL,
  `starred` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `updateUsername` varchar(66) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cacheID`)
) ENGINE=InnoDB AUTO_INCREMENT=2830 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for eo_api_group
-- ----------------------------
DROP TABLE IF EXISTS `eo_api_group`;
CREATE TABLE `eo_api_group` (
  `groupID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `groupName` varchar(255) COLLATE utf8_bin NOT NULL,
  `projectID` int(11) unsigned NOT NULL,
  `groupDesc` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `systemGroup` tinyint(1) NOT NULL DEFAULT '0',
  `isChild` tinyint(1) NOT NULL DEFAULT '0',
  `parentGroupID` int(11) DEFAULT NULL,
  PRIMARY KEY (`groupID`,`projectID`),
  KEY `groupID` (`groupID`),
  KEY `projectID` (`projectID`)
) ENGINE=InnoDB AUTO_INCREMENT=662 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for eo_dubbo_api
-- ----------------------------
DROP TABLE IF EXISTS `eo_dubbo_api`;
CREATE TABLE `eo_dubbo_api` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `apiName` varchar(255) COLLATE utf8_bin NOT NULL,
  `apiDocName` varchar(255) COLLATE utf8_bin NOT NULL,
  `apiVersion` varchar(255) COLLATE utf8_bin NOT NULL,
  `apiGroup` varchar(255) COLLATE utf8_bin NOT NULL,
  `description` varchar(255) COLLATE utf8_bin NOT NULL,
  `apiRespDec` varchar(255) COLLATE utf8_bin NOT NULL,
  `apiModelClass` varchar(255) COLLATE utf8_bin NOT NULL,
  `methodParamInfo` longtext COLLATE utf8_bin,
  `request` text COLLATE utf8_bin COMMENT 'sa',
  `response` longtext COLLATE utf8_bin,
  `errorCodes` longtext COLLATE utf8_bin COMMENT '错误码',
  `async` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=225 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for eo_log_project_operation
-- ----------------------------
DROP TABLE IF EXISTS `eo_log_project_operation`;
CREATE TABLE `eo_log_project_operation` (
  `opID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `opType` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `opUsername` varchar(66) NOT NULL,
  `opDesc` text NOT NULL,
  `opTime` datetime NOT NULL,
  `opProjectID` int(10) unsigned NOT NULL,
  `opTarget` tinyint(4) unsigned NOT NULL,
  `opTargetID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`opID`,`opTargetID`,`opProjectID`,`opUsername`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4188 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for eo_project_document
-- ----------------------------
DROP TABLE IF EXISTS `eo_project_document`;
CREATE TABLE `eo_project_document` (
  `documentID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `projectID` int(10) unsigned NOT NULL,
  `contentType` tinyint(4) unsigned NOT NULL,
  `contentRaw` longtext,
  `content` longtext,
  `title` varchar(255) NOT NULL,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `username` varchar(255) NOT NULL,
  `createUserName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`documentID`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for eo_project_document_group
-- ----------------------------
DROP TABLE IF EXISTS `eo_project_document_group`;
CREATE TABLE `eo_project_document_group` (
  `groupID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `projectID` int(10) unsigned NOT NULL,
  `groupName` varchar(255) NOT NULL,
  PRIMARY KEY (`groupID`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for eo_project_document_group_order
-- ----------------------------
DROP TABLE IF EXISTS `eo_project_document_group_order`;
CREATE TABLE `eo_project_document_group_order` (
  `orderID` int(11) NOT NULL AUTO_INCREMENT,
  `projectID` int(11) NOT NULL,
  `orderList` text NOT NULL,
  PRIMARY KEY (`orderID`,`projectID`),
  UNIQUE KEY `projectID` (`projectID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for eo_project_focus
-- ----------------------------
DROP TABLE IF EXISTS `eo_project_focus`;
CREATE TABLE `eo_project_focus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `busProjectId` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `项目和用户一个关联` (`busProjectId`,`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gateway_api_info
-- ----------------------------
DROP TABLE IF EXISTS `gateway_api_info`;
CREATE TABLE `gateway_api_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'api名称',
  `description` varchar(500) COLLATE utf8_bin DEFAULT '' COMMENT 'api描述',
  `url` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '请求路径',
  `http_method` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '提交方式',
  `path` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '后端请求路径',
  `route_type` int(4) DEFAULT NULL,
  `application` varchar(255) COLLATE utf8_bin DEFAULT 'tesla' COMMENT 'api所属appName',
  `service_name` varchar(100) COLLATE utf8_bin DEFAULT '' COMMENT '服务名称',
  `method_name` varchar(100) COLLATE utf8_bin DEFAULT '' COMMENT '方法名称',
  `service_group` varchar(100) COLLATE utf8_bin DEFAULT '' COMMENT 'RPC服务分组',
  `service_version` varchar(100) COLLATE utf8_bin DEFAULT '' COMMENT '服务的版本号',
  `param_template` text COLLATE utf8_bin COMMENT '参数模板，json模板',
  `status` int(100) NOT NULL,
  `invoke_limit` int(11) DEFAULT '600' COMMENT '每分钟调用次数限制',
  `qps_limit` int(11) DEFAULT '1000' COMMENT 'qps限制',
  `timeout` int(11) NOT NULL DEFAULT '1000' COMMENT '超时时间（毫秒）',
  `ctime` bigint(20) DEFAULT '0' COMMENT '创建时间（毫秒）',
  `utime` bigint(20) DEFAULT '0' COMMENT '修改时间（毫秒）',
  `allow_mock` tinyint(1) NOT NULL,
  `mock_data` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `mock_data_desc` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for http_push_data
-- ----------------------------
DROP TABLE IF EXISTS `http_push_data`;
CREATE TABLE `http_push_data` (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '接口服务地址',
  `httpApiModuleInfo` json DEFAULT NULL COMMENT '接口controller等信息',
  `httpApiModuleListAndApiInfo` json DEFAULT NULL COMMENT '方法等信息',
  `httpApiParamsResponseInfo` json DEFAULT NULL COMMENT '具体参数数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='http服务推送的数据\n';

-- ----------------------------
-- Table structure for index_info
-- ----------------------------
DROP TABLE IF EXISTS `index_info`;
CREATE TABLE `index_info` (
  `index_id` int(11) NOT NULL AUTO_INCREMENT,
  `index_name` varchar(255) DEFAULT NULL,
  `project_id` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `index_doc` longtext,
  PRIMARY KEY (`index_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for module_name_data
-- ----------------------------
DROP TABLE IF EXISTS `module_name_data`;
CREATE TABLE `module_name_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module_name` varchar(255) NOT NULL COMMENT '模块名，http为controller、dubbo为serviceName',
  `address` varchar(255) NOT NULL COMMENT '实例地址：ip:port',
  `last_beat_time` bigint(20) NOT NULL COMMENT '上次心跳时间',
  PRIMARY KEY (`id`),
  KEY `uk_module_addr` (`module_name`,`address`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sidecar_push_data
-- ----------------------------
DROP TABLE IF EXISTS `sidecar_push_data`;
CREATE TABLE `sidecar_push_data` (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT 'sidecar服务地址',
  `sidecarApiModuleInfo` json DEFAULT NULL COMMENT '接口模块信息',
  `sidecarApiModuleListAndApiInfo` json DEFAULT NULL COMMENT '方法等信息',
  `sidecarApiParamsResponseInfo` json DEFAULT NULL COMMENT '具体参数数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='sidecar服务推送的数据\n';

-- ----------------------------
-- Table structure for test_case_group
-- ----------------------------
DROP TABLE IF EXISTS `test_case_group`;
CREATE TABLE `test_case_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `case_group_name` varchar(255) NOT NULL COMMENT 'case分组名',
  `project_id` int(11) NOT NULL DEFAULT '0' COMMENT '所属项目id',
  `api_id` int(11) NOT NULL DEFAULT '0' COMMENT '所属api id',
  `account_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
