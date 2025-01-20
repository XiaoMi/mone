<template>
  <div class="about-wrap">
    <div class="about-container">
      <ul class="ul-wrap">
        <li>
          <h1><span>{{ t('about.aboutUs') }}</span></h1>
          <About />
        </li>
        <li>
          <h1><span>{{ t('about.contactUs') }}</span></h1>
          <Connect />
        </li>
        <li>
          <h1><span>{{ t('about.problemFeedback') }}</span></h1>
          <Feedback />
        </li>
      </ul>
    </div>
    <CommonFoot></CommonFoot>
    <CommonBg></CommonBg>
  </div>
</template>

<script setup lang="ts">
import About from './components/About.vue'
import Connect from './components/Connect.vue'
import Feedback from './components/Feedback.vue'
import CommonFoot from '@/components/CommonFoot.vue'
import CommonBg from '@/components/common-bg/index.vue'
import { t } from '@/locales'
</script>

<style lang="scss" scoped>
.about-wrap {
  margin-top: -60px !important;
  position: relative;
  min-width: 1000px;
}
.about-container {
  padding: 200px 0 40px;
  width:1000px;
  margin :0 auto;
  ul.ul-wrap {
    li {
      padding-bottom: 40px;
      h1 {
        margin-top: 18px;
        margin-bottom: 18px;
        font-size: 20px;
        position: relative;
      }
    }
  }
}
</style>
