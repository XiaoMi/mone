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

ALTER TABLE `api_info` ADD COLUMN `priority` INT(32) NULL COMMENT 'priority';

CREATE TABLE `project_compilation` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` int(32) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `jar_name` varchar(512) DEFAULT NULL,
  `branch` varchar(128) DEFAULT NULL,
  `url` varchar(512) DEFAULT NULL,
  `deploy_status` int(32) DEFAULT NULL,
  `jar_key` varchar(512) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `step` int(32) DEFAULT NULL,
  `param_setting` varchar(1024) DEFAULT NULL,
  `profile` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

