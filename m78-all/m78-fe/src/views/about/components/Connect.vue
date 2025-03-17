<template>
  <div class="connect-wrap">
    <dl>
      <dt>{{ t('about.contactGitHub') }}</dt>
      <dd>
        <el-image :src="GitHub" />
      </dd>
    </dl>
  </div>
</template>
<script setup lang="ts">
// import Img from '../assets/photo.png'
import GitHub from '../assets/github2.png'
import { t } from '@/locales'
</script>
<style lang="scss" scoped>
.connect-wrap {
  display: flex;
  dl {
    width: 25%;
    height: 100%;
    border-radius: 5px;
    background-color: #fff;
    margin-right: 40px;
    dt {
      font-size: 14px;
      line-height: 30px;
      border-bottom: 1px solid #ddd;
      padding: 10px 20px;
      color: #666;
      font-weight: bold;
    }
    dd {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 283px;
      :deep(.oz-image) {
        width: 250px;
      }
    }
  }
}
</style>
