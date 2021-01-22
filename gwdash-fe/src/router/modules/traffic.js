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

import layoutHeaderAside from '@/layout/header-aside'

export default {
  path: '/traffic/',
  name: 'traffic',
  meta: { title: '流量录制', icon: 'newspaper-o' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'record/config',
      name: 'recordconfig',
      meta: { title: 'recordconfig', icon: 'thermometer-quarter' },
      component: () => import('@/pages/traffic/record-config')
    },
    {
      path: 'record/result',
      name: 'recordresult',
      meta: { title: 'recordresult', icon: 'thermometer-quarter' },
      component: () => import('@/pages/traffic/record-result')
    },
    {
      path: 'playback/config',
      name: 'playbackconfig',
      meta: { title: 'playbackconfig', icon: 'thermometer-quarter' },
      component: () => import('@/pages/traffic/playback-config')
    },
    {
      path: 'playback/result',
      name: 'playbackresult',
      meta: { title: 'playbackresult', icon: 'thermometer-quarter' },
      component: () => import('@/pages/traffic/playback-result')
    }
  ]
}