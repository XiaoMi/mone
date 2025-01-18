CREATE TABLE `m78_api_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_id` bigint NOT NULL COMMENT '关联id，比如空间id、botId、flowId、知识库Id，默认空间id',
  `type` int NOT NULL default 1 COMMENT '1-空间，2-bot，3-flow，4-知识库',
  `api_key` CHAR(36) NOT NULL COMMENT '秘钥',
  `creator` varchar(128) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,
  KEY `idx_api_key` (`api_key`),
  KEY `idx_type_id` (`type_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;

ALTER TABLE m78_knowledge_base ADD version INT NOT NULL DEFAULT 0 COMMENT '版本标识';
ALTER TABLE m78_workspace ADD version INT NOT NULL DEFAULT 0 COMMENT '版本标识';

ALTER TABLE m78_code_generation_info ADD COLUMN bot_id BIGINT(20) DEFAULT NULL COMMENT 'botID';