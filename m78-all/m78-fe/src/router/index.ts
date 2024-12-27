import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/layouts/index.vue'
import ProbotLayout from '@/components/layouts/ProbotLayout.vue'

const router = createRouter({
  history: createWebHistory(`${import.meta.env.VITE_APP_ROUTER_PATH}`),
  routes: [
    {
      path: '/',
      name: 'Root',
      component: Layout,
      children: [
        {
          path: '/',
          name: 'HomeView',
          component: () => import('@/views/home/index.vue')
        },
        {
          path: '/code',
          name: 'AI Code',
          component: () => import('@/views/code/index.vue')
        },
        {
          path: '/doc',
          name: 'AI Document',
          component: () => import('@/views/doc/index.vue')
        },
        {
          path: '/translate',
          name: 'AI Translate',
          component: () => import('@/views/translate/index.vue')
        },
        {
          path: '/chat',
          name: 'AI Chat',
          component: () => import('@/views/chat/layout/Layout.vue')
        },
        {
          path: '/data-source',
          name: 'AI Datasource',
          component: () => import('@/views/data-source/index.vue')
        },
        {
          path: '/agent',
          name: 'AI Agent',
          component: () => import('@/views/agent/index.vue')
        },
        {
          path: '/about',
          name: 'about',
          component: () => import('@/views/about/index.vue')
        },
        {
          path: '/manual/:id?',
          name: 'manual',
          component: () => import('@/views/manual/index.vue')
        },
        {
          path: '/word',
          name: 'AI Word',
          component: () => import('@/views/word/index.vue')
        },
        {
          path: '/ws/test',
          name: 'AI Word',
          component: () => import('@/views/websocket/index.vue')
        },
        {
          path: '/probot-layout',
          name: 'ProbotLayout',
          component: ProbotLayout,
          children: [
            {
              path: '/probot',
              name: 'AI Probot',
              component: () => import('@/views/probot/index.vue')
            },
            {
              path: '/probot-index',
              name: 'AI Probot Index',
              component: () => import('@/views/probot/index.vue')
            },
            {
              path: '/probot-list',
              name: 'AI Probot List',
              component: () => import('@/views/probot-list/index.vue')
            },
            {
              path: '/probot-plugin-list',
              name: 'AI Probot Plugin List',
              component: () => import('@/views/plugins-list/index.vue')
            },
            {
              path: '/probot-create',
              name: 'AI Probot Create',
              component: () => import('@/views/probot/create/index.vue')
            },
            {
              path: '/probot-edit/:id?',
              name: 'AI Probot Edit',
              component: () => import('@/views/probot/create/index.vue')
            },
            {
              path: '/probot-view/:id?',
              name: 'AI Probot View',
              component: () => import('@/views/probot/create/index.vue')
            },
            {
              path: '/probot-space/:id?',
              name: 'AI Probot Space',
              component: () => import('@/views/probot/my-space/index.vue')
            },
            {
              path: '/probot-my-collect/:id?',
              name: 'AI Probot My Collect',
              component: () => import('@/views/probot/my-space/index.vue')
            },
            {
              path: '/probot-comp-list/:space/:plugin',
              name: 'AI Probot Comp List',
              component: () => import('@/views/probot/my-space/plugins/list/index.vue')
            },
            {
              path: '/probot-knowledge-manage/:id/:knowledgeBaseId',
              name: 'AI Probot Knowledge Manage',
              component: () => import('@/views/probot/my-space/knowledge/manage/index.vue')
            },
            {
              path: '/probot-team',
              name: 'AI Probot Team',
              component: () => import('@/views/probot-team/index.vue')
            },
            {
              path: '/probot-classification',
              name: 'AI Probot Classification',
              component: () => import('@/views/probot-classification/index.vue')
            }
          ]
        }
      ]
    },
    {
      path: '/probot-layout-visit',
      name: 'ProbotLayoutVisit',
      component: ProbotLayout,
      children: [
        {
          path: '/probot-visit/:id',
          name: 'AI Probot Visit',
          component: () => import('@/views/probot/visit/index.vue')
        },
        {
          path: '/plugin-detail',
          name: 'AI Probot Plugin Detail',
          component: () => import('@/views/plugin-detail/index.vue')
        }
      ]
    },
    {
      path: '/probot/workflow/:id',
      name: 'AI Probot workflowItem',
      component: () => import('@/views/workflow/index.vue')
    }
  ]
})

export default router
