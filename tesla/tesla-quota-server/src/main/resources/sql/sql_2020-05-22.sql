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

ALTER TABLE `resource` ADD COLUMN `project_ids` json default NULL;
update `resource` set project_ids="[]";



update `resource` set project_ids="[5,8,9]" where id = 1;
update `resource` set project_ids="[5,8,9,34,35]" where id = 2;
update `resource` set project_ids="[5,8]" where id = 3;
update `resource` set project_ids="[5,8]" where id = 4;
update `resource` set project_ids="[5,9]" where id = 5;

update `resource` set project_ids="[5,9]" where id = 6;
update `resource` set project_ids="[9,34,35,38,40,41,42]" where id = 7;
update `resource` set project_ids="[8,9,50]" where id = 8;
update `resource` set project_ids="[8,43]" where id = 9;
update `resource` set project_ids="[8,9,49]" where id = 10;

update `resource` set project_ids="[8,9]" where id = 11;
update `resource` set project_ids="[8,5]" where id = 12;
update `resource` set project_ids="[36,12]" where id = 13;
update `resource` set project_ids="[37]" where id = 14;
update `resource` set project_ids="[36]" where id = 15;

update `resource` set project_ids="[37]" where id = 16;
update `resource` set project_ids="[36]" where id = 17;
update `resource` set project_ids="[37]" where id = 18;
update `resource` set project_ids="[36]" where id = 19;
update `resource` set project_ids="[37]" where id = 20;

update `resource` set project_ids="[36]" where id = 21;
update `resource` set project_ids="[36]" where id = 22;
update `resource` set project_ids="[5]" where id = 23;
update `resource` set project_ids="[5]" where id = 24;
update `resource` set project_ids="[5]" where id = 25;

update `resource` set project_ids="[5]" where id = 26;
update `resource` set project_ids="[5]" where id = 27;
update `resource` set project_ids="[36]" where id = 28;
update `resource` set project_ids="[36]" where id = 29;
update `resource` set project_ids="[36]" where id = 30;

update `resource` set project_ids="[36]" where id = 31;
update `resource` set project_ids="[36]" where id = 32;
update `resource` set project_ids="[36]" where id = 33;
update `resource` set project_ids="[49，50]" where id = 34;
update `resource` set project_ids="[]" where id = 35;
update `resource` set project_ids="[49，50]" where id = 36;



