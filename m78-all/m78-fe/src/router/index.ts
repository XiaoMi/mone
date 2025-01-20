import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/layouts/index.vue'
import ProbotLayoutBg from '@/components/layouts/ProbotLayoutBg.vue'
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
          path: '/code-line-count',
          name: 'Code Line Count',
          component: () => import('@/views/code-line-count/index.vue')
        },
        {
          path: '/doc',
          name: 'AI Document',
          component: () => import('@/views/doc/index.vue')
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
          path: '/word',
          name: 'AI Word',
          component: () => import('@/views/word/index.vue')
        },
        {
          path: '/ws/test',
          name: 'AI Test',
          component: () => import('@/views/websocket/index.vue')
        },
        {
          path: '/',
          name: 'ProbotLayoutBg',
          component: ProbotLayoutBg,
          children: [
            {
              path: '/',
              name: 'ProbotLayout',
              component: ProbotLayout,
              children: [
                {
                  path: '/',
                  name: 'AI Probot',
                  component: () => import('@/views/probot-index/index.vue')
                },
                {
                  path: '/index',
                  name: 'AI Probot Index',
                  component: () => import('@/views/probot-index/index.vue')
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
                  path: '/probot-team',
                  name: 'AI Probot Team',
                  component: () => import('@/views/probot-team/index.vue')
                },
                {
                  path: '/probot-super-team',
                  name: 'AI Probot Super Team',
                  component: () => import('@/views/probot-super-team/index.vue')
                },
                {
                  path: '/probot-carousel',
                  name: 'AI Probot Carousel',
                  component: () => import('@/views/probot-carousel/index.vue')
                },
                {
                  path: '/probot-platformStatistics',
                  name: 'AI Probot Platform Statistics',
                  component: () => import('@/views/probot-platformStatistics/index.vue')
                },
                {
                  path: '/git-account',
                  name: 'AI Probot GitAccount',
                  component: () => import('@/views/git-account/index.vue')
                },
                {
                  path: '/employee-manage',
                  name: 'AI Probot EmployeeManage',
                  component: () => import('@/views/employee-manage/index.vue')
                },
                {
                  path: '/probot-space/:id?',
                  name: 'AI Probot Space',
                  component: () => import('@/views/probot-space/index.vue')
                },
                {
                  path: '/probot-my-collect/:id?',
                  name: 'AI Probot My Collect',
                  component: () => import('@/views/probot-space/index.vue')
                },
                {
                  path: '/probot-classification',
                  name: 'AI Probot Classification',
                  component: () => import('@/views/probot-classification/index.vue')
                },
                {
                  path: '/probot-comp-list/:space/:plugin',
                  name: 'AI Probot Comp List',
                  component: () => import('@/views/probot-space/plugins/list/index.vue')
                },
                {
                  path: '/probot-knowledge-manage/:id/:knowledgeBaseId',
                  name: 'AI Probot Knowledge Manage',
                  component: () => import('@/views/probot-space/knowledge/manage/index.vue')
                },
                {
                  path: '/probot-card',
                  name: 'AI Probot Card',
                  component: () => import('@/views/probot-space/card/index.vue')
                },
                {
                  path: '/project-priority-config',
                  name: 'AI Probot ProjectPriorityConfig',
                  component: () => import('@/views/project-priority-config/index.vue')
                }
              ]
            },
            {
              path: '/probot-log',
              name: 'AI Probot Log',
              component: () => import('@/views/probot-log/index.vue')
            },
            {
              path: '/probot-api',
              name: 'AI Probot Api',
              component: () => import('@/views/probot-api/index.vue')
            },
            {
              path: '/probot-edit/:id?',
              name: 'AI Probot Edit',
              component: () => import('@/views/probot-create/index.vue')
            },
            {
              path: '/probot-view/:id?',
              name: 'AI Probot View',
              component: () => import('@/views/probot-create/index.vue')
            },
            {
              path: '/probot-mode-voice',
              name: 'AI Probot Mode Voice',
              component: () => import('@/components/layouts/mode/VoiceMenu.vue'),
              children: [
                {
                  path: '/probot-mode-voice',
                  name: 'AI Probot Mode Voice Index',
                  component: () => import('@/views/probot-mode/voice/index.vue')
                },
                {
                  path: '/probot-mode-voice/audio',
                  name: 'AI Probot Mode Voice Audio',
                  component: () => import('@/views/probot-mode/voice/Audio.vue')
                },
                {
                  path: '/probot-mode-voice/sound-copy',
                  name: 'AI Probot Mode Voice SoundCopy',
                  component: () => import('@/views/probot-mode/voice/SoundCopy.vue')
                }
              ]
            },
            {
              path: '/probot-mode-image',
              name: 'AI Probot Mode Image',
              component: () => import('@/components/layouts/mode/ImgMenu.vue'),
              children: [
                {
                  path: '/probot-mode-image',
                  name: 'AI Probot Mode Image Index',
                  component: () => import('@/views/probot-mode/image/index.vue')
                },
                {
                  path: '/probot-mode-image/text-to-images',
                  name: 'AI Probot Mode Image TextToImages',
                  component: () => import('@/views/probot-mode/image/TextToImages.vue')
                },
                {
                  path: '/probot-mode-image/office-chart',
                  name: 'AI Probot Mode Image OfficeChart',
                  component: () => import('@/views/probot-mode/image/OfficeChart.vue')
                },
                {
                  path: '/probot-mode-image/graffiti',
                  name: 'AI Probot Mode Image Graffiti',
                  component: () => import('@/views/probot-mode/image/Graffiti.vue')
                },
                {
                  path: '/probot-mode-image/background',
                  name: 'AI Probot Mode Image Background',
                  component: () => import('@/views/probot-mode/image/Background.vue')
                },
                {
                  path: '/probot-mode-image/redraw',
                  name: 'AI Probot Mode Image Redraw',
                  component: () => import('@/views/probot-mode/image/Redraw.vue')
                },
                {
                  path: '/probot-mode-image/image-text',
                  name: 'AI Probot Mode Image ImageAndText',
                  component: () => import('@/views/probot-mode/image/ImageAndText.vue')
                },
                {
                  path: '/probot-mode-image/word-art',
                  name: 'AI Probot Mode Image WordArt',
                  component: () => import('@/views/probot-mode/image/WordArt.vue')
                },
                {
                  path: '/probot-mode-image/extend',
                  name: 'AI Probot Mode Image Extend',
                  component: () => import('@/views/probot-mode/image/Extend.vue')
                }
              ]
            }
          ]
        }
      ]
    },
    {
      path: '/probot-knowledge-new/:id?',
      name: 'AI Probot Knowledge New',
      component: () => import('@/views/probot-space/knowledge/index.vue')
    },
    {
      path: '/probot-knowledge-manage-new/:id/:knowledgeBaseId',
      name: 'AI Probot Knowledge Manage New',
      component: () => import('@/views/probot-space/knowledge/manage/index.vue')
    },
    {
      path: '/probot-layout-visit',
      name: 'ProbotLayoutVisit',
      component: ProbotLayoutBg,
      children: [
        {
          path: '/probot-visit/:id',
          name: 'AI Probot Visit',
          component: () => import('@/views/probot-visit/index.vue')
        },
        {
          path: '/plugin-detail',
          name: 'AI Probot Plugin Detail',
          component: () => import('@/views/plugin-detail/index.vue')
        },
        {
          path: '/probot-card-manage/:workSpaceId/:cardId',
          name: 'AI Probot Card Manage',
          component: () => import('@/views/probot-space/card/manage/index.vue')
        }
      ]
    },
    {
      path: '/probot/workflow/:id',
      name: 'AI Probot workflowItem',
      component: () => import('@/views/workflow/index.vue')
    },
    {
      path: '/:catchAll(.*)', // 不识别的path自动匹配到首页
      redirect: '/'
    }
  ]
})

export default router
