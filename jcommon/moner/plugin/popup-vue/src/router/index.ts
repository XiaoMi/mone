import { createRouter, createWebHashHistory } from 'vue-router'
import PreviewView from '../views/PreviewView.vue'
import PopupView from '../views/PopupView.vue'
import ConfigView from '../views/ConfigView.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'popup',
      component: PopupView
    },
    {
      path: '/preview',
      name: 'preview',
      component: PreviewView
    },
    {
      path: '/features',
      name: 'features',
      component: () => import('../views/FeaturesView.vue')
    },
    {
      path: '/config',
      name: 'config',
      component: ConfigView
    }
  ],
})

export default router
