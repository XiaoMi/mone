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
    title: '降级系统',
    icon: 'downgrade',
    bgImage: 'downgrade',
    children: (pre => [
        {
            path: `${pre}service/list`,
            title: '策略列表',
            icon: 'users'
        },
        {
            path: `${pre}operation/record`,
            title: '操作记录',
            icon: 'list-alt'
        }
    ])('/downgrade/')
}
