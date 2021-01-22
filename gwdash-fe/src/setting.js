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

import { version } from '../package'

const setting = {
    // 快捷键
    // 支持快捷键 例如 ctrl+shift+s
    hotkey: {
        search: {
            open: false,
            close: 'esc'
        }
    },
    // 侧边栏默认折叠状态
    menu: {
        asideCollapse: false
    },
    // 在读取持久化数据失败时默认页面
    page: {
        opened: [
            {
                name: 'index',
                meta: {
                    title: '首页',
                    requiresAuth: false
                }
            }
        ]
    },
    // 版本
    releases: {
        version: version,
        api: 'https://api.github.com/repos/FairyEver/d2-admin/releases/latest'
    },
    // 菜单搜索
    search: {
        enable: true
    },
    // 注册的主题
    theme: {
        list: [
            {
                title: 'd2admin 经典',
                name: 'd2',
                preview: 'image/theme/d2/preview@2x.png'
            }
        ]
    },
    // 是否默认开启页面切换动画
    transition: {
        active: true
    },
    // 在读取持久化数据失败时默认用户信息
    user: {
        info: {
            name: 'Ghost'
        }
    }
}

export default setting
