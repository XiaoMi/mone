/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

ALTER TABLE `project_deploy_record`
    ADD COLUMN `deploy_info` JSON NULL;

ALTER TABLE `project_env` ADD COLUMN `pipeline_id` BIGINT(64) NULL DEFAULT '0' COMMENT '当前环境对应的部署记录';

ALTER TABLE `project_env_policy` CHANGE COLUMN `time` `batch_num` INT(32) NULL DEFAULT '2' COMMENT '部署批次';

ALTER TABLE `project`
    ADD COLUMN `git_group` VARCHAR(128) NULL,
    ADD COLUMN `git_name` VARCHAR(128) NULL,
    ADD COLUMN `project_gen` JSON NULL;

ALTER TABLE `project_env_deploy_setting`
    ADD COLUMN `is_docker` INT(32) NULL DEFAULT '0',
    ADD COLUMN `log_path` VARCHAR(128) NULL,
    ADD COLUMN `memory` BIGINT(64) NULL DEFAULT '1073741824',
    ADD COLUMN `docker_port` INT(32) NULL DEFAULT '20880',
    ADD COLUMN `heapSize` BIGINT(64) NULL DEFAULT '512',
    ADD COLUMN `cpu` VARCHAR(45) NULL DEFAULT '0',
    ADD COLUMN `blkio_weight` INT(32) NULL DEFAULT '500';

ALTER TABLE `project_pipeline`
    ADD COLUMN `code_check_id` BIGINT(64) NULL DEFAULT '0',
    ADD COLUMN `build_type` VARCHAR(128) NULL,
    ADD COLUMN `rollback_id` BIGINT(64) NULL DEFAULT '0',
    ADD COLUMN `ctime` BIGINT(64) NULL,
    ADD COLUMN `utime` BIGINT(64) NULL;

ALTER TABLE `filter_info`
    ADD COLUMN `git_name` VARCHAR(128) NULL,
    ADD COLUMN `git_group` VARCHAR(128) NULL,
    ADD COLUMN `commit_id` VARCHAR(128) NULL,
    ADD COLUMN `compile_id` BIGINT(64) NULL;

ALTER TABLE `plugin_info`
    ADD COLUMN `git_group` VARCHAR(128) NULL,
    ADD COLUMN `git_name` VARCHAR(128) NULL;

ALTER TABLE `plugin_data`
    ADD COLUMN `compile_id` BIGINT(64) NULL,
    ADD COLUMN `commit_id` VARCHAR(128) NULL;

CREATE TABLE `gitlab_access_token` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `username` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `token` varchar(1024) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `project_code_check_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `step` int(32) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `time` bigint(64) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `project_compilation_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `jar_name` varchar(512) DEFAULT NULL,
  `branch` varchar(128) DEFAULT NULL,
  `url` varchar(512) DEFAULT NULL,
  `jar_key` varchar(512) DEFAULT NULL,
  `step` int(32) DEFAULT NULL,
  `env_id` bigint(64) DEFAULT NULL,
  `pipeline_id` bigint(64) DEFAULT NULL,
  `time` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;