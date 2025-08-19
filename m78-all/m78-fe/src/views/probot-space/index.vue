<!--
 * @Description:
 * @Date: 2024-03-05 17:15:23
 * @LastEditTime: 2024-09-11 19:23:37
-->
<template>
  <div class="probot-space">
    <MySpaceHead v-if="route.name != 'AI Probot My Collect'"></MySpaceHead>
    <Tabs />
  </div>
</template>

<script setup lang="ts">
import MySpaceHead from './MySpaceHead.vue'
import Tabs from './Tabs.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped lang="scss">
.probot-space {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-bottom: 10px;
  padding-top: 10px;
  flex: 1;
}
</style>
