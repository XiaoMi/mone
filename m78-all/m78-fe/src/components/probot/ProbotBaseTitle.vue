<!--
 * @Description: 
 * @Date: 2024-03-20 19:56:54
 * @LastEditTime: 2024-03-20 21:11:20
-->
<template>
  <div class="probot-base-title">
    {{ props.title }}
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.probot-base-title {
  margin: 10px 10px 0;
  padding: 18px 20px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 5px;
  font-size: 16px;
  color: rgb(71, 85, 105, 0.7);
  font-weight: 500;
}
</style>
