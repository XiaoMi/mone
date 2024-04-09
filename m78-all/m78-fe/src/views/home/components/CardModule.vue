<!--
 * @Description:
 * @Date: 2024-01-11 17:29:38
 * @LastEditTime: 2024-01-25 14:12:06
-->
<template>
  <div class="module-container" ref="cardRef">
    <ul>
      <li>
        <div class="item-module" @click="goCode" :style="height">
          <div class="item-img">
            <img :src="img1Src" />
          </div>
          <div class="item-main">
            <div class="item-title">AI Code</div>
            <div class="item-content" v-html="t('home.code')"></div>
          </div>
        </div>
      </li>
      <li>
        <div class="item-module" @click="goDocument" :style="height">
          <div class="item-img">
            <img :src="img2Src" />
          </div>
          <div class="item-main">
            <div class="item-title">AI Document</div>
            <div class="item-content" v-html="t('home.doc')"></div>
          </div>
        </div>
      </li>
      <li>
        <div class="item-module" @click="goChat" :style="height">
          <div class="item-img">
            <img :src="img3Src" />
          </div>
          <div class="item-main">
            <div class="item-title">AI Chat</div>
            <div class="item-content" v-html="t('home.chat')"></div>
          </div>
        </div>
      </li>
      <li>
        <div class="item-module" @click="goTranslate" :style="height">
          <div class="item-img">
            <img :src="img4Src" />
          </div>
          <div class="item-main">
            <div class="item-title">AI Translate</div>
            <div class="item-content" v-html="t('home.translate')"></div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { t } from '@/locales'
import { useAppStore } from '@/stores'

const router = useRouter()
const appStore = useAppStore()

const img1Src = ref(`${import.meta.env.VITE_APP_STATIC_PATH}images/code.png`)
const img2Src = ref(`${import.meta.env.VITE_APP_STATIC_PATH}images/document.png`)
const img3Src = ref(`${import.meta.env.VITE_APP_STATIC_PATH}images/chat.png`)
const img4Src = ref(`${import.meta.env.VITE_APP_STATIC_PATH}images/translate.png`)

const height = ref('')
const cardRef = ref('')

const goCode = () => {
  router.push({
    path: 'code'
  })
}
const goDocument = () => {
  router.push({
    path: 'doc'
  })
}
const goChat = () => {
  router.push({
    path: 'chat'
  })
}
const goTranslate = () => {
  router.push({
    path: 'translate'
  })
}
const setHeight = () => {
  height.value = 'height:auto'
  nextTick(() => {
    const $ele = cardRef.value?.getElementsByClassName('item-module')
    const eleArr = [...$ele]
    let h = 0
    for (var i in eleArr) {
      h = Math.max(h, eleArr[i].offsetHeight)
    }
    height.value = 'height:' + h + 'px'
  })
}

window.onresize = function () {
  setHeight()
}

watch(
  () => appStore.language,
  () => {
    setHeight()
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.module-container {
  width: 100%;
  text-align: center;
  padding: 50px 0;
}
ul {
  display: inline-flex;
  justify-content: space-around;
  flex-wrap: wrap;
  padding: 0 5%;
  li {
    flex-basis: 50%;
    padding: 30px 20px 30px;
    position: relative;
    display: inline-block;
    min-height: 176px;
    &:hover .item-module {
      transform: translateY(-5px);
    }
    .item-module {
      height: 100%;
      border: 1px solid hsla(0, 0%, 100%, 0.5);
      border-radius: 16px;
      color: #151b26;
      cursor: pointer;
      margin: 0 auto;
      min-width: 0;
      padding: 0 22px 24px 24px;
      position: relative;
      transition: all 0.3s ease-in-out;
      -webkit-transition: all 0.3s ease-in-out;
      display: flex;
      box-shadow: (0 0 #0000, 0 0 #0000), (0 0 #0000, 0 0 #0000),
        (0 25px 50px -12px rgba(0, 0, 0, 0.25));
    }
    &:nth-child(1) .item-module {
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgb(138, 230, 225),
        rgba(255, 255, 255, 0.1)
      );
    }
    &:nth-child(2) .item-module {
      background-color: rgb(230, 240, 255);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgb(168, 202, 255),
        rgba(255, 255, 255, 0.1)
      );
    }
    &:nth-child(3) .item-module {
      background-color: rgb(222, 244, 255);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgb(153, 226, 255),
        rgba(255, 255, 255, 0.1)
      );
    }
    &:nth-child(4) .item-module {
      background-color: rgb(230, 235, 255);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgb(184, 187, 255),
        rgba(255, 255, 255, 0.1)
      );
    }

    .item-img {
      width: 240px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      img {
        width: 100%;
        position: absolute;
        top: -60px;
      }
    }

    .item-main {
      flex: 1;
      padding-left: 20px;
    }
    .item-title {
      padding-top: 20px;
      padding-bottom: 10px;
      text-align: left;
      font-size: 24px;
      font-weight: 600;
      line-height: 32px;
    }
    .item-content {
      font-size: 14px;
      line-height: 22px;
      text-align: left;
      letter-spacing: 1px;
    }
  }
}
</style>
