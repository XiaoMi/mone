import { createRouter, createWebHashHistory } from 'vue-router'
import { PATH } from './constant'
import i18n from '../lang'

import Layout from '@/views/Layout.vue'
import Outer from "@/views/Outer.vue"
import { isExtranet } from "@/utils"

export const secRoutes = [
  {
    path: PATH.API,
    component: Layout,
    meta: { title: i18n.t('router.apiList'), type: 'submenu' },
    children: [{
      path: PATH.API,
      name: 'api',
      component: () => import(/* webpackChunkName: "apilist" */'@/views/ApiList/index.vue'),
      meta: { title: i18n.t('router.apiList'), type: 'submenu' },
      children: [{
        path: PATH.API,
        name: 'api',
        component: () => import(/* webpackChunkName: "apilist-list" */'@/views/ApiList/components/List/index.vue'),
        meta: { title: 'API Interface', type: 'submenu', activeMenu: true },
        hidden: true
      }, {
        path: PATH.ADD_API,
        name: 'addapi',
        component: () => import(/* webpackChunkName: "add-api" */'@/views/AddApi/index.vue'),
        meta: { title: 'New api', type: 'submenu' },
        hidden: true
      }, {
        path: PATH.API_DETAIL,
        name: 'apidetail',
        component: () => import(/* webpackChunkName: "api-detail" */'@/views/ApiDetail/index.vue'),
        meta: { title: 'API details', type: 'submenu' },
        hidden: true
      }, {
        path: PATH.IMPORT_INDEX,
        name: 'importindex',
        component: () => import(/* webpackChunkName: "import-index" */'@/views/ImportIndex/index.vue'),
        meta: { title: 'Import collection', type: 'submenu' },
        hidden: true
      }, {
        path: PATH.ADD_MOCK,
        name: 'addmock',
        component: () => import(/* webpackChunkName: "add-mock" */'@/views/AddMock/index.vue'),
        meta: { title: 'New mock', type: 'submenu' },
        hidden: true
      }, {
        path: PATH.ADD_INDEX_DOC,
        name: 'addindexdoc',
        component: () => import(/* webpackChunkName: "add-index-doc" */'@/views/AddIndexDoc/index.vue'),
        meta: { title: 'New collection document', type: 'submenu' },
        hidden: true
      }]
    }]
  },
  {
    path: PATH.API_INDEX,
    component: Layout,
    meta: { title: 'API collection', type: 'submenu' },
    children: [{
      path: PATH.API_INDEX,
      name: 'indexlist',
      component: () => import(/* webpackChunkName: "apilist" */'@/views/ApiList/index.vue'),
      meta: { title: i18n.t('router.apiCollection'), type: 'submenu', activeMenu: true },
      children: [{
        path: PATH.API_INDEX,
        name: 'indexlist',
        component: () => import(/* webpackChunkName: "apilistIndexList" */'@/views/IndexList/index.vue'),
        meta: { title: i18n.t('router.apiCollection'), type: 'submenu', activeMenu: true }
      }]
    }]
  },
  // 因为要显示tab 目前只能先放第一层，导致模块渲染方式不同
  {
    path: PATH.API_TEST,
    component: Layout,
    meta: { title: i18n.t('router.apiTest'), type: 'submenu' },
    children: [{
      path: PATH.API_TEST,
      name: 'apitest',
      component: () => import(/* webpackChunkName: "apilist" */'@/views/ApiList/index.vue'),
      meta: { title: i18n.t('router.apiTest'), type: 'submenu' },
      children: [{
        path: PATH.API_TEST,
        name: 'apitest',
        component: () => import(/* webpackChunkName: "apilist" */'@/views/TestApi/index.vue'),
        meta: { title: i18n.t('router.apiTest'), type: 'submenu', activeMenu: true }
      }]
    }]
  },
  {
    path: PATH.SUMMARY,
    component: Layout,
    meta: { title: i18n.t('router.projectSummary'), type: 'submenu' },
    children: [{
      path: PATH.SUMMARY,
      name: 'summary',
      component: () => import(/* webpackChunkName: "apilist" */'@/views/ApiList/index.vue'),
      meta: { title: i18n.t('router.projectSummary'), type: 'submenu', activeMenu: true },
      children: [{
        path: PATH.SUMMARY,
        name: 'summary',
        component: () => import(/* webpackChunkName: "apilistSummary" */'@/views/Summary/index.vue'),
        meta: { title: i18n.t('router.projectSummary'), type: 'submenu', activeMenu: true }
      }]
    }]
  }
]

let router404 = [
  {
    path: PATH.PAGE_404,
    component: () => import(/* webpackChunkName: "errorpage404" */'@/views/ErrorPage/404'),
    meta: { title: '404', type: 'menu' },
    hidden: true
  },
  { path: '/:pathMatch(.*)*', redirect: PATH.PAGE_404, meta: { title: '', type: 'menu' } }
]

let routesList = [
  {
    path: PATH.HOME,
    component: Layout,
    meta: { title: i18n.t('router.home'), type: 'submenu' },
    children: [{
      path: PATH.HOME,
      name: 'main',
      component: () => import(/* webpackChunkName: "projectlist" */'@/views/ProjectList/index.vue'),
      meta: { title: i18n.t('router.home'), type: 'menu' }
    }]
  }, {
    path: PATH.ACCOUNT,
    component: Layout,
    meta: { title: i18n.t('router.personalCenter'), type: 'menu' },
    hidden: true,
    children: [{
      path: PATH.ACCOUNT,
      name: 'account',
      component: () => import(/* webpackChunkName: "account" */'@/views/Account/index.vue'),
      meta: { title: i18n.t('router.personalCenter'), type: 'menu' }
    }]
  }, {
    path: PATH.SHARE_DOC,
    component: Layout,
    meta: { title: 'API doc', type: 'menu' },
    hidden: true,
    children: [{
      path: PATH.SHARE_DOC,
      name: 'sharedoc',
      component: () => import(/* webpackChunkName: "apilist" */'@/views/ShareDocDetail'),
      meta: { title: i18n.t('router.shareDocument'), type: 'menu' }
    }]
  }, {
    path: PATH.SHARE_INDEX,
    component: Layout,
    meta: { title: i18n.t('router.shareCollection'), type: 'menu' },
    hidden: true,
    children: [{
      path: PATH.SHARE_INDEX,
      name: 'shareindex',
      component: () => import(/* webpackChunkName: "shareindex" */'@/views/ShareIndex/index.vue'),
      meta: { title: i18n.t('router.shareCollection'), type: 'menu' }
    }]
  },
  {
    path: PATH.SHARE_OUTER_INDEX,
    meta: { title: i18n.t('router.shareCollection'), type: 'menu' },
    hidden: true,
    children: [{
      path: PATH.SHARE_OUTER_INDEX,
      name: 'outershare',
      component: () => import(/* webpackChunkName: "shareindex" */'@/views/ShareIndex/index.vue'),
      meta: { title: i18n.t('router.shareCollection'), type: 'menu' }
    }]
  },
  {
    path: PATH.IMPORT_PDF,
    meta: { title: "导出PDF", type: 'menu' },
    hidden: true,
    component: Layout,
    children: [{
      path: PATH.IMPORT_PDF,
      name: 'importpdf',
      component: () => import(/* webpackChunkName: "importPdf" */'@/views/ImportPdf/index.vue'),
      meta: { title: "导出PDF", type: 'menu' }
    }]
  },
  ...secRoutes,
  ...router404
]

export const routes = isExtranet ? [
  {
    path: PATH.SHARE_OUTER_INDEX,
    component: Outer,
    meta: { title: i18n.t('router.shareCollection'), type: 'menu' },
    hidden: true,
    children: [{
      path: PATH.SHARE_OUTER_INDEX,
      name: 'outershare',
      component: () => import(/* webpackChunkName: "shareindex" */'@/views/ShareIndex/index.vue'),
      meta: { title: i18n.t('router.shareCollection'), type: 'menu' }
    }]
  },
  ...router404
] : routesList

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
