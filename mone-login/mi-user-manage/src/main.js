import Vue from 'vue';
import ElementUI from 'element-ui';
import VueParticles from '@/lib/vue-particles';
import 'element-ui/lib/theme-chalk/index.css';
import { retUserNameType } from '@/common/js/get-user-type';
import App from './App.vue';
import router from './router';
import store from './store';
import '@/common/css/common.less'; // 全局样式

const dateFormat = require('dateformat');

Vue.prototype.$dateFormat = dateFormat;
Vue.prototype.$retUserNameType = retUserNameType;
Vue.prototype.$requireMsg = [{ required: true, message: '此项为必填项', trigger: 'blur' }];
Vue.config.productionTip = false;
Vue.use(ElementUI, { size: 'mini' });
Vue.use(VueParticles);

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount('#app');
