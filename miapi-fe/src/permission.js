import router, { routes } from './router'
import store from './store'
import NProgress from 'nprogress'
import { PATH } from '@/router/constant'
import 'nprogress/nprogress.css'
import { API_DETAIL_TAB } from '@/views/ApiList/constant'
import { isExtranet } from "@/utils"

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to, from, next) => {
  // NProgress.start()

  if (to.path !== PATH.ADD_MOCK && to.path !== PATH.API_DETAIL) {
    store.dispatch('apilist/changeApiDetailActiveTab', API_DETAIL_TAB.DETAIL)
  }

  if (store.getters.roles) {
    next()
  } else {
    try {
      store.dispatch('user/getInfo')
      // // const accessRoutes = await store.dispatch('permission/generateRoutes')
      // routes.forEach(v => {
      //   router.addRoute(routes)
      // })
       if (isExtranet) {
        next({ ...to, replace: true })
        return
      } else if (!isExtranet && to.path === PATH.SHARE_OUTER_INDEX) {
        next({ query: to.query, path: PATH.SHARE_INDEX, replace: true })
        return
      }
      // replace 刷新时导致第二级router-view不渲染
      // next({ ...to, replace: true })
      next()
    } catch (error) {
    }
  }
})

// router.afterEach(() => {
//   NProgress.done()
// })
