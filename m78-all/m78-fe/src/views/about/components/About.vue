<template>
  <div class="about-cnt">
    <p>
      {{ t('about.aboutContent') }}
    </p>
  </div>
</template>
<script setup lang="ts">
import { t } from '@/locales'
</script>

<style lang="scss" scoped>
.about-cnt {
  text-indent: 2em;
  font-size: 17px;
  line-height: 36px;
  color:#666;
}
</style>
