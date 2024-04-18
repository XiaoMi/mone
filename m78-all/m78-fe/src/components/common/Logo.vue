<template>
  <div
    :class="['logo-container', size ? size + '-logo' : '', link ? 'link-logo' : '']"
    @click="goHome"
  >
    <Logo2 />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Logo2 from './images/m78.svg'

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
  display: flex;
  align-items: center;
  margin-right: 26px;
  .oz-image {
    width: 74px;
  }
  .m78-svg {
    width: 40px;
  }
  // 是否可点击
  &.link-logo {
    cursor: pointer;
  }
}
</style>
