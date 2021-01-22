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

const isAdmin = !!(userInfo && userInfo.role == 1)

export default {
    title: '账号管理',
    icon: 'id-card-o',
    children: (pre => {
        const accountList = [
            { path: `${pre}settings`,  title: '个人账号详情', icon: 'user-o' },
            { path: `${pre}gitlab`,  title: 'gitlab access token', icon: 'gitlab' }
        ]
        // 暂时去掉账号列表 - 目前权限管理都在Hermes
        // isAdmin && accountList.unshift({ path: `${pre}manage`,  title: '账号列表', icon: 'users', hidden: !isAdmin })
        return accountList
    })('/account/')
}
