import { createApp } from "vue"
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import store from './store'
import './permission'
import Sortable from 'sortablejs'
import './plugins/element'
import * as utils from './utils'
import './icons'
import i18n from './lang'
import { showLoading, hideLoading } from './common/loading'
import moment from 'moment'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


moment.locale('zh-cn')

const APP = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    APP.component(key, component)
}

// APP.config.productionTip = false
APP.config.globalProperties.$utils = utils
// 拖拽库
APP.config.globalProperties.$Sortable = Sortable
APP.config.globalProperties.$Loading = { stop: hideLoading, start: showLoading }

APP.use(router).use(store).use(i18n).use(ElementPlus, { size: 'mini' }).mount("#app")
console.log(22333)