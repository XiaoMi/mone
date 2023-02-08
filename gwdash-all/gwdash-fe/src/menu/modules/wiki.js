
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
  title: '文档管理',
  icon: 'wikipedia-w',
  bgImage: 'wiki',
  children: (pre => [
    {
      path: `${pre}doc/list`,
      title: '文档列表',
      icon: 'file-word-o'
    },
    {
      path: `${pre}issue/feedbacks`,
      title: '反馈列表',
      icon: 'list'
    },
    {
      path: `${pre}issue/feedback`,
      title: '问题反馈',
      icon: 'feed'
    }
  ])('/wiki/')
}
