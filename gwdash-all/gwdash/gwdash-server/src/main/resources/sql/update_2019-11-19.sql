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

#2019-09-07 ~ now
#

ALTER TABLE `api_info` CHANGE COLUMN `description` `description` varchar(500) DEFAULT '' COMMENT 'api描述';

ALTER TABLE `api_info` ADD COLUMN `priority` INT(32) NULL COMMENT 'priority';

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

ALTER TABLE `project`
    ADD COLUMN `git_group` VARCHAR(128) NULL,
    ADD COLUMN `git_name` VARCHAR(128) NULL,
    ADD COLUMN `project_gen` JSON NULL;

CREATE TABLE `user_collection` (
  `id` bigint(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `apiInfoId` int(11) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# ALTER TABLE `project_env` ADD COLUMN `pipeline_id` BIGINT(64) NULL DEFAULT '0' COMMENT '当前环境对应的部署记录';
CREATE TABLE `project_env` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `project_id` bigint(64) DEFAULT NULL,
  `project_name` varchar(1024) DEFAULT NULL,
  `my_group` varchar(45) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `pipeline_id` bigint(64) DEFAULT '0' COMMENT '当前环境对应的部署记录',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

# ALTER TABLE `project_env_policy` CHANGE COLUMN `time` `batch_num` INT(32) NULL DEFAULT '2' COMMENT '部署批次';
CREATE TABLE `project_env_policy` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `env_id` bigint(64) DEFAULT NULL,
  `deployment` varchar(64) DEFAULT NULL,
  `stop` varchar(64) DEFAULT NULL,
  `batch_num` int(32) DEFAULT '2' COMMENT '部署批次',
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ALTER TABLE `project_env_deploy_setting`
--     ADD COLUMN `is_docker` INT(32) NULL DEFAULT '0',
--     ADD COLUMN `log_path` VARCHAR(128) NULL,
--     ADD COLUMN `memory` BIGINT(64) NULL DEFAULT '1073741824',
--     ADD COLUMN `docker_port` INT(32) NULL DEFAULT '20880',
--     ADD COLUMN `heapSize` BIGINT(64) NULL DEFAULT '512',
--     ADD COLUMN `cpu` VARCHAR(45) NULL DEFAULT '0',
--     ADD COLUMN `blkio_weight` INT(32) NULL DEFAULT '500';

CREATE TABLE `project_env_deploy_setting` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `env_id` bigint(64) DEFAULT NULL,
  `path` varchar(128) DEFAULT NULL,
  `is_docker` int(32) DEFAULT '0',
  `log_path` varchar(128) DEFAULT NULL,
  `memory` bigint(64) DEFAULT '1073741824',
  `docker_port` int(32) DEFAULT '20880',
  `heapSize` bigint(64) DEFAULT '512',
  `cpu` varchar(45) DEFAULT '0',
  `blkio_weight` int(32) DEFAULT '500',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ALTER TABLE `project_pipeline`
--     ADD COLUMN `code_check_id` BIGINT(64) NULL DEFAULT '0',
--     ADD COLUMN `build_type` VARCHAR(128) NULL,
--     ADD COLUMN `rollback_id` BIGINT(64) NULL DEFAULT '0',
--     ADD COLUMN `ctime` BIGINT(64) NULL,
--     ADD COLUMN `utime` BIGINT(64) NULL;
CREATE TABLE `project_pipeline` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(64) DEFAULT NULL,
  `env_id` bigint(64) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `compilation_id` bigint(64) DEFAULT NULL,
  `deployment_id` bigint(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `deploy_info` json DEFAULT NULL,
  `code_check_id` bigint(64) DEFAULT '0',
  `build_type` varchar(128) DEFAULT NULL,
  `rollback_id` bigint(64) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `machine_list` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `my_group` varchar(45) DEFAULT NULL,
  `my_desc` varchar(128) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `project_env_machine` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `env_id` bigint(64) DEFAULT NULL,
  `machine_id` bigint(64) DEFAULT NULL,
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

 CREATE TABLE `project_deploy_record` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `jar_id` bigint(64) DEFAULT NULL,
  `jar_name` varchar(128) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `operatio` varchar(128) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  `project_id` int(32) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `deploy_info` JSON DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
