CREATE TABLE `v_knowledge_vector_meta` (
  `id` int NOT NULL AUTO_INCREMENT primary key,
  `type` varchar(64) DEFAULT NULL COMMENT '知识库类型,normal_document、project_code',
  `tag1` varchar(64) NOT NULL COMMENT 'tag1',
  `tag2` varchar(64) DEFAULT '' COMMENT 'tag2',
  `tag3` varchar(64) DEFAULT '' COMMENT 'tag3',
  `tag4` varchar(64) DEFAULT '' COMMENT 'tag4',
  `tag5` varchar(64) DEFAULT '' COMMENT 'tag5',
  `tag6` varchar(64) DEFAULT '' COMMENT 'tag6',
  `group_tag` varchar(64) NOT NULL COMMENT 'group节点tag',
  INDEX (type, tag1, tag2, tag3, tag4, tag5, tag6),
  INDEX type_group_tag_index (type, group_tag),
  UNIQUE KEY type_group_tag_unique (type, group_tag)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='知识库向量元数据，仅存储至group节点级别(除leaf层级外，最多支持六层tag结构)';


CREATE TABLE `v_knowledge_vector_detail` (
  `id` int NOT NULL AUTO_INCREMENT primary key,
  `meta_id` int NOT NULL COMMENT 'v_knowledge_vector_meta的id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `type` varchar(64) DEFAULT NULL COMMENT '知识库类型,normal_document、project_code',
  `leaf_tag` varchar(64) NOT NULL COMMENT '叶子节点tag',
  `content` text DEFAULT NULL COMMENT '区块/文件内容',
  `embedding_status` int(11) NOT NULL DEFAULT '0' COMMENT '向量化状态0:未开始, 1:进行中, 2:已完成',
  `vector` blob NOT NULL,
  FOREIGN KEY (meta_id) REFERENCES v_knowledge_vector_meta (id),
  INDEX type_leaf_tag_index (type, leaf_tag),
  UNIQUE KEY type_leaf_tag_unique (type, leaf_tag)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='知识库向量详情，仅存储叶子节点';
