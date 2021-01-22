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

CREATE TABLE `tesla_tr_recording_config` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) DEFAULT "",
  `source_type` int(32) DEFAULT 0,
  `env_type` int(32) DEFAULT NULL,
  `url` varchar(128) DEFAULT "",
  `service_name` varchar(128) DEFAULT "",
  `service_group` varchar(128) DEFAULT NULL,
  `methods` varchar(1024) DEFAULT NULL,
  `version` varchar(64) DEFAULT NULL,
  `recording_strategy` int(32) DEFAULT 0,
  `percentage` int(32) DEFAULT 0,
  `uid` int(32) DEFAULT 0,
  `headers` json DEFAULT NULL,
  `status` int(32) DEFAULT 0,
  `save_days` int(32) DEFAULT 7,
  `create_time` bigint(64) DEFAULT NULL,
  `update_time` bigint(64) DEFAULT NULL,
  `creator` varchar(64) DEFAULT NULL,
  `updater` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `k_source_env` (`source_type`,`env_type`,`status`),
  KEY `k_status` (`status`),
  KEY `k_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE `tesla_tr_traffic` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `recording_config_id` int(32) DEFAULT 0,
  `source_type` int(32) DEFAULT 0,
  `origin_headers` json DEFAULT NULL,
  `modify_headers` json DEFAULT NULL,
  `http_method` varchar(128) DEFAULT "",
  `host` varchar(128) DEFAULT "",
  `url` varchar(128) DEFAULT "",
  `origin_query_string` TEXT DEFAULT NULL,
  `origin_body` TEXT DEFAULT NULL,
  `modify_body` TEXT DEFAULT NULL,
  `response` TEXT DEFAULT NULL,
  `invoke_begin_time` bigint(64) DEFAULT NULL,
  `invoke_end_time` bigint(64) DEFAULT NULL,
  `trace_id` varchar(128) DEFAULT "",
  `uid` int(32) DEFAULT 0,
  `save_days` int(32) DEFAULT 7,
  `create_time` bigint(64) DEFAULT NULL,
  `update_time` bigint(64) DEFAULT NULL,
  `creator` varchar(64) DEFAULT NULL,
  `updater` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `k_recording_config_id` (`recording_config_id`),
  KEY `k_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

CREATE TABLE `tesla_tr_replay_result` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `traffic_id` int(32) DEFAULT 0,
  `result` TEXT DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `k_traffic_id` (`traffic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


