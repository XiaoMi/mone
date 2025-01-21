<script setup>
import { Background } from '@vue-flow/background'
</script>

<template>
  <div style="height: 100%; width: 100%">
    <!-- <Background :size="2" :gap="20" pattern-color="#BDBDBD"> -->
    <Background bgColor="#f2f3f5">
      <slot />
    </Background>
  </div>
</template>
