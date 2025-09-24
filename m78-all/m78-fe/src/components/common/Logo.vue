<!--
 * @Description:
 * @Date: 2024-01-12 10:17:23
 * @LastEditTime: 2024-08-28 20:34:31
-->
<template>
  <div
    :class="['logo-container', size ? size + '-logo' : '', link ? 'link-logo' : '']"
    @click="goHome"
  >
    <!-- <Logo2 /> -->
    <div class="log-img"></div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

interface Props {
  size?: string
  link?: string
}
const props = defineProps<Props>()

const goHome = () => {
  if (props.link) {
    router.push({
      path: props.link
    })
  }
}
</script>

<style scoped lang="scss">
.logo-container {
  --vp-home-hero-name-color: transparent;
  --vp-home-hero-name-background: -webkit-linear-gradient(120deg, #bd34fe 30%, #41d1ff);
  --vp-home-hero-image-background-image: linear-gradient(-45deg, #bd34fe 50%, #47caff 50%);
  display: flex;
  align-items: center;
  // margin-right: 26px;
  color: var(--vp-home-hero-name-color);
  font-size: 18px;
  background: var(--vp-home-hero-name-background);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: var(--vp-home-hero-name-color);
  width: 80px;
  // 小logo
  &.small-logo {
  }
  // 是否可点击
  &.link-logo {
    cursor: pointer;
  }
}

.log-img {
  width: 74px;
  height: 28px;
  background-color: transparent;
  background-image: url('./images/oz78.png');
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
}
</style>
