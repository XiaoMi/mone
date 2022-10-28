DROP TABLE IF EXISTS `gw_group_info`;

CREATE TABLE `gw_group_info`
(
    `id`            INT(11) unsigned NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(255)     NOT NULL DEFAULT '' COMMENT '分组名称',
    `description`   VARCHAR(1023)    NOT NULL DEFAULT '' COMMENT '分组描述',
    `creation_date` DATETIME                  DEFAULT NULL,
    `modify_date`   DATETIME                  DEFAULT NULL,
    `status`        INT(11)                   DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '网关分组信息表' AUTO_INCREMENT = 44 DEFAULT CHARSET = utf8;



DROP TABLE IF EXISTS `gw_user_info`;

CREATE TABLE `gw_user_info`
(
    `id`                INT(11) unsigned    NOT NULL AUTO_INCREMENT,
    `user_name`         VARCHAR(100)        NOT NULL DEFAULT '' COMMENT '用户邮箱前缀',
    `user_phone`        VARCHAR(50)         NOT NULL DEFAULT '' COMMENT '用户电话',
    `gids`              VARCHAR(100)        DEFAULT '0',
    `create_date`       DATETIME            DEFAULT NOW(),
    `modify_date`       DATETIME            DEFAULT NOW() ON UPDATE NOW(),
    `status`            TINYINT             DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '网关分组用户映射表' AUTO_INCREMENT = 44 DEFAULT CHARSET = utf8;