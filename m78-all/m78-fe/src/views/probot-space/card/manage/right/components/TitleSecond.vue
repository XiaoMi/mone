<!--
 * @Description: 
 * @Date: 2024-10-08 15:23:02
 * @LastEditTime: 2024-10-17 10:44:47
-->
<template>
  <h2 class="title-second">{{ title }}</h2>
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
.title-second {
  color: #383743;
  font-size: 14px;
  margin-bottom: 5px;
}
</style>
