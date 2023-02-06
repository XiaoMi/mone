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
  title: "测试管理",
  icon: "newspaper-o",
  bgImage: 'application',
  children: (pre => [
    {
      path: `${pre}testcase`,
      title: "测试用例"
    },
    {
      path: `${pre}test/release/management`,
      title: "测试发布管理"
    }
  ])('/quality/')
}
