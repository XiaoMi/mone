<script lang="ts" setup>
import { computed, onMounted, onUnmounted, onUpdated, ref, reactive, watch } from 'vue'
// @ts-ignore
import MarkdownIt from 'markdown-it'
import mdKatex from '@traptitech/markdown-it-katex'
// @ts-ignore
import mila from 'markdown-it-link-attributes'
import hljs from 'highlight.js'
import { copyToClip } from '@/utils/copy'
import { t } from '@/locales'
import Loading from './Loading.vue'
import { ElMessage } from 'element-plus'
// import BaseSounds from '../../BaseSounds'
// import ChartComponent from './Chart.vue'
import TableComponent from './TableData.vue'
import ButtonComponent from './ButtonData.vue'
import { markdownItMcp } from '../markdown-it-plugin/mcp.ts'
interface Props {
  inversion?: boolean
  error?: boolean
  text?: string
  loading?: boolean
  asRawText?: boolean
  showCursor?: boolean
  textType?: string // 文本类型如果是translate怎展示 描述和翻译结果
  translateData?: {}
  showOperate?: boolean // 是否是插件调试页面
  voiceSetting?: object // 语音播报配置
}

const props = defineProps<Props>()

const textRef = ref<HTMLElement>()

const soundRef = ref<HTMLElement>()

const resContent = ref<HTMLElement>()

const pos = reactive({ x: 0, y: 0 })

const mdi = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(code, language) {
    if (language == 'mermaid') {
      return code
    }
    const validLang = !!(language && hljs.getLanguage(language))
    if (validLang) {
      const lang = language ?? ''
      return highlightBlock(hljs.highlight(code, { language: lang }).value, lang)
    }
    return highlightBlock(hljs.highlightAuto(code).value, '')
  }
})

mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } })
mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' })
mdi.use(markdownItMcp)
const wrapClass = computed(() => {
  return [
    'text-wrap',
    'min-w-[20px]',
    'rounded-md',
    'px-3 py-2',
    props.inversion ? 'color-[#fff]' : 'dark:color-[#fff]',
    props.inversion ? 'bg-[#409eff]' : 'bg-[#f4f6f8]',
    props.inversion ? 'dark:bg-[#80d4ff]' : 'dark:bg-[#1e1e20]',
    props.inversion ? 'message-request' : 'message-reply',
    { 'text-red-500': props.error }
  ]
})

const text = computed(() => {
  const value = props.text ?? ''
  if (!props.asRawText) return mdi.render(value)
  return value
})

function highlightBlock(str: string, lang?: string) {
  return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span><span class="code-block-header__copy">${t(
    'chat.copyCode'
  )}</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`
}

function addCopyEvents() {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll('.code-block-header__copy')
    copyBtn.forEach((btn) => {
      btn.addEventListener('click', () => {
        const code = btn.parentElement?.nextElementSibling?.textContent
        if (code) {
          copyToClip(code).then(() => {
            btn.textContent = t('common.copySuccess')
            setTimeout(() => {
              btn.textContent = t('chat.copyCode')
            }, 1000)
          })
        }
      })
    })
  }
}

function removeCopyEvents() {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll('.code-block-header__copy')
    copyBtn.forEach((btn) => {
      btn.removeEventListener('click', () => {})
    })
  }
}

const newTextDom = ref()
const emits = defineEmits(['replaceFn', 'onTryAgain'])
// 复制到剪切板
function clickToClip() {
  copyToClip(props?.translateData?.toNewText || '')
  ElMessage.success(t('common.copySuccess'))
}
function copyText(text) {
  copyToClip(text)
  ElMessage.success(t('common.copySuccess'))
}
function replaceFn() {
  emits('replaceFn', props?.translateData?.toNewText || '')
}

function getLastTextNode(dom: HTMLElement | ChildNode) {
  const childNodes = dom.childNodes
  for (let i = childNodes.length - 1; i >= 0; i--) {
    const node = childNodes[i]
    if (node.nodeType === Node.TEXT_NODE && /\S/.test(node.nodeValue)) {
      node.nodeValue = node.nodeValue?.replace(/\s+$/, '')
      return node
    } else if (node.nodeType === Node.ELEMENT_NODE) {
      const lastTextNode = getLastTextNode(node)
      if (lastTextNode) {
        return lastTextNode
      }
    }
  }
  return null
}

function updateCursor() {
  const dom = resContent.value
  if (dom && dom.childNodes[1]) {
    const contentDom = dom.childNodes[1] as HTMLElement
    const lastTextNode = getLastTextNode(contentDom)
    // console.log(lastTextNode)
    const textNode = document.createTextNode('\u200b')
    if (lastTextNode) {
      lastTextNode.parentElement!.appendChild(textNode)
    } else {
      contentDom.appendChild(textNode)
    }
    const range = document.createRange()
    range.setStart(textNode, 0)
    range.setEnd(textNode, 0)
    const domRect = contentDom.getBoundingClientRect()
    const rect = range.getBoundingClientRect()
    pos.x = rect.left - domRect.left
    pos.y = rect.top - domRect.top
    textNode.remove()
  }
}

function tryAgainFn() {
  soundRef.value?.stopRead?.()
  emits('onTryAgain')
}

function addZoomControls(mermaidContainer) {
  const outerWrapper = document.createElement('div')
  outerWrapper.className = 'mermaid-outer-wrapper'

  const dragContainer = document.createElement('div')
  dragContainer.className = 'drag-container'

  const parent = mermaidContainer.parentNode

  // 状态管理
  let isDragging = false
  let startX = 0
  let startY = 0
  let translateX = 0
  let translateY = 0
  let currentScale = 1

  // 更新变换
  const updateTransform = (svg) => {
    svg.style.transform = `scale(${currentScale}) translate(${translateX}px, ${translateY}px)`
  }

  // 处理拖拽
  const handleDragStart = (e) => {
    if (e.type.startsWith('mouse') && e.button !== 0) return
    isDragging = true
    dragContainer.style.cursor = 'grabbing'

    const point = e.type.startsWith('mouse') ? e : e.touches[0]
    startX = point.pageX - translateX * currentScale
    startY = point.pageY - translateY * currentScale

    e.preventDefault()
  }

  const handleDragMove = (e) => {
    if (!isDragging) return

    const point = e.type.startsWith('mouse') ? e : e.touches[0]
    translateX = (point.pageX - startX) / currentScale
    translateY = (point.pageY - startY) / currentScale

    const svg = dragContainer.querySelector('svg')
    updateTransform(svg)

    e.preventDefault()
  }

  const handleDragEnd = () => {
    isDragging = false
    dragContainer.style.cursor = 'grab'
  }

  // 处理缩放
  let initialDistance = 0
  let initialScale = 1

  const getDistance = (touches) => {
    return Math.hypot(touches[0].pageX - touches[1].pageX, touches[0].pageY - touches[1].pageY)
  }

  const handlePinchStart = (e) => {
    if (e.touches.length !== 2) return
    initialDistance = getDistance(e.touches)
    initialScale = currentScale
    e.preventDefault()
  }

  const handlePinchMove = (e) => {
    if (e.touches.length !== 2) return

    const distance = getDistance(e.touches)
    const scale = Math.min(Math.max(initialScale * (distance / initialDistance), 0.5), 3)

    // 保持缩放中心点
    const svg = dragContainer.querySelector('svg')
    const rect = svg.getBoundingClientRect()
    const centerX = (e.touches[0].pageX + e.touches[1].pageX) / 2 - rect.left
    const centerY = (e.touches[0].pageY + e.touches[1].pageY) / 2 - rect.top

    const factor = scale / currentScale
    translateX = centerX / scale - centerX / currentScale + translateX
    translateY = centerY / scale - centerY / currentScale + translateY

    currentScale = scale
    updateTransform(svg)

    e.preventDefault()
  }

  // 添加缩放按钮的点击处理
  const handleZoom = (factor) => {
    const svg = dragContainer.querySelector('svg')
    const rect = svg.getBoundingClientRect()
    const centerX = rect.width / 2
    const centerY = rect.height / 2

    const newScale = Math.min(Math.max(currentScale * factor, 0.5), 3)
    const scaleFactor = newScale / currentScale

    translateX = centerX / newScale - centerX / currentScale + translateX
    translateY = centerY / newScale - centerY / currentScale + translateY

    currentScale = newScale
    updateTransform(svg)
  }

  // 全屏处理函数
  const handleFullscreen = async (container) => {
    try {
      if (!document.fullscreenElement) {
        // 进入全屏前重置变换
        const svg = container.querySelector('svg')
        currentScale = 1
        translateX = 0
        translateY = 0
        updateTransform(svg)

        await container.requestFullscreen()
        container.classList.add('is-fullscreen')
      } else {
        await document.exitFullscreen()
        container.classList.remove('is-fullscreen')

        // 退出全屏后重置变换
        const svg = container.querySelector('svg')
        currentScale = 1
        translateX = 0
        translateY = 0
        updateTransform(svg)
      }
    } catch (err) {
      console.error('全屏切换失败:', err)
    }
  }

  // 监听全屏变化
  document.addEventListener('fullscreenchange', () => {
    const container = outerWrapper
    const fullscreenBtn = container.querySelector('.fullscreen-btn')
    if (document.fullscreenElement === container) {
      fullscreenBtn.classList.add('is-fullscreen')
    } else {
      fullscreenBtn.classList.remove('is-fullscreen')
    }
  })

  const controls = document.createElement('div')
  controls.className = 'mermaid-zoom-control'
  controls.innerHTML = `
    <div class="zoom-buttons">
      <div class="button-group">
        <button class="zoom-btn" title="放大">
          <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>
          <span class="tooltip">放大</span>
        </button>
        <button class="zoom-btn" title="缩小">
          <svg viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M19 13H5v-2h14v2z"/></svg>
          <span class="tooltip">缩小</span>
        </button>
        <button class="zoom-btn" title="重置">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path fill="currentColor" d="M12 20c-4.4 0-8-3.6-8-8s3.6-8 8-8s8 3.6 8 8s-3.6 8-8 8m0-18C6.5 2 2 6.5 2 12s4.5 10 10 10s10-4.5 10-10S17.5 2 12 2m5 11l-4-4v3H7v2h6v3l4-4z"/>
          </svg>
          <span class="tooltip">重置</span>
        </button>
      </div>
      <div class="divider"></div>
      <button class="fullscreen-btn" title="全屏">
        <svg class="fullscreen-icon" viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
        </svg>
        <span class="tooltip">全屏</span>
      </button>
    </div>
  `

  // 绑定全屏按钮事件
  const fullscreenBtn = controls.querySelector('.fullscreen-btn')
  fullscreenBtn.addEventListener('click', () => handleFullscreen(outerWrapper))

  // 绑定缩放按钮事件
  const zoomButtons = controls.querySelectorAll('.zoom-btn')
  zoomButtons[0].addEventListener('click', () => handleZoom(1.2))
  zoomButtons[1].addEventListener('click', () => handleZoom(1 / 1.2))
  zoomButtons[2].addEventListener('click', () => {
    currentScale = 1
    translateX = 0
    translateY = 0
    updateTransform(dragContainer.querySelector('svg'))
  })

  // 添加事件监听
  dragContainer.addEventListener('mousedown', handleDragStart)
  dragContainer.addEventListener('touchstart', handleDragStart, { passive: false })

  document.addEventListener('mousemove', handleDragMove)
  document.addEventListener('touchmove', handleDragMove, { passive: false })

  document.addEventListener('mouseup', handleDragEnd)
  document.addEventListener('touchend', handleDragEnd)

  dragContainer.addEventListener('touchstart', handlePinchStart, { passive: false })
  dragContainer.addEventListener('touchmove', handlePinchMove, { passive: false })

  // 防止触摸时的默认行为
  dragContainer.addEventListener('touchstart', (e) => e.preventDefault(), { passive: false })

  // 清理函数
  const cleanup = () => {
    dragContainer.removeEventListener('mousedown', handleDragStart)
    dragContainer.removeEventListener('touchstart', handleDragStart)
    document.removeEventListener('mousemove', handleDragMove)
    document.removeEventListener('touchmove', handleDragMove)
    document.removeEventListener('mouseup', handleDragEnd)
    document.removeEventListener('touchend', handleDragEnd)
    dragContainer.removeEventListener('touchstart', handlePinchStart)
    dragContainer.removeEventListener('touchmove', handlePinchMove)
  }

  // 在组件卸载时清理事件监听
  outerWrapper.cleanup = cleanup

  // 将原始的 mermaid 容器移到拖拽容器中
  dragContainer.appendChild(mermaidContainer)
  outerWrapper.appendChild(dragContainer)
  outerWrapper.appendChild(controls)
  parent.insertBefore(outerWrapper, null)
}

// 添加全屏切换函数到 window 对象
window.toggleFullScreen = function (element) {
  if (!document.fullscreenElement) {
    element.requestFullscreen().catch((err) => {
      console.error(`Error attempting to enable full-screen mode: ${err.message}`)
    })
  } else {
    document.exitFullscreen()
  }
}

// 添加全屏变化事件监听
document.addEventListener('fullscreenchange', () => {
  const fullscreenBtns = document.querySelectorAll('.fullscreen-btn')
  fullscreenBtns.forEach((btn) => {
    if (document.fullscreenElement) {
      btn.classList.add('is-fullscreen')
    } else {
      btn.classList.remove('is-fullscreen')
    }
  })
})

let timer: number | null | undefined = null
watch(
  () => props.text,
  () => {
    timer && clearTimeout(timer)
    timer = setTimeout(() => {
      if (document.querySelectorAll('.language-mermaid')?.length) {
        try {
          mermaid.initialize({
            startOnLoad: false,
            zoom: {
              enabled: true,
              maxZoom: 10,
              minZoom: 0.1
            }
          })

          mermaid.init(
            {
              zoom: true
            },
            document.querySelectorAll('.language-mermaid'),
            (id) => {
              console.log('Mermaid chart initialized:', id)
              const container = document.getElementById(id)
              if (container) {
                addZoomControls(container)
              }
            },
            (id, error) => {
              console.error('Error initializing chart:', id, error)
            }
          )
          timer && clearTimeout(timer)
        } catch (error) {
          console.log(error)
        }
      }
    }, 500)
  },
  {
    deep: true,
    immediate: true
  }
)
onMounted(() => {
  mermaid.initialize({ startOnLoad: false })
  updateCursor()
  addCopyEvents()
})

onUpdated(() => {
  updateCursor()
  addCopyEvents()
})

onUnmounted(() => {
  removeCopyEvents()
})
</script>

<template>
  <div :class="wrapClass">
    <div ref="textRef" class="leading-relaxed break-words">
      <template v-if="loading">
        <Loading />
      </template>
      <template v-else>
        <div v-if="props.textType == 'translate'">
          <p class="text-t">{{ translateData?.aiDetail }}</p>
          <div class="text-detail" ref="newTextDom">
            <span class="new-text">
              {{ translateData?.toNewText }}
            </span>
            <div class="btn-box">
              <span class="copy-btn" @click="replaceFn"> {{ t('chat.replace') }} </span>
              <span class="copy-btn" @click="clickToClip"> {{ t('common.copy') }} </span>
            </div>
          </div>
        </div>
        <template v-else-if="props.textType == 'img'">
          <div style="max-width: 200px; max-height: 200px">
            <el-image :src="props.text" fit="contain" />
          </div>
        </template>
        <!-- <ChartComponent
          v-else-if="props.textType?.includes('chart')"
          :chartType="props.textType"
          :data="props.text"
        ></ChartComponent> -->
        <TableComponent v-else-if="props.textType == 'table'" :data="props.text"></TableComponent>
        <ButtonComponent
          v-else-if="props.textType == 'button'"
          :data="props.text"
          :asRawText="props.asRawText"
        ></ButtonComponent>
        <template v-else>
          <div v-if="!inversion" ref="resContent">
            <div v-if="showCursor" class="show-cursor"></div>
            <div v-if="!asRawText" class="markdown-body markdown-body-table" v-html="text" />
            <div v-else class="whitespace-pre-wrap" v-text="text" />
          </div>
          <div v-else class="whitespace-pre-wrap" v-text="text" />
          <div v-if="props.showOperate && props.asRawText && !inversion" class="debug-operate">
            <el-tooltip content="复制">
              <el-button class="sounds-icon" @click="copyText(text)" text>
                <el-icon :size="16"><CopyDocument /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="再试一次">
              <el-button class="sounds-icon" @click="tryAgainFn" text>
                <el-icon :size="16"><RefreshRight /></el-icon>
              </el-button>
            </el-tooltip>
            <!-- <BaseSounds
              ref="soundRef"
              v-if="props.voiceSetting?.open"
              :content="text"
              :language="props.voiceSetting.language"
            /> -->
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<style lang="scss">
@keyframes blink {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

.show-cursor {
  position: relative;

  &::after {
    content: '';
    width: 8px;
    height: 1.5em;
    position: absolute;
    background: #d5d9da;
    animation: blink 0.6s infinite;
    left: calc(v-bind('pos.x') * 1px);
    top: calc(v-bind('pos.y') * 1px);
  }
}
.debug-operate {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: #626060;
  &:deep(button) {
    margin-left: 0;
    padding-bottom: 0;
  }
}
.markdown-body-table {
  &.markdown-body table th,
  &.markdown-body table td {
    white-space: nowrap;
  }
}

.mermaid-outer-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: relative;

  &.is-fullscreen {
    background: white;
    padding: 20px;
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;

    .drag-container {
      flex: 1;
      width: 100%;
      height: calc(100% - 40px); // 减去控制按钮的高度
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;

      .language-mermaid {
        width: auto;
        height: auto;
        max-width: 100%;
        max-height: 100%;

        svg {
          max-width: 100%;
          max-height: 100%;
          width: auto;
          height: auto;
        }
      }
    }

    .mermaid-zoom-control {
      margin-top: 10px;
      position: fixed;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  .language-mermaid {
    width: 100%;
    position: relative;

    svg {
      cursor: grab;
      display: block;
      margin: 0 auto;

      &:active {
        cursor: grabbing;
      }
    }
  }

  .mermaid-zoom-control {
    margin-top: 4px;
    line-height: 0;
    font-size: 0;
    z-index: 100;

    .zoom-buttons {
      display: inline-flex;
      align-items: center;
      background: #ffffff;
      padding: 2px;
      border-radius: 6px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      border: 1px solid rgba(0, 0, 0, 0.08);
      backdrop-filter: blur(8px);
      vertical-align: top;

      .button-group {
        display: flex;
        gap: 1px;
        background: #f5f5f5;
        padding: 1px;
        border-radius: 4px;
        line-height: 0;
      }

      .divider {
        width: 1px;
        height: 14px;
        background: #e0e0e0;
        margin: 0 2px;
        align-self: center;
      }

      button {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        min-width: 24px;
        min-height: 24px;
        border: none;
        background: transparent;
        color: #666;
        cursor: pointer;
        border-radius: 3px;
        transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
        padding: 0;
        margin: 0;
        vertical-align: top;
        position: relative;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background-color: currentColor;
          opacity: 0;
          transition: opacity 0.2s ease;
        }

        &:hover {
          color: #1890ff;
          transform: translateY(-1px);
          &::before {
            opacity: 0.1;
          }
        }

        &:active {
          transform: translateY(0);
          &::before {
            opacity: 0.2;
          }
        }

        svg {
          width: 13px;
          height: 13px;
          transition: transform 0.2s ease;
          display: block;
          position: relative;
          z-index: 1;
        }

        &.fullscreen-btn {
          &:hover .fullscreen-icon {
            transform: scale(1.1);
          }

          &:active .fullscreen-icon {
            transform: scale(0.95);
          }
        }

        .tooltip {
          position: absolute;
          left: 50%;
          transform: translateX(-50%);
          background: rgba(0, 0, 0, 0.75);
          color: white;
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 12px;
          line-height: 1.2;
          white-space: nowrap;
          opacity: 0;
          visibility: hidden;
          transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
          pointer-events: none;
          z-index: 1000;
          top: -6px;

          &::before {
            content: '';
            position: absolute;
            left: 50%;
            transform: translateX(-50%);
            border-left: 4px solid transparent;
            border-right: 4px solid transparent;
            border-top: 4px solid rgba(0, 0, 0, 0.75);
            bottom: -4px;
          }
        }

        &:hover .tooltip {
          opacity: 1;
          visibility: visible;
          top: -28px;
          transform: translateX(-50%);
        }

        @for $i from 1 through 4 {
          &:nth-child(#{$i}) .tooltip {
            transition-delay: #{$i * 0.05}s;
          }
        }
      }

      .button-group button {
        background: #fff;

        &:hover {
          background: #f0f7ff;
          color: #1890ff;
        }

        &:active {
          background: #e6f4ff;
        }
      }
    }
  }

  .drag-container {
    width: 100%;
    overflow: hidden; // 改为 hidden 以防止滚动条
    cursor: grab;
    position: relative;
    touch-action: none; // 禁用默认触摸行为

    &:active {
      cursor: grabbing;
    }

    .language-mermaid {
      min-width: 100%;
      display: inline-block;

      svg {
        display: block;
        transform-origin: center center; // 确保缩放以中心点为基准
        transition: transform 0.1s ease; // 添加平滑过渡
        will-change: transform; // 优化性能
      }
    }
  }

  // 全屏模式下的拖拽容器样式
  &:fullscreen .drag-container {
    height: 90vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

// Webkit 全屏支持
.mermaid-outer-wrapper:-webkit-full-screen {
  background: white;
  padding: 20px;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>

<style lang="scss" scoped>
@import url(./style.scss);
.text-detail {
  margin: 5px 0;
  background: #e5e5e5;
  padding: 20px 10px 10px 10px;
  position: relative;
  border-radius: 0.375rem;
  .btn-box {
    position: absolute;
    right: 4px;
    top: 2px;
    font-size: 12px;
  }
  .copy-btn {
    cursor: pointer;
    color: #00a9ff;
  }
}
</style>
