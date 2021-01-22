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

import gateway from './modules/gateway'
import application from './modules/application'
import applicationOld from './modules/application-old'
import sre from './modules/sre'
import nacos from './modules/nacos'
import cat from './modules/cat'
import wiki from './modules/wiki'
import miSchedule from './modules/miSchedule'
import project from './modules/project'
import devTest from './modules/devTest'
import quality from './modules/quality'
import traffic from './modules/traffic'
const serverEnv = window.serverEnv
const userInfo = window.userInfo
const isNotC4 = !(serverEnv === 'c4')
const isNotOnline = !(serverEnv === 'c3' || serverEnv === 'c4' || serverEnv === 'intranet')
const isDev = serverEnv === `development`
// let isShowSre = (userInfo.roles || []).find(item => {
//     return item.name === 'sre' || item.name === 'admin'||item.name==="nginx-admin"
// })
//  let onlyShowNginx = ((roles)=>{
//     let roleNames = roles.map(role=>role.name);
//     if(roleNames.includes("nginx-admin")&&!roleNames.includes("sre")&&!roleNames.includes("admin")){
//         return true;
//     }
//     return false;
//  })(userInfo.roles)

// let isAdmin =(userInfo.roles || []).find(item => {
//     return  item.name === 'admin'
// })
let defaultMenu = [
  {
    path: '/index',
    title: '首页',
    icon: 'home', // icon属性暂不使用，需配置bgImage属性[将菜单图标上传有品图床cdn]
    bgImage: 'home'
  },
  gateway
  // application
  // project,
  // sre,
  // nacos,
  // cat,
  // wiki,
  // devTest,
  // miSchedule
]
isNotC4 && defaultMenu.push(application)
!isNotC4 && defaultMenu.push(applicationOld)
isNotC4 && defaultMenu.push(project)
defaultMenu.push(quality)
isNotC4 && defaultMenu.push(sre)
defaultMenu.push(nacos)
defaultMenu.push(cat)
isNotOnline && defaultMenu.push(wiki)
isNotOnline && defaultMenu.push(devTest)
defaultMenu.push(miSchedule)
defaultMenu.push(traffic)

let AuthFiter = function (defaultMenu, allRoutes) {
  // 目前只遍历2层 有需求就改成递归
  let ret = []
  defaultMenu.forEach(menu => {
    let copy = JSON.parse(JSON.stringify(menu))
    if (copy.children) {
      copy.children = []
    }
    if (!menu.path && menu.children && menu.children.length > 0) {
      let flag = false// 下面是否有子节点 有则保留一级入口
      menu.children.forEach(child => {
        if (allRoutes.includes(child.path)) {
          flag = true
          copy.children.push(child)
        }
      })
      if (flag) {
        ret.push(copy)
      }
    } else if (menu.path || menu.href) {
      if (allRoutes.includes(menu.path) || allRoutes.includes(menu.href)) {
        ret.push(menu)
      }
    }
  })
  return ret
}
if (!isDev) {
  defaultMenu = AuthFiter(defaultMenu, userInfo.resource.map(it => it.url))
}
// 菜单 侧边栏
export const menuAside = defaultMenu
// 非admin隐藏 资源列表
// if(!isAdmin){
//     sre.children=sre.children.filter(route=> route.path!=="/sre/resource/list")
// }
// if(onlyShowNginx){
//     sre.children = sre.children.filter(route=>route.path==="/sre/nginx/list")
// }
// isShowSre && menuAside.splice(3, 0, sre)

// 菜单顶部栏
// export const menuHeader = [
//     {
//         path: '/index',
//         title: '首页'，
//         icon: 'home',
//         bgImage:'https://img.youpin.mi-img.com/ArchitectureGroup/981d988db034d0d23c2c005764e41767.png?w=54&h=54'
//     },
//     gateway,
//     application,
//     sre,
//     nacos,
//     cat,
//     // account,
//     // miSchedule
// ]
// isNotOnline && menuHeader.push(wiki)

// export default {
//     menuAside,
//     // menuHeader
// }
