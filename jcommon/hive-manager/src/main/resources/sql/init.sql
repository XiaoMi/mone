CREATE TABLE `t_agent`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) NOT NULL COMMENT 'Agent名称',
    `description`  text COMMENT 'Agent描述',
    `agent_url`    varchar(255)  DEFAULT NULL COMMENT 'Agent URL',
    `created_by`   bigint(20)   NOT NULL COMMENT '创建者ID',
    `is_public`    tinyint(1)    DEFAULT '0' COMMENT '是否公开',
    `ctime`        bigint(20)   NOT NULL COMMENT '创建时间',
    `utime`        bigint(20)   NOT NULL COMMENT '更新时间',
    `agent_group`  varchar(255)  DEFAULT NULL,
    `version`      varchar(255)  DEFAULT '',
    `state`        int(11)       DEFAULT '1' COMMENT 'Agent状态：1-正常，0-禁用',
    `image`        mediumblob,
    `tool_map`     text,
    `mcp_tool_map` text,
    `profile`      varchar(1000) DEFAULT NULL COMMENT 'Agent profile',
    `goal`         varchar(1000) DEFAULT NULL COMMENT 'Agent goal',
    `constraints`  varchar(1000) DEFAULT NULL COMMENT 'Agent constraints',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_name_group_version` (`name`, `agent_group`, `version`),
    KEY `idx_created_by` (`created_by`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 46
  DEFAULT CHARSET = utf8mb4 COMMENT ='Agent表';

CREATE TABLE `t_agent_access`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `agent_id`      bigint(20)   NOT NULL COMMENT 'agent ID',
    `access_app`    varchar(100) NOT NULL COMMENT '访问应用名称',
    `access_key`    varchar(64)  NOT NULL COMMENT '访问密钥',
    `description`   varchar(255)          DEFAULT NULL COMMENT '描述',
    `state`         tinyint(4)   NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `ctime`         bigint(20)   NOT NULL COMMENT '创建时间',
    `utime`         bigint(20)   NOT NULL COMMENT '更新时间',
    `access_app_id` int(11)               DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_app_id` (`agent_id`, `access_app_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 31
  DEFAULT CHARSET = utf8mb4 COMMENT ='agent访问授权表';

CREATE TABLE `t_agent_config`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id`     bigint(20)                              DEFAULT NULL COMMENT '代理ID',
    `user_id`      bigint(20)                              DEFAULT NULL COMMENT '用户ID',
    `config_key`   varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置键',
    `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
    `ctime`        bigint(20)                              DEFAULT NULL COMMENT '创建时间',
    `utime`        bigint(20)                              DEFAULT NULL COMMENT '更新时间',
    `state`        int(11)                                 DEFAULT '1' COMMENT '状态，1-正常，0-删除',
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='代理配置表';

CREATE TABLE `t_agent_instance`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT,
    `agent_id`            bigint(20)   NOT NULL,
    `ip`                  varchar(255) NOT NULL,
    `port`                int(11)      NOT NULL,
    `last_heartbeat_time` bigint(20) DEFAULT NULL,
    `is_active`           tinyint(1) DEFAULT '1',
    `ctime`               bigint(20) DEFAULT NULL,
    `utime`               bigint(20) DEFAULT NULL,
    `state`               int(11)    DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_ip_port` (`agent_id`, `ip`, `port`),
    CONSTRAINT `t_agent_instance_ibfk_1` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 637
  DEFAULT CHARSET = utf8;

CREATE TABLE `t_favorite`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`   int(11)    NOT NULL COMMENT '用户ID',
    `target_id` int(11)    NOT NULL COMMENT '目标ID',
    `type`      int(11)    NOT NULL COMMENT '类型',
    `state`     int(11)    NOT NULL DEFAULT '1' COMMENT '状态：0-无效 1-有效',
    `ctime`     bigint(20) NOT NULL COMMENT '创建时间',
    `utime`     bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target_id` (`target_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 26
  DEFAULT CHARSET = utf8mb4 COMMENT ='收藏表';

CREATE TABLE `t_skill`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id`      bigint(20)            DEFAULT NULL COMMENT 'Agent ID',
    `name`          varchar(255) NOT NULL COMMENT '技能名称',
    `skill_id`      varchar(255)          DEFAULT NULL COMMENT '技能ID',
    `description`   text COMMENT '技能描述',
    `tags`          text COMMENT '技能标签',
    `examples`      text COMMENT '示例',
    `output_schema` text COMMENT '输出模式定义',
    `ctime`         bigint(20)   NOT NULL COMMENT '创建时间',
    `utime`         bigint(20)   NOT NULL COMMENT '更新时间',
    `state`         tinyint(4)   NOT NULL DEFAULT '1' COMMENT '状态：1-正常，0-删除',
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_skill_id` (`skill_id`),
    KEY `idx_state` (`state`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4 COMMENT ='技能表';

CREATE TABLE `t_task`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_uuid`       varchar(255)        DEFAULT NULL COMMENT '任务UUID',
    `client_agent_id` bigint(20)          DEFAULT NULL COMMENT '客户端AgentID',
    `server_agent_id` bigint(20)          DEFAULT NULL COMMENT '服务端AgentID',
    `skill_id`        bigint(20)          DEFAULT NULL COMMENT '技能ID',
    `title`           varchar(255)        DEFAULT NULL COMMENT '任务标题',
    `description`     text COMMENT '任务描述',
    `status`          varchar(50)         DEFAULT NULL COMMENT '任务状态',
    `result`          longtext COMMENT '任务结果',
    `ctime`           bigint(20) NOT NULL COMMENT '创建时间',
    `utime`           bigint(20) NOT NULL COMMENT '更新时间',
    `state`           tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-正常，0-删除',
    `task_content`    longtext COMMENT '任务内容',
    `metadata`        longtext COMMENT '任务相关元数据',
    `username`        varchar(255)        DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_task_uuid` (`task_uuid`),
    KEY `idx_client_agent_id` (`client_agent_id`),
    KEY `idx_server_agent_id` (`server_agent_id`),
    KEY `idx_state` (`state`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = utf8mb4 COMMENT ='任务表';

CREATE TABLE `t_user`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `username`         varchar(255) NOT NULL,
    `password`         varchar(255) NOT NULL,
    `email`            varchar(255) NOT NULL,
    `ctime`            bigint(20)  DEFAULT NULL,
    `utime`            bigint(20)  DEFAULT NULL,
    `state`            int(11)     DEFAULT NULL,
    `token`            varchar(64) DEFAULT NULL,
    `internal_account` varchar(64) DEFAULT NULL COMMENT '公司内部账号绑定',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 22
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `t_invoke_history`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type`             int(11)             DEFAULT NULL COMMENT '类型, 1agent',
    `relate_id`        bigint(20)          DEFAULT NULL COMMENT '关联ID',
    `inputs`           text                DEFAULT NULL COMMENT '输入参数',
    `outputs`          text                DEFAULT NULL COMMENT '输出结果',
    `invoke_time`      bigint(20)          DEFAULT NULL COMMENT '调用时间',
    `invoke_way`       int(11)             DEFAULT NULL COMMENT '调用方式, 1页面, 2接口, 3系统内部, 4调试等等',
    `invoke_user_name` varchar(64)         DEFAULT NULL COMMENT '调用用户名',
    `ctime`            bigint(20) NOT NULL COMMENT '创建时间',
    `utime`            bigint(20) NOT NULL COMMENT '更新时间',
    `state`            int(11)    NOT NULL DEFAULT '1' COMMENT '状态：1-正常，0-删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_relate_id` (`relate_id`),
    KEY `idx_invoke_user_name` (`invoke_user_name`),
    KEY `idx_ctime` (`ctime`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='调用历史记录表';