<!--
 * @Description:
 * @Date: 2024-03-05 17:15:23
 * @LastEditTime: 2024-03-20 15:02:10
-->
<template>
  <div class="probot-space">
    <MySpaceHead></MySpaceHead>
    <Tabs />
  </div>
</template>

<script setup lang="ts">
import MySpaceHead from './MySpaceHead.vue'
import Tabs from './tabs.vue'
</script>

<style scoped lang="scss">
.probot-space {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-bottom: 10px;
  .title {
    display: flex;
    align-items: center;
    padding: 10px 50px 0;

    h1 {
      line-height: 30px;
      font-size: 20px;
      padding-right: 20px;
      color: rgb(71, 85, 105);
      padding-left: 10px;
    }
  }
}
</style>
