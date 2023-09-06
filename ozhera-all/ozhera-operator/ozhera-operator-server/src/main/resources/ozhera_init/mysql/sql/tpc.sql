CREATE
DATABASE  IF NOT EXISTS `mi_tpc`  DEFAULT CHARACTER SET utf8mb4 ;

USE
`mi_tpc`;

-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile

CREATE TABLE `account_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(64)  DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime NOT NULL COMMENT 'Creation Time',
    `update_time`  datetime NOT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `account`      varchar(64)  DEFAULT '' COMMENT 'Account',
    `pwd`          varchar(64)  DEFAULT '' COMMENT 'Password',
    `name`         varchar(64)  DEFAULT '' COMMENT 'Name',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `account`      varchar(64)  DEFAULT '' COMMENT 'Account',
    PRIMARY KEY (`id`),
    KEY            `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_group_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `group_name`   varchar(32)  DEFAULT '' COMMENT 'Group Name',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_group_rel_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `group_id`     int(20) DEFAULT '0' COMMENT 'Group ID',
    `user_id`      int(20) DEFAULT '0' COMMENT 'User ID',
    `account`      varchar(64) DEFAULT '' COMMENT 'Account',
    `user_type`    int(1) DEFAULT '0' COMMENT 'User Type',
    PRIMARY KEY (`id`),
    KEY            `idx_group_id` (`group_id`),
    KEY            `idx_user_id` (`user_id`),
    KEY            `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `node_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'type',
    `status`       int(1) DEFAULT '0' COMMENT 'status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'description',
    `content`      varchar(256) DEFAULT NULL COMMENT 'content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'creator account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'creator type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'updater account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'updater type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'create time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'update time',
    `deleted`      int(1) DEFAULT '0' COMMENT 'delete or not 0->no 1->deleted',
    `parent_id`    int(20) DEFAULT '0' COMMENT 'parent node Id',
    `parent_type`  int(1) DEFAULT '0' COMMENT 'parent node type',
    `top_id`       int(20) DEFAULT '0' COMMENT 'root node ID',
    `top_type`     int(1) DEFAULT '0' COMMENT 'root node type',
    `node_name`    varchar(64)  DEFAULT '' COMMENT 'node name',
    `out_id`       int(20) DEFAULT '0' COMMENT 'external ID type',
    `out_id_type`  int(2) DEFAULT '0' COMMENT 'external ID type',
    `env_flag`     int(2) DEFAULT '0' COMMENT 'env flag',
    `code`         varchar(64)  DEFAULT '' COMMENT 'node encoding',
    PRIMARY KEY (`id`),
    KEY            `idx_parent_id` (`parent_id`),
    KEY            `idx_top_id` (`top_id`),
    KEY            `idx_out_id` (`out_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node_user_rel_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `user_id`      int(20) DEFAULT '0' COMMENT 'User ID',
    `account`      varchar(64) DEFAULT '' COMMENT 'Account',
    `user_type`    int(1) DEFAULT '0' COMMENT 'User Type',
    `node_id`      int(20) DEFAULT '0' COMMENT 'Node ID',
    `node_type`    int(1) DEFAULT '0' COMMENT 'Node Type',
    `tester`       int(1) DEFAULT '0' COMMENT '0 for No, 1 for Yes',
    PRIMARY KEY (`id`),
    KEY            `idx_user_id` (`user_id`),
    KEY            `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `node_user_group_rel_entity`
(
    `id`              int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`            int(1) DEFAULT '0' COMMENT 'Type',
    `status`          int(1) DEFAULT '0' COMMENT 'Status',
    `desc`            varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`         varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`      int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`     varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`    int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`      int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`     varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`    int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`     datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`     datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`         int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `user_group_id`   int(20) DEFAULT '0' COMMENT 'User Group ID',
    `user_group_name` varchar(64) DEFAULT '' COMMENT 'User Group Name',
    `node_id`         int(20) DEFAULT '0' COMMENT 'Node ID',
    `node_type`       int(1) DEFAULT '0' COMMENT 'Node Type',
    PRIMARY KEY (`id`),
    KEY               `idx_user_id` (`user_group_id`),
    KEY               `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `flag_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `parent_id`    int(20) DEFAULT '0' COMMENT 'Parent node ID',
    `flag_name`    varchar(64)  DEFAULT NULL COMMENT 'Name',
    `flag_key`     varchar(128) DEFAULT NULL COMMENT 'Key',
    `flag_val`     varchar(128) DEFAULT NULL COMMENT 'Value',
    PRIMARY KEY (`id`),
    KEY            `idx_parent_id` (`parent_id`),
    KEY            `idx_flag_key` (`flag_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `node_resource_rel_entity`
(
    `id`            int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`          int(1) DEFAULT '0' COMMENT 'Type',
    `status`        int(1) DEFAULT '0' COMMENT 'Status',
    `desc`          varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`       varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`    int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`   varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`  int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`    int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`   varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`  int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`   datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`   datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`       int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `resource_id`   int(20) DEFAULT '0' COMMENT 'Resource ID',
    `resource_type` int(1) DEFAULT '0' COMMENT 'Resource Type',
    `node_id`       int(20) DEFAULT '0' COMMENT 'Node ID',
    `node_type`     int(1) DEFAULT '0' COMMENT 'Node Type',
    PRIMARY KEY (`id`),
    KEY             `idx_resource_id` (`resource_id`),
    KEY             `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `resource_entity`
(
    `id`            int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`          int(1) DEFAULT '0' COMMENT 'Type',
    `status`        int(1) DEFAULT '0' COMMENT 'Status',
    `desc`          varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`       text         DEFAULT NULL COMMENT 'Content',
    `creater_id`    int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`   varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`  int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`    int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`   varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`  int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`   datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`   datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`       int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `pool_node_id`  int(20) DEFAULT '0' COMMENT 'Resource Pool ID',
    `apply_id`      int(20) DEFAULT '0' COMMENT 'Application Workorder ID',
    `resource_name` varchar(64)  DEFAULT '' COMMENT 'Resource Name',
    `key1`          varchar(64)  DEFAULT '' COMMENT 'Resource Tag 1',
    `key2`          varchar(256) DEFAULT '' COMMENT 'Resource Tag 2',
    `env_flag`      int(2) DEFAULT '0' COMMENT 'Environment Flag',
    `is_open_kc`    int(2) DEFAULT '0' COMMENT 'Whether to Use Keycenter (0: Not Used, 1: Used)',
    `sid`           varchar(255) DEFAULT '' COMMENT 'SID Used by Keycenter',
    `kc_user`       varchar(255) DEFAULT '' COMMENT 'Keycenter User',
    `mfa`           varchar(255) DEFAULT '' COMMENT 'Keycenter MFA',
    `region`        int(2) DEFAULT '0' COMMENT 'Resource Region',
    PRIMARY KEY (`id`),
    KEY             `idx_pool_node_id` (`pool_node_id`),
    KEY             `idx_apply_id` (`apply_id`),
    KEY             `idx_key1` (`key1`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `apply_entity`
(
    `id`              int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`            int(1) DEFAULT '0' COMMENT 'Type',
    `status`          int(1) DEFAULT '0' COMMENT 'Status',
    `desc`            varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`         text         DEFAULT NULL COMMENT 'Content',
    `creater_id`      int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`     varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`    int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`      int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`     varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`    int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`     datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`     datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`         int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `cur_node_id`     int(20) DEFAULT '0' COMMENT 'Current Node ID',
    `cur_node_type`   int(1) DEFAULT '0' COMMENT 'Current Node Type',
    `apply_node_id`   int(20) DEFAULT '0' COMMENT 'Application Node ID',
    `apply_node_type` int(1) DEFAULT '0' COMMENT 'Application Node Type',
    `apply_user_id`   int(20) DEFAULT '0' COMMENT 'Applicant ID',
    `apply_account`   varchar(64)  DEFAULT '' COMMENT 'Applicant Account',
    `apply_user_type` int(1) DEFAULT '0' COMMENT 'Applicant User Type',
    `apply_name`      varchar(64)  DEFAULT '' COMMENT 'Application Name',
    PRIMARY KEY (`id`),
    KEY               `idx_cur_node_id` (`cur_node_id`),
    KEY               `idx_apply_node_id` (`apply_node_id`),
    KEY               `idx_apply_user_id` (`apply_user_id`),
    KEY               `idx_apply_account` (`apply_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `apply_approval_entity`
(
    `id`            int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`          int(1) DEFAULT '0' COMMENT 'Type',
    `status`        int(1) DEFAULT '0' COMMENT 'Status',
    `desc`          varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`       text         DEFAULT NULL COMMENT 'Content',
    `creater_id`    int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`   varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`  int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`    int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`   varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`  int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`   datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`   datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`       int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `apply_id`      int(20) DEFAULT '0' COMMENT 'Application ID',
    `apply_type`    int(1) DEFAULT '0' COMMENT 'Application Type',
    `cur_node_id`   int(20) DEFAULT '0' COMMENT 'Approval Node ID',
    `cur_node_type` int(1) DEFAULT '0' COMMENT 'Approval Node Type',
    `approval_name` varchar(64)  DEFAULT '' COMMENT 'Approval Name',
    PRIMARY KEY (`id`),
    KEY             `idx_apply_id` (`apply_id`),
    KEY             `idx_cur_node_id` (`cur_node_id`),
    KEY             `idx_creater_id` (`creater_id`),
    KEY             `idx_creater_acc` (`creater_acc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `system_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `system_name`  varchar(64)  DEFAULT '' COMMENT 'System Name',
    `system_token` varchar(64)  DEFAULT '' COMMENT 'System Token',
    PRIMARY KEY (`id`),
    KEY            `idx_system_name` (`system_name`),
    KEY            `idx_system_token` (`system_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `permission_entity`
(
    `id`              int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`            int(1) DEFAULT '0' COMMENT 'Type',
    `status`          int(1) DEFAULT '0' COMMENT 'Status',
    `desc`            varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`         varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`      int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`     varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`    int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`      int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`     varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`    int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`     datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`     datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`         int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `system_id`       int(20) DEFAULT '0' COMMENT 'System ID',
    `permission_name` varchar(64)  DEFAULT '' COMMENT 'Permission Name',
    `path`            varchar(128) DEFAULT '' COMMENT 'Permission URL',
    PRIMARY KEY (`id`),
    KEY               `idx_system_id` (`system_id`),
    KEY               `idx_path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `role_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(128) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(128) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64)  DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime     DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime     DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `system_id`    int(20) DEFAULT '0' COMMENT 'System ID',
    `role_name`    varchar(64)  DEFAULT '' COMMENT 'Role Name',
    `node_id`      int(20) DEFAULT '0' COMMENT 'Node ID',
    `node_type`    int(1) DEFAULT '0' COMMENT 'Node Type',
    PRIMARY KEY (`id`),
    KEY            `idx_system_id` (`system_id`),
    KEY            `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `role_permission_rel_entity`
(
    `id`            int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`          int(1) DEFAULT '0' COMMENT 'Type',
    `status`        int(1) DEFAULT '0' COMMENT 'Status',
    `desc`          varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`       varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`    int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`   varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type`  int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`    int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`   varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type`  int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`   datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`   datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`       int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `system_id`     int(20) DEFAULT '0' COMMENT 'System ID',
    `permission_id` int(20) DEFAULT '0' COMMENT 'Permission ID',
    `role_id`       int(20) DEFAULT '0' COMMENT 'Role ID',
    PRIMARY KEY (`id`),
    KEY             `idx_permission_id` (`permission_id`),
    KEY             `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user_node_role_rel_entity`
(
    `id`           int(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique Record',
    `type`         int(1) DEFAULT '0' COMMENT 'Type',
    `status`       int(1) DEFAULT '0' COMMENT 'Status',
    `desc`         varchar(64) DEFAULT NULL COMMENT 'Description',
    `content`      varchar(64) DEFAULT NULL COMMENT 'Content',
    `creater_id`   int(20) DEFAULT '0' COMMENT 'Creator ID',
    `creater_acc`  varchar(64) DEFAULT NULL COMMENT 'Creator Account',
    `creater_type` int(1) DEFAULT '0' COMMENT 'Creator Type',
    `updater_id`   int(20) DEFAULT '0' COMMENT 'Updater ID',
    `updater_acc`  varchar(64) DEFAULT NULL COMMENT 'Updater Account',
    `updater_type` int(1) DEFAULT '0' COMMENT 'Updater Type',
    `create_time`  datetime    DEFAULT NULL COMMENT 'Creation Time',
    `update_time`  datetime    DEFAULT NULL COMMENT 'Update Time',
    `deleted`      int(1) DEFAULT '0' COMMENT '0 for Normal, 1 for Deleted',
    `user_id`      int(20) DEFAULT '0' COMMENT 'User ID',
    `account`      varchar(64) DEFAULT '' COMMENT 'Account',
    `user_type`    int(1) DEFAULT '0' COMMENT 'User Type',
    `node_id`      int(20) DEFAULT '0' COMMENT 'Node ID',
    `node_type`    int(1) DEFAULT '0' COMMENT 'Node Type',
    `role_id`      int(20) DEFAULT '0' COMMENT 'Role ID',
    `system_id`    int(20) DEFAULT '0' COMMENT 'System ID',
    PRIMARY KEY (`id`),
    KEY            `idx_user_id` (`user_id`),
    KEY            `idx_account` (`account`),
    KEY            `idx_user_node_id` (`node_id`),
    KEY            `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
