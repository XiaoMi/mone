CREATE TABLE `help_video` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ctime` bigint(64) DEFAULT NULL,
  `utime` bigint(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `project_env_build_setting` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `env_id` bigint(64) DEFAULT NULL,
  `build_dir` varchar(256) DEFAULT NULL,
  `jar_dir` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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

-- CREATE TABLE `machine_label` (
--  `id` bigint(64) NOT NULL AUTO_INCREMENT,
--  `ip` varchar(64) DEFAULT NULL,
--  `label_key` varchar(45) DEFAULT NULL,
--  `label_value` varchar(64) DEFAULT NULL,
--  `ctime` bigint(64) DEFAULT NULL,
--  `utime` bigint(64) DEFAULT NULL,
--  PRIMARY KEY (`id`),
--  UNIQUE KEY `unique` (`ip`,`label_key`,`label_value`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

ALTER TABLE `project_env`
    ADD COLUMN `branch` VARCHAR(128) NULL,
    ADD COLUMN `profile` VARCHAR(128) NULL,
     ADD COLUMN `deploy_type` INT(32) NULL;

ALTER TABLE `machine_list`
    ADD UNIQUE INDEX `ip_UNIQUE` (`ip` ASC),
    CHANGE COLUMN `name` `name` VARCHAR(512) NULL DEFAULT NULL ,
    CHANGE COLUMN `my_desc` `my_desc` VARCHAR(1024) NULL DEFAULT NULL,
    ADD COLUMN `hostname` VARCHAR(128) NULL,
    ADD COLUMN `labels` json;
