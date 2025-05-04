<!--
 * @Description: 
 * @Date: 2024-09-23 16:38:18
 * @LastEditTime: 2024-10-08 15:19:18
-->
<template>
  <div class="divider">
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
.divider {
  width: 100%;
  display: flex;
  align-items: center;
  padding: 10px 0 18px;
  &:after {
    content: '';
    display: inline-block;
    width: 100%;
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
  }
}
</style>