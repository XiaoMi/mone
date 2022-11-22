import service from "@/plugin/axios"

let defaultMenu = [
  {
    path: '/index',
    title: '首页',
    icon: 'home', // icon属性暂不使用，需配置bgImage属性[将菜单图标上传有品图床cdn]
    bgImage: 'home'
  }
]

async function getDefaultMenu(defaultMenu) {
  let res = await service({url: '/menu/menu'});
  try {
    let newMenu = res && res.menu;
    defaultMenu = JSON.parse(newMenu) ? JSON.parse(newMenu) : defaultMenu
  }catch (err) {
    console.log(err,'menu无法匹配')
  }
  return defaultMenu
}

// getDefaultMenu(defaultMenu).then(res => {
//   return res
// })

export const menuAside = getDefaultMenu(defaultMenu)


// 兜底方案：
// import defaultMenus from './defaultMenus'
// export const menuAside = defaultMenus