CREATE TABLE `m78_bot`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `name`           varchar(128) NOT NULL COMMENT '名称',
    `workspace_id`   bigint(20) NOT NULL COMMENT 'workspaceId',
    `remark`         varchar(512) DEFAULT NULL COMMENT '备注',
    `creator`        varchar(255) DEFAULT "",
    `updator`        varchar(255) DEFAULT "",
    `avatar_url`     varchar(255) DEFAULT NULL,
    `permissions`    int(1) NOT NULL DEFAULT '0' COMMENT '开放权限0-私有 1-公开',
    `publish_status` int(1) NOT NULL DEFAULT '0',
    `publish_time`   datetime     DEFAULT NULL,
    `create_time`    datetime     DEFAULT NULL,
    `update_time`    datetime     DEFAULT NULL,
    `deleted`        int(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0-否 1-是',
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY              `idx_workspace_id` (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_bot_character_setting`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `bot_id`                  bigint(20) NOT NULL COMMENT 'bot id',
    `setting`                 text COMMENT 'bot人设',
    `ai_model`                varchar(64)  NOT NULL COMMENT '模型',
    `dialogue_turns`          int(2) NOT NULL COMMENT '对话轮次',
    `opening_remarks`         text COMMENT '开场白文案',
    `opening_ques`            json                  DEFAULT NULL COMMENT '开场白问题',
    `customize_prompt_switch` int(2) NOT NULL COMMENT '预留问题开关，0-关闭 1-开启',
    `customize_prompt`        varchar(1000)         DEFAULT NULL COMMENT '预留问题prompt',
    `timbre_switch`           int(2) NOT NULL COMMENT '音色开关，0-关闭 1-开启',
    `timbre`                  varchar(128)          DEFAULT NULL COMMENT '音色',
    `deleted`                 int(1) NOT NULL DEFAULT '0' NOT NULL,
    `creator`                 varchar(128) NOT NULL DEFAULT "" COMMENT '创建人',
    `updater`                 varchar(128) NOT NULL DEFAULT "" COMMENT '更新人',
    `create_time`             datetime              DEFAULT NULL,
    `update_time`             datetime              DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY                       `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_bot_publish_record`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `bot_id`             bigint(20) NOT NULL,
    `version_record`     varchar(2000) NOT NULL COMMENT '版本记录',
    `bot_snapshot`       text COMMENT 'bot快照',
    `publish_im_channel` json     DEFAULT NULL COMMENT '发布渠道，ex:[1,2,3], id参考m78_im_type',
    `publisher`          varchar(64)   NOT NULL COMMENT '发布人',
    `publish_time`       datetime DEFAULT NULL COMMENT '发布时间',
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



CREATE TABLE `m78_im_type`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(128) NOT NULL,
    `deleted`     varchar(255) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_im_relation`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `bot_id`        bigint(20) NOT NULL COMMENT '机器人id',
    `im_type_id`    int(2) NOT NULL COMMENT 'im类型id',
    `relation_flag` varchar(512) NOT NULL COMMENT '关联标志，比如openId',
    `deleted`       int(1) NOT NULL,
    `creator`       varchar(128) NOT NULL COMMENT '创建人',
    `create_time`   datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
    KEY             `idx_relation_flag` (`relation_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
