<template>
  <div class="flex flex-1 flex-col overflow-hidden">
    <div class="shrink-0 flex"></div>
    <div class="flex flex-1 overflow-hidden">
      <div class="flex flex-1" ref="container"></div>
      <div class="flex flex-col shrink-0 w-[360px]">
        <div class="flex flex-1 overflow-hidden">
          <div v-if="conversions.length == 0" class="flex flex-1 justify-center"><el-empty /></div>
          <div v-else class="flex-1 p-[8px] bg-[#FFFFFF]">
            <div class="h-full overflow-auto">
              <Message
                v-for="(item, index) of conversions"
                :username="item.name"
                :key="index"
                :date-time="item.dateTime"
                :text="item.text"
                :inversion="item.inversion"
                :error="item.error"
                :loading="item.loading"
                :show-cursor="item.showCursor"
              />
            </div>
          </div>
        </div>
        <div class="shrink-0">
          <CommmonTextarea
            ref="inputRef"
            v-model="text"
            class="flex-1"
            :placeholder="placeholder"
            @enterFn="handleEnter"
            :disabled="loading"
          />
        </div>
      </div>
    </div>
    <div class="shrink-0 flex"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import * as PIXI from 'pixi.js'
import { t } from '@/locales'
import { fetchAgents, communicate } from '@/api/agent'
import { ElMessage } from 'element-plus'
import CommmonTextarea from '@/components/CommmonTextarea.vue'
import { Message } from '@/components/common-message'
import { createCharacter, characterWidth, bindAppEvent, keys } from './mixin/index'

let app: PIXI.Application<PIXI.ICanvas> | null = null
// let dragTarget: PIXI.Container | null = null
// let me: PIXI.Sprite | null = null
const others: PIXI.Container[] = []

const placeholder = computed(() => {
  return t('chat.placeholder')
})

const container = ref<HTMLDivElement | null>()
const conversions = ref<
  {
    name?: string
    text: string
    dateTime: string
    inversion: boolean
    error?: boolean
    loading: boolean
    showCursor?: boolean
  }[]
>([])
const text = ref('')
const username = ref('')
const loading = ref(false)

window.addEventListener('keydown', (e) => {
  keys[e.code] = true
})

window.addEventListener('keyup', (e) => {
  keys[e.code] = false
})

function handleEnter(prompt: string) {
  text.value = prompt
  sendMessage()
}

async function sendMessage() {
  if (loading.value) return
  loading.value = true
  try {
    if (!app!.target) {
      ElMessage.error('选择要发送的对象')
      return
    }
    app!.target.playAnimation('action', true)
    if (text.value && text.value.trim()) {
      const prompt = text.value
      text.value = ''
      conversions.value.push({
        text: prompt,
        inversion: true,
        loading: false,
        dateTime: new Date().toLocaleString()
      })
      const { code, message, data } = await communicate(username.value, {
        message: prompt,
        topicId: app!.target.uuid
      })
      if (code == 0) {
        conversions.value.push({
          name: username.value,
          text: data,
          inversion: false,
          loading: false,
          dateTime: new Date().toLocaleString()
        })
      } else {
        ElMessage.error(message || '出错了')
      }
    }
  } finally {
    app!.target?.playAnimation('action', false)
    loading.value = false
  }
}

function bindClick(name: string, me: PIXI.Container) {
  return function onClick() {
    username.value = name
    if (app!.target) {
      app!.target.alpha = 0.6
    }
    app!.target = me
    me.alpha = 1
    me.playAnimation('')
    if (text.value && text.value.trim()) {
      sendMessage()
    }
  }
}

async function getAgents(app: PIXI.Application) {
  const { code, data } = await fetchAgents()
  if (code == 0 && Array.isArray(data)) {
    for (let i = 0; i < data.length; i++) {
      const colCount = Math.round(app.screen.width / characterWidth) || 1
      const it = data[i]
      const robot = createCharacter(it.name, colCount)
      bindRobotEvent(robot, it)
      app.stage.addChild(robot)
      others.push(robot)
    }
  }
}

function bindRobotEvent(robot: PIXI.Container<PIXI.DisplayObject>, info: { name: string }) {
  robot.on('click', bindClick(info.name, robot))
}

onMounted(() => {
  const rect = container.value!.getClientRects()
  app = new PIXI.Application({
    background: '#1099bb',
    forceCanvas: false,
    width: rect[0].width,
    height: rect[0].height
  })
  app.view.width = rect[0].width
  app.view.height = rect[0].height
  container.value!.appendChild(app.view)

  getAgents(app)
  bindAppEvent(app)
})

onBeforeUnmount(() => {
  app = null
})
</script>
