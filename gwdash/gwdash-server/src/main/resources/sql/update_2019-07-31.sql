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

use gwdash_intra;


DROP TABLE IF EXISTS `filter_info`;
CREATE TABLE `filter_info` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `version` varchar(20) NOT NULL,
  `author` varchar(128) DEFAULT NULL,
  `desc` varchar(128) DEFAULT NULL,
  `git_address` varchar(128) NOT NULL,
  `params` varchar(1024) DEFAULT NULL,
  `data` mediumblob,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `creator` varchar(128) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `project_id` int(32) DEFAULT NULL,
  `cname` varchar(128) DEFAULT NULL,
  `next_version_data` mediumblob,
  `online_status` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

alter table api_info add filter_params varchar(1024);

alter table plugin_info add project_id int;
alter table plugin_info add flow_key varchar(100);

CREATE TABLE `approval` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `key` varchar(128) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `projectId` int(32) DEFAULT NULL,
  `applicantId` int(32) DEFAULT NULL,
  `auditorId` int(32) DEFAULT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  `biz_id` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `project` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `desc` varchar(100) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `gitAddress` varchar(200) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `project_role` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `projectId` int(32) DEFAULT NULL,
  `accountId` int(32) DEFAULT NULL,
  `roleType` int(32) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

ALTER TABLE tesla_ds ADD api_package varchar(128);
ALTER TABLE tesla_ds ADD threads int(32);