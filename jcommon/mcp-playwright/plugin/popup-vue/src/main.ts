import '@fortawesome/fontawesome-free/css/all.css'
import 'uno.css'
import 'element-plus/dist/index.css'
import './assets/main.css'

// 导入 Element Plus 暗色主题
import 'element-plus/theme-chalk/dark/css-vars.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ElementPlus from 'element-plus'

import App from './App.vue'
import router from './router'
import i18n from './locales'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(i18n)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}

app.mount('#app')
