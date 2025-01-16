ALTER TABLE `m78_bot_plugin_org` ADD COLUMN official TINYINT(1) DEFAULT 0 COMMENT '0:非官方,1:官方';
ALTER TABLE m78_flow_base ADD COLUMN outputs JSON DEFAULT NULL COMMENT 'end节点output';
