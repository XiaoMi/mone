<template>
  <!-- t('word.docTitle') -->
  <div class="word-wrap">
    <CommontPage :showDefaultBtns="false" :modelValue="1" title="----" :showCommonInput="false">
      <template #left>
        <aiEditor />
      </template>
      <!-- <template #btnsTop>
        <div>1</div>
      </template> -->
      <template #chatList>
        <div>
          <!-- <ul>
            <li>这里是列表</li>
            <li>这里是列表</li>
            <li>这里是列表</li>
            <li>这里是列表</li>
            <li>这里是列表</li>
          </ul> -->
        </div>
      </template>
    </CommontPage>
  </div>
</template>

<script setup lang="ts">
import aiEditor from '@/components/word-edit/index.vue'
import CommontPage from '@/components/common-page/CommonPage.vue'
import { t } from '@/locales'
</script>

<style lang="scss" scoped>
.word-wrap {
  height: 100%;
}
</style>
