create table m78.m78_meta
(
    `id`                 bigint unsigned auto_increment primary key,
    `uuid`               varchar(255)  default ''                not null comment 'UUID',
    `table_name`         varchar(1024) default ''                not null comment '',
    `create_time`        datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`        timestamp     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`             int           default 0                 not null comment '预留, 行状态标记',
    `user_name`          varchar(255)  default ''                not null comment '用户名',
    `favorite`           tinyint(1) default 0 not null comment '是否为收藏, 0: 非收藏, 1：收藏',
    `type`               int           default 0                 not null comment '类型标记, 0：excel文档, 1：翻译文本的输入, 2: 翻译文本的输出',
    `original_file_name` varchar(1024) default ''                not null comment '上传文件原始名',
    `custom_knowledge`   text comment 'meta信息的文本',
    unique key unq_uuid (uuid)
) charset = utf8mb4;

create table m78.m78_translate
(
    `id`               bigint unsigned auto_increment primary key,
    `uuid`             varchar(255) default ''                not null comment 'UUID',
    `status`           int          default 0                 not null comment '预留, 行状态标记',
    `type`             int          default 0                 not null comment '类型标记',
    `favorite`         tinyint(1) default 0 not null comment '是否为收藏, 0: 非收藏, 1：收藏',
    `from_language`    varchar(255) default ''                not null comment '源语种',
    `to_language`      varchar(255) default ''                not null comment '目标语种',
    `from_text`        mediumtext comment '翻译文本的输入',
    `to_text`          mediumtext comment '翻译文本的输出',
    `custom_knowledge` text comment 'meta信息的文本',
    `user_name`        varchar(255) default ''                not null comment '用户名',
    `create_time`      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`      timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    unique key unq_uuid (uuid)
) charset = utf8mb4;

create table m78.m78_contact_info
(
    `id`                     bigint unsigned auto_increment primary key,
    `contact_name`           varchar(255) default ''                not null comment '联系名',
    `contact_email`          varchar(255) default ''                not null comment '联系email',
    `contact_subject`        varchar(255) default ''                not null comment '主题',
    `contact_content`        mediumtext comment '内容',
    `processing_status`      int          default 0                 not null comment '处理状态',
    `processing_person_name` varchar(255) default ''                not null comment '处理人名',
    `user_name`              varchar(255) default ''                not null comment '用户名',
    `create_time`            datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`            timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) charset = utf8mb4;

-- 创建聊天题目表
CREATE TABLE `m78_chat_topics`
(
    `id`          INT AUTO_INCREMENT PRIMARY KEY, -- 聊天题目ID
    `title`       VARCHAR(255) NOT NULL,          -- 聊天题目的标题
    `user_name`   varchar(200),
    `description` TEXT,                           -- 聊天题目的描述
    `ctime`       BIGINT       NOT NULL,          -- 创建时间（单位：毫秒）
    `utime`       BIGINT       NOT NULL,          -- 最后更新时间（单位：毫秒）
    `state`       INT          NOT NULL,          -- 状态（可以用来表示聊天题目是否活跃等信息）
    INDEX (ctime),
    INDEX (utime),
    INDEX ( state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建聊天信息表
CREATE TABLE `m78_chat_messages`
(
    `id`           INT AUTO_INCREMENT PRIMARY KEY,          -- 聊天信息ID
    `topic_id`     INT    NOT NULL,                         -- 关联的聊天题目ID
    `user_name`    varchar(200),
    `message_role` varchar(200),
    `message`      TEXT   NOT NULL,                         -- 聊天信息内容
    `ctime`        BIGINT NOT NULL,                         -- 创建时间（单位：毫秒）
    `utime`        BIGINT NOT NULL,                         -- 最后更新时间（单位：毫秒）
    `state`        INT    NOT NULL,                         -- 状态（可以用来表示聊天信息是否已读、删除等信息）
    `meta`         json default null,
    FOREIGN KEY (topic_id) REFERENCES m78_chat_topics (id), -- 外键约束，关联到聊天题目表的ID
    INDEX (ctime),
    INDEX (utime),
    INDEX ( state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文档询问记录表
create table m78.m78_chat_info
(
    `id`              bigint unsigned auto_increment primary key,
    `session_id`      varchar(255) default ''                not null comment 'sessionId, relate to UUID',
    `content`         mediumtext comment '单条聊天记录内容',
    `mapping_content` mediumtext comment '聊天记录对应映射内容，eg: sql',
    `conditions`      json         default null comment '解析后的查询条件',
    `user_name`       varchar(255) default ''                not null comment '用户名',
    `heat`            bigint       default 0                 not null comment '热度，排序预留',
    `create_time`     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`     timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`          int          default 0                 not null comment '预留, 行状态标记'
        `chat_info_meta` json default null comment '存储元数据',
) charset = utf8mb4;

-- 用户配置表
CREATE TABLE `m78_user_config`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `ctime`        bigint(20) DEFAULT NULL,
    `utime`        bigint(20) DEFAULT NULL,
    `state`        int(11) DEFAULT NULL,
    `user_name`    varchar(255) DEFAULT NULL,
    `model_config` json         DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- 使用utf8mb4以支持完整的Unicode字符集

-- 数据源连接信息表
CREATE TABLE `m78_connection_info`
(
    `id`               int unsigned auto_increment,
    `host`             varchar(255) DEFAULT '',
    `port`             varchar(255) DEFAULT '',
    `database`         varchar(255) DEFAULT '',
    `user`             varchar(255) DEFAULT '',
    `pwd`              varchar(255) DEFAULT '',
    `jdbc_url`         varchar(255) DEFAULT '',
    `cluster`          varchar(255) DEFAULT NULL,
    `kerberos`         varchar(255) DEFAULT NULL,
    `queue`            varchar(255) DEFAULT NULL,
    `type`             int          DEFAULT 0,
    `custom_knowledge` text comment 'meta信息的文本',
    `user_name`        varchar(255) DEFAULT NULL,
    `create_time`      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`      timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_feature_router`
(
    `id`          bigint unsigned auto_increment primary key,
    `name`        varchar(255) default null comment 'feature router名',
    `user_name`   varchar(255) default null comment '所属用户名',
    `label_id`    bigint unsigned default null comment '对应的chatInfoId',
    `status`      int          default 0                 not null comment '预留, 行状态标记',
    `create_time` datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time` timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `router_meta` json         default null comment '请求参数信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_agent`
(
    `id`      BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(255),
    `role`    VARCHAR(255),
    `status`  INT,
    `ctime`   BIGINT(20) DEFAULT NULL,
    `utime`   BIGINT(20) DEFAULT NULL,
    `creator` VARCHAR(255),
    `meta`    JSON,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_agent_messages`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `source_agent` BIGINT(20) DEFAULT NULL,
    `target_agent` BIGINT(20) DEFAULT NULL,
    `content`      TEXT,
    `status`       INT,
    `ctime`        BIGINT(20) DEFAULT NULL,
    `utime`        BIGINT(20) DEFAULT NULL,
    `meta`         JSON,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_agent_contacts`
(
    `id`               BIGINT(20) NOT NULL AUTO_INCREMENT,
    `label`            VARCHAR(255),
    `agent_id`         BIGINT(20) DEFAULT NULL,
    `contact_agent_id` BIGINT(20) DEFAULT NULL,
    `status`           INT,
    `ctime`            BIGINT(20) DEFAULT NULL,
    `utime`            BIGINT(20) DEFAULT NULL,
    `meta`             JSON,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_workspace`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(64)  NOT NULL COMMENT 'workspace名称',
    `creator`     varchar(128) NOT NULL COMMENT '创建人',
    `deleted`     int(1) NOT NULL COMMENT '是否删除0-否 1-是',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY           `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_bot`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `name`         varchar(128) NOT NULL COMMENT '名称',
    `workspace_id` bigint(20) NOT NULL COMMENT 'workspaceId',
    `remark`       varchar(512) DEFAULT NULL COMMENT '备注',
    `creator`      varchar(255) DEFAULT NULL,
    `permissions`  int(1) NOT NULL COMMENT '开放权限0-私有 1-公开',
    `deleted`      int(1) NOT NULL COMMENT '是否删除 0-否 1-是',
    `create_time`  datetime     DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY            `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_bot_character_setting`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `bot_id`      bigint(20) NOT NULL COMMENT 'bot id',
    `setting`     varchar(2000) NOT NULL COMMENT 'bot设定',
    `deleted`     int(1) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY           `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_category`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(128) NOT NULL COMMENT '类目名称',
    `deleted`     int(1) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_category_bot_rel`
(
    `id`          bigint(20) NOT NULL,
    `cat_id`      bigint(20) NOT NULL COMMENT '分类id',
    `bot_id`      bigint(20) NOT NULL COMMENT 'bot id',
    `deleted`     int(1) NOT NULL COMMENT '是否删除0-否 1-是',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE `m78_bot_plugin`
(
    `id`                bigint unsigned auto_increment primary key,
    `org_id`            bigint unsigned default null comment '插件所属组织id',
    `workspace_id` bigint(20)    default NULL COMMENT 'workspaceId',
    `name`              varchar(1024) default ''                not null comment '名称',
    `api_url`           text comment '插件api_url',
    `avatar_url`        varchar(1024) DEFAULT NULL,
    `feature_router_id` bigint unsigned default null comment '导出的featureRouterId',
    `meta`              json          default null,
    `release_time`      datetime      default null comment '发布时间',
    `create_time`       datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`       timestamp     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`            int           default 0                 not null comment '预留, 行状态标记, 目前用作启用状态',
    `debug_status`      int           default 0                 not null comment '调试状态, 0: 未调试, 1: 调试成功, 2: 调试失败'
    `user_name`         varchar(255)  default ''                not null comment '用户名',
    `modifier`          varchar(255)  default ''                not null comment '更新人',
    `type`              int           default 0                 not null comment '类型标记, 0：featureRouter类型，1: 自定义类型',
    `category`          varchar(1024) default ''                not null comment '分类',
    `category_id`       bigint unsigned default null comment '插件所属分类id',
    `description`       text comment '描述(可以很长)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_bot_plugin_org`
(
    `id`           bigint unsigned auto_increment primary key,
    `workspace_id` bigint(20)    default NULL COMMENT 'workspaceId',
    `name`         varchar(1024) default ''                not null comment '名称',
    `avatar_url`   varchar(1024) DEFAULT NULL,
    `release_time` datetime      default null comment '最新的插件发布时间',
    `create_time`  datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time`  timestamp     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`       int           default 0                 not null comment '预留, 行状态标记; 现用作上线状态',
    `user_name`    varchar(255)  default ''                not null comment '用户名',
    `modifier`     varchar(255)  default ''                not null comment '更新人',
    `type`         int           default 0                 not null comment '类型标记, 预留',
    `description`  text comment '描述(可以很长)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_bot_plugin_favorite`
(
    `id`          bigint unsigned auto_increment primary key,
    `plugin_id`   bigint unsigned default null comment '插件id',
    `create_time` datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time` timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`      int          default 0                 not null comment '预留, 行状态标记',
    `user_name`   varchar(255) default ''                not null comment '用户名',
    `type`        int          default 0                 not null comment '类型标记, 预留'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_bot_plugin_usage`
(
    `plugin_id`   bigint unsigned default null comment '插件id',
    `bot_id`      bigint unsigned default null comment 'bot id',
    `create_time` datetime  default CURRENT_TIMESTAMP not null comment '创建时间',
    `modify_time` timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `status`      int       default 0                 not null comment '预留, 行状态标记',
    `type`        int       default 0                 not null comment '类型标记, 预留'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_code`
(
    id      bigint       auto_increment primary key,
    utime   bigint       not null,
    ctime   bigint       not null,
    code    json         null,
    creator varchar(128) not null,
    type    int          not null,
    name    varchar(255) not null,
    `desc`  text         null,
    model   varchar(255) null comment '模型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;;

create table m78.m78_custom_task
(
    `id`                 bigint unsigned auto_increment primary key,
    `task_name`          varchar(50)   default ''     not null comment 'task_name',
    `task_type`          int           default 1      not null comment 'task_type',
    `scheduled_time`     varchar(30)                  not null comment 'scheduled_time',
    `status`             int           default 1      not null comment 'status',
    `bot_id`             bigint(20)               not null comment 'bot_id',
    `moon_id`            bigint(20),
    `task_detail`    json         null,
    `core_type`          varchar(15),
    `user_name`          varchar(25)   default ''     not null comment '用户名',
    `task_description`   varchar(1024) default ''     not null comment 'task_description',
    `ctime`  BIGINT(20) DEFAULT NULL,
    `utime`  BIGINT(20) DEFAULT NULL
) charset = utf8mb4;