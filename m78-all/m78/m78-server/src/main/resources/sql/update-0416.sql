CREATE TABLE `m78_flow_test_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `flow_base_id` BIGINT(20) DEFAULT NULL,
  `runner` varchar(255) NOT NULL,
  `status` INT DEFAULT '-1',
  `execute_type` INT DEFAULT '0',
  `duration`   BIGINT(20)  DEFAULT NULL,
  `start_time`  BIGINT(20) DEFAULT NULL,
  `end_flow_output` JSON,
  PRIMARY KEY (`id`),
  INDEX (flow_base_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `m78_flow_test_node_snapshot` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `flow_record_id` BIGINT(20) NOT NULL,
  `node_id` BIGINT(20) NOT NULL,
  `status` INT DEFAULT '0',
  `errorInfo` varchar(1024) NOT NULL,
  `duration`   BIGINT(20)  DEFAULT NULL,
  `node_input` JSON,
  `node_output` JSON,
  PRIMARY KEY (`id`),
  INDEX (flow_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;