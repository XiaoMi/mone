CREATE TABLE `m78_asr_cost`
(
    `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
    `product_line`  varchar(50) NOT NULL DEFAULT '' comment '业务线',
    `asr_platform`  varchar(10) NOT NULL DEFAULT '' comment 'asr厂商',
    `used_time`     BIGINT(20) NOT NULL DEFAULT 0  comment '使用时长，单位秒',
    `ctime`         int(10) NOT NULL DEFAULT 0 comment '创建时间',
    `utime`         int(10) NOT NULL DEFAULT 0 comment '更新时间',
    PRIMARY KEY  (`id`),
    UNIQUE KEY      `product_asr` (`product_line`, `asr_platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;