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

# from 2019-12-25
CREATE TABLE `m_error` (
  `id` BIGINT(64) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(40) NOT NULL DEFAULT "",
  `service_name` varchar(50) NOT NULL DEFAULT "",
  `group` varchar(50) NOT NULL DEFAULT "",
  `utime` bigint(64) NOT NULL DEFAULT 0,
  `ctime` bigint(64) NOT NULL DEFAULT 0,
  `type` int(32) NOT NULL DEFAULT 0,
  `count` int(32) NOT NULL DEFAULT 0,
  `status` int(32) NOT NULL DEFAULT 0,
  `version` int(32) NOT NULL DEFAULT 0,
  `key` varchar(128) NOT NULL DEFAULT "",
  `content` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `project_java_doc` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(64) NOT NULL,
  `doc` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

alter table project_env
    add health_check_task_id int default 0,
    add health_check_result json NULL,
    add last_auto_scale_time bigint DEFAULT 0;

alter table project_env_deploy_setting add max_replicate bigint DEFAULT 0;
