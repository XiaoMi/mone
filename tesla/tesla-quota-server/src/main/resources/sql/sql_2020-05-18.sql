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

CREATE TABLE `record` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `ip` varchar(128) DEFAULT NULL,
  `biz_id` bigint(64) DEFAULT NULL,
  `project_before` json DEFAULT NULL,
  `project_after` json DEFAULT NULL,
  `resource_before` json DEFAULT NULL,
  `resource_after` json DEFAULT NULL,
  `type_op` varchar(128) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  `version` int(32) DEFAULT NULL,
  `status` int DEFAULT 0,
  `operator` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
