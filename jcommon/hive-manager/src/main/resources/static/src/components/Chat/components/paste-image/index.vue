<template>
  <div class="paste-image">
    <div class="button-icon" @click="callScreenshot">
      <font-awesome-icon color="#FFF" :icon="['fas', 'image']" />
    </div>
  </div>
</template>

<script setup lang="ts">
import util from '@/libs/util';

async function callScreenshot() {
  await util.pasteImage()
}
</script>

<style lang="scss" scoped>
.paste-image {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;

  .button-icon {
    display: inline-block;
  }
}
</style>
