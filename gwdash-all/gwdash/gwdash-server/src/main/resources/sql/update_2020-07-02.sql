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

CREATE TABLE `apply_machine` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint(64) unsigned NOT NULL DEFAULT 0,
  `creator` varchar(512) NOT NULL DEFAULT '',
  `ctime` bigint(64) NOT NULL DEFAULT 0,
  `utime` bigint(64) NOT NULL DEFAULT 0,
  `order_res` text,
  `status` int(32) NOT NULL DEFAULT 0,
  `suit_id` varchar(1024) NOT NULL DEFAULT '',
  `order_detail` text,
  `catalyst_res` text,
  `init_seq` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;