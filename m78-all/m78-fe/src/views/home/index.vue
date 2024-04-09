<template>
  <div class="plug-container">
    <div id="container"><div id="output"></div></div>
    <div class="plug-main">
      <h1 class="title">
        <span>{{ t('home.title') }}</span>
      </h1>
      <div class="tip"><span>Smarter，More Innovative，Infinite Possibilities</span></div>
      <div class="input-container">
        <HomeInput></HomeInput>
      </div>
      <div class="plug-main-bottom">
        <CardModule></CardModule>
        <CommonFoot></CommonFoot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { watch, nextTick } from 'vue'
import Victor from './dynamic-bg/vector'
import HomeInput from './components/HomeInput.vue'
import { t } from '@/locales'
import CommonFoot from '@/components/CommonFoot.vue'
import CardModule from './components/CardModule.vue'
import { useAppStore } from '@/stores'

const appStore = useAppStore()

watch(
  () => appStore.language,
  () => {
    nextTick(() => {
      new Victor('container', 'output')
    })
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.plug-container {
  display: flex;
  flex-direction: column;
  position: relative;
  margin-top: -60px !important;
  #container {
    position: absolute;
    height: 100%;
    width: 100%;
  }
  #output {
    width: 100%;
    height: 100%;
  }
  .plug-main {
    flex: 1;
    position: relative;
    z-index: 3;
    display: flex;
    flex-direction: column;
  }
  .title {
    color: #151b26;
    font-size: 40px;
    font-weight: 700;
    margin-top: 88px;
    text-align: center;
    span {
      -webkit-text-fill-color: transparent;
      background: linear-gradient(90deg, #2cd497, #56ccda 10%, #5eb5ff 35%, #2468f2 60% 87%);
      background-clip: text;
      -webkit-background-clip: text;
      background-size: auto;
      color: transparent;
    }
  }
  .tip {
    color: #151b26;
    font-size: 18px;
    font-weight: 700;
    text-align: center;
    margin-top: 20px;
    color: #ddd;
    span {
    }
  }
  .input-container {
    border-radius: 12px;
    box-shadow: 0 4px 8px 0 rgba(36, 104, 242, 0.15);
    margin: 48px auto 0;
    min-width: 600px;
    max-width: 900px;
    overflow: hidden;
  }
  .plug-main-bottom {
    background-color: var(--oz-menu-bg-color);
    margin-top: 90px;
    flex: 1;
  }
}
</style>

<style lang="scss">
.plug-container .input-item {
  .oz-input__wrapper {
    border-radius: 12px;
    &:has(.oz-input__inner:focus) {
      -webkit-box-shadow: 0 0 0 1000px white inset;
    }
  }
  .oz-input__inner {
    color: #151b26;
    font-size: 14px;
    height: 52px;
    line-height: 22px;
    max-height: 236px;
    width: 100%;
    padding: 1px;
    -webkit-box-shadow: 0 0 0 1000px transparent inset;
    transition: 0s;
    &:focus {
    }
  }
}
</style>
