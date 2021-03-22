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
  dev: {
    name: '日常',
    icon: 'circle',
    type: 'dev'
  },
  staging: {
    name: 'staging',
    icon: 'circle',
    type: 'staging'
  },
  preview: {
    name: '预发',
    icon: 'circle',
    type: 'preview'
  },
  intranet: {
    name: '内网',
    icon: 'circle',
    type: 'intranet'
  },
  production: {
    name: '线上集群',
    icon: 'circle',
    type: 'production'
  },
  c3: {
    name: '线上c3集群',
    icon: 'circle',
    type: 'c3'
  },
  c4: {
    name: '线上c4集群',
    icon: 'circle',
    type: 'c4'
  },
  options: [{
    key: 'dev',
    value: '日常'
  }, {
    key: 'staging',
    value: 'staging'
  }, {
    key: 'preview',
    value: '预发'
  }, {
    key: 'intranet',
    value: '内网'
  }, {
    key: 'production',
    value: '线上集群'
  }, {
    key: 'c3',
    value: '线上c3集群'
  }, {
    key: 'c4',
    value: '线上c4集群'
  }]
}
