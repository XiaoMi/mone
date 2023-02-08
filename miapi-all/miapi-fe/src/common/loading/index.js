import 'nprogress/nprogress.css'
import NProgress from 'nprogress'

export const hideLoading = () => {
  NProgress.done()
}

export const showLoading = () => {
  NProgress.start()
}
