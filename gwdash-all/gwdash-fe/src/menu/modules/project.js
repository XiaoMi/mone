/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

export default {
  title: "项目审核",
  icon: "newspaper-o",
  bgImage: 'nacos',
  children: (pre => [
    {
      path: `${pre}auditList`,
      title: "审核列表",
      icon: "rocket"
    },
    {
      path: `${pre}operationLog`,
      title: '操作日志',
      icon: 'file'
    }
  ])('/projectAudit/')
}
