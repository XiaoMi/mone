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

ALTER TABLE `project_env_machine`
    ADD COLUMN `app_id` BIGINT(64) NULL DEFAULT 0,
    ADD COLUMN `used` TINYINT NULL DEFAULT 1,
    ADD COLUMN `ctime` BIGINT(64) NULL DEFAULT 0,
    ADD COLUMN `utime` BIGINT(64) NULL DEFAULT 0,
    ADD COLUMN `version` INT(32) NULL;

ALTER TABLE `project_env`
    ADD COLUMN `status` INT(11) NULL DEFAULT 1;
