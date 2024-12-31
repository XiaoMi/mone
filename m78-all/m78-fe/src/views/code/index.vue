<!--
 * @Description:
 * @Date: 2024-01-15 10:12:46
 * @LastEditTime: 2024-01-26 17:44:14
-->
<template>
  <div class="code-wrap">
    <div class="code-container">
      <!-- 头 -->
      <div class="code-head">
        <h1>AI Code</h1>
        <p>{{ t('code.tip') }}</p>
        <div class="head-btn">
          <el-button size="large" @click="btnStar">{{ t('code.btnStar') }}</el-button>
          <el-button type="primary" size="large">{{ t('code.btnTry') }}</el-button>
        </div>
      </div>
      <!--  -->
      <CodeVideo></CodeVideo>
      <div class="code-contant-wrap">
        <h1>Why Ai Code?</h1>
        <div class="code-content">
          <div class="code-module-wrap">
            <div class="code-module">
              <div class="code-module-img">
                <svg1 />
              </div>
              <div class="code-module-text">
                <h2>{{ t('code.title1') }}</h2>
                <div class="text-container">
                  <p v-html="t('code.content1P1')"></p>
                  <p v-html="t('code.content1P2')"></p>
                  <p v-html="t('code.content1P3')"></p>
                </div>
              </div>
            </div>
          </div>
          <div class="code-module-wrap">
            <div class="code-module">
              <div class="code-module-img">
                <svg2 />
              </div>
              <div class="code-module-text">
                <h2>{{ t('code.title2') }}</h2>
                <div class="text-container">
                  <p v-html="t('code.content2P1')"></p>
                  <p v-html="t('code.content2P2')"></p>
                  <p v-html="t('code.content2P3')"></p>
                </div>
              </div>
            </div>
          </div>
          <div class="code-module-wrap">
            <div class="code-module">
              <div class="code-module-img">
                <svg3 />
              </div>
              <div class="code-module-text">
                <h2>{{ t('code.title3') }}</h2>
                <div class="text-container">
                  <p v-html="t('code.content3P1')"></p>
                  <p v-html="t('code.content3P2')"></p>
                  <p v-html="t('code.content3P3')"></p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <CommonFoot></CommonFoot>
    <CommonBg></CommonBg>
  </div>
</template>

<script setup lang="ts">
import CodeVideo from './components/CodeVideo.vue'
import CommonFoot from '@/components/CommonFoot.vue'
import CommonBg from '@/components/common-bg/index.vue'
import { t } from '@/locales'
import svg1 from './assets/svg1.svg'
import svg2 from './assets/svg2.svg'
import svg3 from './assets/svg3.svg'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const btnStar = () => {
  router.push({
    path: '/manual/2-0'
  })
}
</script>

<style scoped lang="scss">
.code-wrap {
  margin-top: -60px !important;
  position: relative;
  min-width: 1000px;
}
.code-head {
  position: relative;
  padding: 120px 0 50px;
  width: 1000px;
  margin: 0 auto;
  h1 {
    font-size: 48px;
    font-weight: 800;
    font-family: Inter, sans-serif;
    padding-bottom: 10px;
    color: #303133;
  }
  p {
    font-size: 16px;
    line-height: 50px;
    padding-bottom: 20px;
    color: #606266;
  }
  .head-btn {
    .oz-button {
      margin-right: 30px;
    }
  }
}
.code-contant-wrap {
  width: 100%;
  background-color: #fff;
  padding: 50px 0 100px;
  margin-top: 80px;
  h1 {
    font-size: 48px;
    font-weight: 800;
    font-family: Inter, sans-serif;
    color: #303133;
    text-align: center;
    padding-bottom: 50px;
    opacity: 1;
  }
}
.code-content {
  width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
}
.code-module-wrap {
  position: relative;
  width: 30%;
  &::before {
    content: '';
    display: inline-block;
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    box-shadow: (0 0 #0000, 0 0 #0000), (0 0 #0000, 0 0 #0000),
      (0 25px 50px -12px rgba(0, 0, 0, 0.25));
    border-radius: 15px;
  }
  &:nth-child(1)::before {
    background: #fff;
  }
  &:nth-child(2)::before {
    background: #fff;
  }
  &:nth-child(3)::before {
    background: #fff;
  }
}
.code-module {
  padding: 10px 10px 40px;
  position: relative;
  z-index: 6;
  &-text {
    width: 100%;
    h2 {
      font-size: 28px;
      padding: 20px;
      color: #666;
    }
    .text-container {
      padding: 2px 10px;
    }
    p {
      font-size: 14px;
      line-height: 30px;
      display: flex;
      position: relative;
      padding-left: 10px;
      &::before {
        content: '';
        width: 5px;
        height: 5px;
        border-radius: 50%;
        background-color: #666;
        position: absolute;
        top: 13px;
        left: -5px;
      }
    }
  }
  &-img {
    text-align: center;
    height: 150px;
    svg {
      height: 150px;
    }
  }
}
</style>
