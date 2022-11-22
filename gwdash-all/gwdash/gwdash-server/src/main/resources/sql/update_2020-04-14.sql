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

CREATE TABLE `docker_image_info` (
  `id` bigint(64) UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_name` varchar(128) NOT NULL DEFAULT "",
  `project_name` varchar(20) NOT NULL DEFAULT "",
  `desc` varchar(128) NOT NULL DEFAULT "",
  `git_address` varchar(128) NOT NULL DEFAULT "",
  `commit_id` varchar(128) NOT NULL DEFAULT "",
  `compilation_id` bigint(64) NOT NULL DEFAULT 0,
  `ctime` bigint(64) NOT NULL DEFAULT 0,
  `utime` bigint(64) NOT NULL DEFAULT 0,
  `creator` varchar(128) NOT NULL DEFAULT "",
  `updater` varchar(128) NOT NULL DEFAULT "",
  `status` int(32) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
