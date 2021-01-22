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

export default {
    title: '智能网关',
    icon: 'tachometer',
    bgImage: 'gateway',
    children: (pre => [
        {
            path: `${pre}apigroup/list`,
            title: '分组管理',
            icon: 'users'
        },
        {
            path: `${pre}apiinfo/list`,
            title: 'API管理',
            icon: 'list-alt'
        },
        {
            path: `${pre}plugins/list`,
            title: 'plugins',
            icon: 'plug'
        },
        {
            path: `${pre}cfilter/upload/list`,
            title: 'filter',
            icon: 'upload'
        },
        {
            path: `${pre}ds/list`,
            title: '数据源',
            icon: 'database'
        },
        {
            path: `${pre}agent/list`,
            title: '节点信息',
            icon: 'sitemap'
        },
        {
            path: `${pre}mirror/list`,
            title: '镜像市场',
            icon: 'sitemap'
        }
        // {
        //     title: '通用filter',
        //     icon: 'filter',
        //     children: [
        //       {
        //         path: `${pre}cfilter/upload/list`,
        //         title: 'filter上传',
        //         icon: 'upload'
        //       },
        //       {
        //         path: `${pre}/cfilter/audit/list`,
        //         title: 'filter审核',
        //         icon: 'legal'
        //       }
        //     ]
        // },
    ])('/gateway/')
}