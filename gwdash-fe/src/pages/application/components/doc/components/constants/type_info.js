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

export const pjPre = { // 构建文档表中的projectId字段，其由prPre + id组成，pjPre要求是5位数字，最高位不能位0
    project: 10000,
    filter: 10001
  }
  
  export const options = [
    {
      label: '未归档',
      value: '0'
    },
    {
      label: '项目文档',
      value: '1'
    },
    {
      label: '评论',
      value: '2'
    },
    {
      label: 'issue',
      value: '3'
    },
    {
      label: 'issue回答',
      value: '4'
    },
    {
      label: '回复评论',
      value: '5'
    },
    {
      label: 'todo文档',
      value: '6'
    },
    {
      label: 'change log',
      value: '7'
    }
  ]
  
  export default {
    pjPre,
    options
  }
  