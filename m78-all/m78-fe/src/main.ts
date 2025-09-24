import { createApp } from 'vue'
import { createPinia } from 'pinia'
// import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import '@/utils/polyfill'

import App from './App.vue'
import router from './router'
import { setupStore } from './stores'

import '@/styles/index.scss'
import 'uno.css'
// If you want to use ElMessage, import it.
import 'element-plus/theme-chalk/src/message.scss'
import 'element-plus/theme-chalk/src/message-box.scss'
import { setupI18n } from './locales'
import JsonViewer from 'vue3-json-viewer'
import 'vue3-json-viewer/dist/index.css'
import contextmenu from 'v-contextmenu'

declare global {
  interface Window {
    __APP_INFO__: {
      buildTime: string
    }
  }
}

const app = createApp(App)

// app.use(ElementPlus)

setupStore(app)

setupI18n(app)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(JsonViewer)
app.use(contextmenu)
app.use(pinia)
app.use(router)

app.mount('#app')

const styles = [
  'color: white',
  'background: green',
  'font-size: 19px',
  'border: 1px solid #fff',
  'text-shadow: 2px 2px black',
  'padding: 5px'
].join(';')
console.log(`%c版本更新时间: ${__APP_INFO__.buildTime}`, styles)
console.log(`%c浏览器信息: ${navigator.userAgent}`, styles)
