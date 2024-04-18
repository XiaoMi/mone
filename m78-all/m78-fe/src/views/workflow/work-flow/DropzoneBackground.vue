<script setup>
import { Background } from '@vue-flow/background'
</script>

<template>
  <div style="height: 100%; width: 100%">
    <Background bgColor="#f2f3f5">
      <slot />
    </Background>
  </div>
</template>
