<script setup lang="ts">
import CommontPage from '@/components/common-page/CommonPage.vue'
import Sider from './sider/index.vue'
import ChatWindow from '../index.vue'
</script>

<template>
  <div style="height: 100%">
    <CommontPage
      :modelValue="1"
      class="flex-row-reverse flex-1"
      uuid="ai-chat"
      :title="$t('chat.title')"
      :showDefaultBtns="false"
      :showCommonInput="false"
    >
      <template #left>
        <ChatWindow class="flex-1 h-full" />
      </template>
      <template #chatList>
        <Sider class="sider" />
      </template>
    </CommontPage>
  </div>
</template>
