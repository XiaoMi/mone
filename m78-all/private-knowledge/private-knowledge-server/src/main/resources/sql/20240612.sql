ALTER TABLE v_knowledge_vector_meta ADD COLUMN `gmt_create` datetime DEFAULT NULL COMMENT '创建时间';
ALTER TABLE v_knowledge_vector_meta ADD COLUMN `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间';
ALTER TABLE v_knowledge_vector_meta ADD COLUMN `deleted` int(1) DEFAULT 0 COMMENT '0：未删除 1：已删除';
