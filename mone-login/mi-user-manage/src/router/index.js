import Vue from 'vue';
import VueRouter from 'vue-router';
import store from '../store/index';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    component: () => import('../views/layout/MenuLayout.vue'),
    redirect: {
      name: 'UserLogin',
    },
  },
  {
    path: '/user-manage/regist',
    name: 'UserRegist',
    component: () => import('../views/user-manage/RegistPage.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/user-manage/login',
    name: 'UserLogin',
    component: () => import('../views/user-manage/LoginPage.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/user-manage/find-password',
    name: 'FindPwd',
    component: () => import('../views/user-manage/FindPwd.vue'),
    meta: { title: '找回密码' },
  },
  {
    path: '/user-manage/password-reset',
    name: 'ResetPwd',
    component: () => import('../views/user-manage/ResetPwd.vue'),
    meta: { title: '重置密码' },
  },
  {
    path: '/user-manage/login/code',
    name: 'LoginCode',
    component: () => import('../views/user-manage/LoginCode.vue'),
    meta: { title: '三方授权中...' },
  },
  {
    path: '/user-manage/test',
    name: 'LoginCode',
    component: () => import('../views/user-manage/testPage.vue'),
    meta: { title: '测试页面' },
  },
];

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes,
});

router.beforeEach((to, from, next) => {
  store.dispatch('NodeModule/GETENUMS'); // 获取字典
  if (to.path.indexOf('/user-manage') < 0) { // 不是用户管理页面
    store.dispatch('UserModule/GETUSERINFO'); // 获取用户信息
  }
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next();
});

export default router;
