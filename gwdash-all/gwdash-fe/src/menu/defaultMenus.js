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
import downgrade from './modules/downgrade'
import dubboCheck from './modules/dubboCheck'

let defaultMenu = [
  {
    path: '/index',
    title: '首页',
    icon: 'home', // icon属性暂不使用，需配置bgImage属性[将菜单图标上传有品图床cdn]
    bgImage: 'home'
  },
  gateway,
  application,
  project,
  quality,
  sre,
  nacos,
  cat,
  wiki,
  devTest,
  miSchedule,
  traffic,
  downgrade,
  dubboCheck
]

export default defaultMenu