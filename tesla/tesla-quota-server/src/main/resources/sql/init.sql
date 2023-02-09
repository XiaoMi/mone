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

CREATE TABLE `resource` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `ip` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `host_name` varchar(128) DEFAULT NULL,
  `cpu` int(32) DEFAULT NULL,
  `remain_cpu` int(32) DEFAULT NULL,
  `biz_ids` json DEFAULT NULL,
  `labels` json DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE `quota` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `resource_id` int(32) DEFAULT NULL,
  `biz_id` bigint(64) DEFAULT NULL,
  `cpu` int(32) DEFAULT NULL,
  `ip` varchar(128) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


CREATE TABLE `quota_request` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `biz_id` bigint(64) DEFAULT NULL,
  `num` int(32) DEFAULT NULL,
  `cpu` int(32) DEFAULT NULL,
  `quotas` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;