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

/**
 * @description 首页<常见功能>模块 -> 展示用户使用频率较高的功能菜单
 */

export default {
  namespaced: true,
  state: {
    features: [
      {
        path: '/gateway/apigroup/list',
        desc: '分组管理',
        times: 0
      },
      {
        path: '/gateway/apiinfo/list',
        desc: 'API管理',
        times: 0
      },
      {
        path: '/gateway/plugins/list',
        desc: 'plugins',
        times: 0
      },
      {
        path: '/gateway/cfilter/upload/list',
        desc: 'filter',
        times: 0
      },
      {
        path: '/gateway/ds/list',
        desc: '数据源',
        times: 0
      },
      {
        path: '/gateway/agent/list',
        desc: '节点信息',
        times: 0
      },
      {
        path: '/gateway/mirror/list',
        desc: '镜像市场',
        times: 0
      },
      {
        path: '/application/list',
        desc: '',
        times: 0
      },
      {
        path: '/sre/machine/list',
        desc: '机器列表',
        times: 0
      },
      {
        path: '/sre/machine/apply',
        desc: '申请机器记录',
        times: 0
      },
      {
        path: '/sre/agent/list',
        desc: '远程调试',
        times: 0
      },
      {
        path: '/sre/agent/ssh',
        desc: '远程控制台(Beta)',
        times: 0
      },
      {
        path: '/sre/nginx/list',
        desc: 'nginx治理',
        times: 0
      },
      {
        path: '/sre/error/list',
        desc: '错误列表',
        times: 0
      },
      {
        path: '/sre/quota/record',
        desc: 'quota记录',
        times: 0
      },
      {
        path: '/sre/resource/list',
        desc: '资源列表',
        times: 0
      },
      {
        path: '/sre/deploy/onsiteInspection',
        desc: '每日巡检',
        times: 0
      },
      {
        path: '/nacos/instances',
        desc: '实例',
        times: 0
      },
      {
        path: '/nacos/configs',
        desc: '配置',
        times: 0
      },
      {
        path: '/cat',
        desc: '',
        times: 0
      },
      {
        path: '/wiki/doc/list',
        desc: '文档列表',
        times: 0
      },
      {
        path: '/wiki/issue/feedbacks',
        desc: '反馈列表',
        times: 0
      },
      {
        path: '/wiki/issue/feedback',
        desc: '问题反馈',
        times: 0
      }
    ]
  },
  mutations: {
    recordTimes (state, param) {
      // 洗数据
      if (Array.isArray(JSON.parse(localStorage.getItem('features')))) {
        const arr = JSON.parse(localStorage.getItem('features'));
        if(arr[6].path === '/application/project/list') {
          localStorage.removeItem('features');
        }
      }

      const features = JSON.parse(localStorage.getItem('features')) || state.features;
      features.forEach( item => {
        if (item.path === param ) {
          item.times++;
          localStorage.setItem('features',JSON.stringify(features));
          return;
        }
      })
    }
  }
}