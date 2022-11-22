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
 Navicat MySQL Data Transfer

 Source Server         : local_docker
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 127.0.0.1
 Source Database       : gateway_web

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : utf-8

 Date: 07/31/2020 12:01:45 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `usage_record`
-- ----------------------------
DROP TABLE IF EXISTS `usage_record`;
CREATE TABLE `usage_record` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `envId` bigint(11) DEFAULT NULL,
  `cpuCount` int(11) unsigned zerofill DEFAULT NULL,
  `cpuUsage` decimal(5,4) unsigned zerofill DEFAULT NULL,
  `memoryUsage` decimal(5,4) unsigned zerofill DEFAULT NULL,
  `ctime` bigint(20) DEFAULT NULL,
  `utime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
