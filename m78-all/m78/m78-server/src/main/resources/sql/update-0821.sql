ALTER TABLE `m78_flow_base`  ADD COLUMN `official` int NULL AFTER `state`;
ALTER TABLE `m78_flow_base`  ADD COLUMN `flow_avg_star` double NULL ;

ALTER TABLE `m78_flow_test_record`  ADD COLUMN `input` json NULL ;
ALTER TABLE `m78_flow_test_record`  ADD COLUMN `node_inputs_map` json NULL ;
ALTER TABLE `m78_flow_test_record`  ADD COLUMN `node_outputs_map` json NULL ;


CREATE TABLE `m78_category_flow_rel`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `cat_id` bigint(20) NOT NULL COMMENT '分类id',
                                 `flow_id` bigint(20) NOT NULL COMMENT 'flow id',
                                 `deleted` int(1) NOT NULL COMMENT '是否删除0-否 1-是',
                                 `create_time` datetime NULL DEFAULT NULL,
                                 `update_time` datetime NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;