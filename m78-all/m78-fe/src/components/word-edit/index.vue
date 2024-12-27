<template>
  <div class="editor-wrap">
    <div ref="editorRef" style="height: 100%">
      <div class="aie-container">
        <strong class="running" v-if="isAiRunning">
          <span>{{
            disconnect
              ? t('word.terminating')
              : streamType === 1
                ? t('word.articleGenerated')
                : t('word.translating')
          }}</span>
          <ul>
            <li>.</li>
            <li>.</li>
            <li>.</li>
          </ul>
        </strong>
        <div class="aie-container-header">
          <ul class="custom-menu" ref="customUlRef" v-click-outside="onClickOutside">
            <li @click="createByAi">{{ t('word.aiGenerated') }}</li>
            <li @click="translateByAi('中文', '英文')">{{ t('word.zhToEn') }}</li>
            <li @click="translateByAi('英文', '中文')">{{ t('word.enToZh') }}</li>
          </ul>
          <section class="btns">
            <el-tooltip effect="dark" :content="t('word.save')" placement="top">
              <el-button class="save-btn" size="small" type="primary" circle>
                <svg
                  t="1708590805534"
                  class="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="5783"
                  width="16"
                  height="16"
                >
                  <path
                    d="M925.248 356.928l-258.176-258.176a64 64 0 0 0-45.248-18.752H144a64 64 0 0 0-64 64v736a64 64 0 0 0 64 64h736a64 64 0 0 0 64-64V402.176a64 64 0 0 0-18.752-45.248zM288 144h192V256H288V144z m448 736H288V736h448v144z m144 0H800V704a32 32 0 0 0-32-32H256a32 32 0 0 0-32 32v176H144v-736H224V288a32 32 0 0 0 32 32h256a32 32 0 0 0 32-32V144h77.824l258.176 258.176V880z"
                    p-id="5784"
                    fill="#ffffff"
                  ></path>
                </svg>
              </el-button>
            </el-tooltip>
          </section>
        </div>
        <div class="aie-container-main">
          <el-input v-model="title" :placeholder="t('word.enterTitle')"></el-input>
        </div>
        <div class="aie-container-footer"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { ClickOutside as vClickOutside, ElMessage, ElMessageBox } from 'element-plus'
// https://aieditor.dev/zh/config/toolbar.html
import { AiEditor, type CustomMenu } from 'aieditor'
import 'aieditor/dist/style.css'
import { toolbarKeys, fontFamily } from './toolbarKeys'
import { useAppStore } from '@/stores'
import { watch } from 'vue'
import drawWaterMark from './waterMark'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import CryptoJS from 'crypto-js'
import { t } from '@/locales'
import { addListener } from './mutationObserver'
const appStore = useAppStore()

const editorRef = ref()
const title = ref()
const customUlRef = ref()
let aiEditor: AiEditor | null = null
const controller = new AbortController()
const uuid = ref('')
const streamType = ref(1)
const isAiRunning = ref(false)
const streamRef = ref()
const disconnect = ref(false)

const createArticl = (editor: AiEditor, body: string, type: number) => {
  if (disconnect.value) {
    ElMessage.warning(t('word.terminating'))
    return
  }
  uuid.value = ''
  streamType.value = type
  disconnect.value = false
  streamRef.value = fetchEventSource(
    `//${window.location.host + import.meta.env.VITE_GLOB_API_NEW_URL}v1/prompt/execute/stream`,
    {
      method: 'POST',
      signal: controller.signal,
      openWhenHidden: true,
      headers: {
        'Content-Type': 'application/json'
      },
      body,
      onmessage(res) {
        console.log(res)
        if (disconnect.value) {
          editor?.blur?.()
          controller.abort()
          isAiRunning.value = false
          disconnect.value = false
          throw '手动终止'
        } else if (res.event === 'start') {
          isAiRunning.value = true
          editor?.clear?.()
          uuid.value = res?.data
          editor.focusStart()
        } else if (res.event === 'end') {
          editor?.blur?.()
          controller.abort()
          isAiRunning.value = false
        } else {
          editor
            ?.focusEnd?.()
            .insert(CryptoJS.enc.Base64.parse(res.data).toString(CryptoJS.enc.Utf8))
        }
      },
      onerror(err) {
        isAiRunning.value = false
        controller.abort()
        editor?.blur?.()
        console.log('服务异常', err)
        throw err
      },
      onclose() {
        controller.abort()
        isAiRunning.value = false
        editor?.blur?.()
        console.log('服务关闭')
      }
    }
  )
}

const closeCustomMenu = () => {
  let ele: HTMLElement = document.querySelector('.custom-menu')!
  ele.style.visibility = 'hidden'
}

const createByAi = () => {
  if (title.value) {
    controller?.abort()
    createArticl(
      aiEditor as AiEditor,
      JSON.stringify({
        action: 'article_complete',
        params: {
          article_title: title.value,
          requirements: '500字以内'
        }
      }),
      1
    )
  } else {
    ElMessage.error(t('word.enterTitle'))
  }
  closeCustomMenu()
}

const translateByAi = (fromLanguage: string, toLanguage: string) => {
  if ((aiEditor as AiEditor).getText()) {
    controller?.abort()
    createArticl(
      aiEditor as AiEditor,
      JSON.stringify({
        action: 'translate',
        params: {
          fromText: (aiEditor as AiEditor).getText(),
          fromLanguage,
          toLanguage,
          requirements: '流畅'
        }
      }),
      2
    )
  } else {
    ElMessage.error(t('word.enterContent'))
  }
  closeCustomMenu()
}

const customBtn: CustomMenu[] = [
  {
    icon: '<svg t="1708659191202" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2627" width="16" height="16"><path d="M910.2336 1024H113.7664C51.2 1024 0 972.8 0 910.2336V113.7664C0 51.2 51.2 0 113.7664 0h796.4672C972.8 0 1024 51.2 1024 113.7664v796.4672C1024 972.8 972.8 1024 910.2336 1024z m-236.544-242.176l-196.6848-516.352h-71.3472L208.9984 781.824h73.6768l49.664-141.312h215.0144l52.6336 141.312h73.6768z m65.9712 0h65.3568V413.1584h-65.3568V781.824z m-7.68-498.6624c0 11.5456 3.9168 21.1712 11.6992 28.8512 7.7568 7.6544 17.5616 11.4944 29.3376 11.4944 11.776 0 21.6832-3.84 29.824-11.52 8.1152-7.68 12.16-17.28 12.16-28.8256 0-11.776-4.0448-21.5552-12.16-29.312a41.472 41.472 0 0 0-29.824-11.6736c-11.5712 0-21.2992 3.9168-29.184 11.8272a39.6544 39.6544 0 0 0-11.8528 29.184z m-205.312 299.008h-173.3376l77.312-214.6816c3.1232-8.448 5.888-20.4544 8.3456-35.9936h1.6896c2.8672 17.1264 5.5552 29.1072 7.9872 35.9936l78.0032 214.6816z" fill="#333333" p-id="2628"></path></svg>',
    onClick: (event: MouseEvent, editor: AiEditor) => {
      let ele: HTMLElement = document.querySelector('.custom-menu')!
      ele.style.visibility = 'visible'
    },
    className: 'custom-btn',
    tip: t('word.aiFunction')
  }
]

const initBind = () => {
  document.addEventListener('click', function (event) {
    if (isAiRunning.value && !disconnect.value) {
      ElMessageBox.confirm(t('word.stopAi'), t('word.warning'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancle'),
        type: 'warning'
      })
        .then((action) => {
          if (action === 'confirm') {
            disconnect.value = true
          }
        })
        .catch(() => {})
      return
    }
    if ((event?.target as HTMLElement).className === 'aie-codeblock-tools-comments') {
      event.preventDefault()
      ElMessage.info('没写完呢，哈哈😄')
    }
    if ((event?.target as HTMLElement).className === 'aie-codeblock-tools-explain') {
      event.preventDefault()
      ElMessage.info('没写完呢，哈哈😄')
    }
  })
}

const initStyle = () => {
  let obj = document.querySelector('.custom-btn')?.getBoundingClientRect()
  let ele = document.querySelector('.custom-menu')! as HTMLElement
  let x = obj!.x - ele?.getBoundingClientRect().width / 2
  ele.style.transform = `translate3d(${x}px, ${obj!.height + 4}px, 0px)`
}

const drawImg = () => {
  const option = {
    content: window.location.host,
    color: 'rgba(0, 0, 0, 1)',
    className: 'aie-container',
    zIndex: 1
  }
  drawWaterMark(option)
}

onMounted(() => {
  aiEditor = new AiEditor({
    element: editorRef.value,
    placeholder: t('word.enterArticleContent'),
    content: '',
    toolbarKeys: [...toolbarKeys, ...customBtn],
    fontFamily,
    lang: appStore.language === 'zh-CN' ? 'zh' : 'en',
    onChange(val) {
      console.log(val)
    }
  })
  drawImg()
  setTimeout(() => {
    initStyle()
  }, 0)
  window.addEventListener('resize', initStyle, false)
  initBind()
})

const onClickOutside = (e: Event) => {
  closeCustomMenu()
}

onBeforeUnmount(() => {
  controller?.abort()
  aiEditor && aiEditor.destroy()
  window.removeEventListener('resize', initStyle, false)
})

watch(
  () => appStore.language,
  (lang) => {
    aiEditor?.changeLang(lang === 'zh-CN' ? 'zh' : 'en')
  }
)
</script>
<style lang="scss" scoped>
.editor-wrap {
  height: 100%;
  .aie-container {
    position: relative;
    background-color: transparent;
    height: 100%;
    overflow: hidden;
    strong.running {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      color: #e6a23c;
      position: absolute;
      right: 12px;
      bottom: 12px;
      z-index: 10;
      span {
        font-size: 14px;
      }
      ul {
        list-style: none;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        li {
          list-style-type: none;
          animation: move 1.3s linear infinite;
          &:nth-child(2) {
            animation-delay: -1.1s;
          }
          &:nth-child(3) {
            animation-delay: -0.9s;
          }
        }
      }
    }
    &:deep(p) {
      margin: 0;
    }
    &:deep(.aie-codeblock-tools) {
      .aie-codeblock-tools-comments,
      .aie-codeblock-tools-explain {
        // display: none;
      }
    }
    &-header {
      position: relative;
      padding-right: 30px;
      border-bottom: 1px solid #eee;
      background-color: #fff;
      z-index: 2;
      &:deep(aie-header) {
        & > div {
          border-bottom: none;
        }
        // .aie-menu-item {
        //   & > div {
        //     & > svg {
        //       margin-top: 1px;
        //     }
        //   }
        // }
      }
      .custom-menu {
        z-index: 9999;
        visibility: hidden;
        position: absolute;
        inset: 0px auto auto 0px;
        margin: 0px;
        background: var(--aie-dropdown-container-bg-color);
        border: solid 1px var(--aie-dropdown-container-border-color);
        overflow: auto;
        box-shadow: 0 2px 5px 2px #0000001a;
        padding: 4px 0;
        border-radius: 3px;
        color: var(--aie-menus-text-color);
        transition-duration: 0;
        li {
          padding: 8px;
          cursor: pointer;
          &:hover {
            background-color: #efefef;
          }
        }
      }
      section.btns {
        position: absolute;
        right: 4px;
        top: 50%;
        height: 26px;
        margin-top: -13px;
        .save-btn {
          padding: 0px;
          display: none;
        }
      }
    }
    &-main {
      flex: 1;
      overflow-y: auto;
      &::-webkit-scrollbar {
        display: none;
      }
      &:deep(> div) {
        min-height: calc(100% - 40px);
        height: auto;
        overflow: hidden !important;
        display: grid;
        &.oz-input {
          height: 30px;
          min-height: 30px;
          width: auto;
          min-width: 100px;
          padding-top: 10px;
          .oz-input__wrapper {
            box-shadow: none;
            // border-bottom: 1px solid #dcdfe6;
            border-radius: 0;
          }
          input {
            font-size: 22px;
            color: #333;
            text-align: center;
            &::placeholder {
              font-size: 22px;
              text-align: center;
            }
          }
        }
        .aie-content {
          min-height: 100%;
          overflow: hidden;
          box-sizing: border-box;
        }
      }
      &:deep(em) {
        font-style: italic;
      }
      &:deep(ul) {
        li {
          list-style-type: initial;
        }
      }
      &:deep(ol) {
        li {
          list-style-type: auto;
        }
      }
    }
    &-footer {
      display: none;
    }
  }
  @keyframes move {
    0%,
    60%,
    to {
      transform: none;
    }
    30% {
      transform: translateY(-3px);
    }
  }
}
</style>
