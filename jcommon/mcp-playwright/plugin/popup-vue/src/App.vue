<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import LangSwitch from '@/components/LangSwitch.vue'
// import { useI18n } from 'vue-i18n'
import { t } from '@/locales/index'
const router = useRouter()
//const { t } = useI18n()
</script>

<template>
  <header class="app-header">
    <nav class="app-nav">
      <el-button-group>
        <el-button @click="router.push('/')" type="primary" plain>{{ t('nav.home') }}</el-button>
        <el-button @click="router.push('/features')" type="primary" plain>{{ t('nav.features') }}</el-button>
        <el-button @click="router.push('/preview')" type="primary" plain>{{ t('nav.preview') }}</el-button>
      </el-button-group>
      <LangSwitch class="app-lang-switch" />
    </nav>
  </header>
  <div class="app-content">
    <RouterView />
  </div>
</template>

<style scoped>
.app-header {
  background-color: var(--el-bg-color);
  padding: 1rem;
  border-bottom: 1px solid var(--el-border-color-light);
}

.app-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.app-lang-switch {
  margin-left: 20px;
}

.app-content {
  flex: 1;
  overflow: hidden;
}
</style>
