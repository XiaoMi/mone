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
  title: '运维中心',
  icon: 'scribd',
  bgImage: 'sre',
  children: (pre => [
    {
      path: `${pre}machine/list`,
      title: '机器列表',
      icon: 'server'
    },
    {
      path: `${pre}machine/apply`,
      title: '申请机器记录',
      icon: 'server'
    },
    {
      path: `${pre}agent/list`,
      title: '远程调试',
      icon: 'terminal'
    },
    {
      path: `${pre}agent/ssh`,
      title: '远程控制台(Beta)',
      icon: 'terminal'
    },
    {
      path: `${pre}nginx/list`,
      title: 'nginx治理',
      icon: 'window-maximize'
    },
    {
      path: `${pre}error/list`,
      title: "错误列表",
      icon: "cogs"
    },
    {
      path: `${pre}resource/list`,
      title: "资源列表",
      icon: "cogs"
    },
    {
      path: `${pre}deploy/onsiteInspection`,
      title: "每日巡检",
      icon: "cogs"
    },
    {
      path: `${pre}quota/record`,
      title: "quota记录",
      icon: "cogs"
    },
    {
      path: `${pre}health/check`,
      title: "健康监测",
      icon: "cogs"
    }
  ])('/sre/')
}
