<template>
  <div class="header-container">
    <Logo link="/"></Logo>
    <el-menu mode="horizontal" class="menu-container" router :default-active="state.active">
      <el-sub-menu index="1">
        <template #title>{{ menuName }}</template>
        <el-menu-item index="/code">AI Code</el-menu-item>
        <el-menu-item index="/doc">AI Document</el-menu-item>
        <el-menu-item index="/chat">AI Chat</el-menu-item>
        <el-menu-item index="/translate">AI Translate</el-menu-item>
        <el-menu-item index="/data-source">AI Datasource</el-menu-item>
        <el-menu-item index="/agent">AI Agent</el-menu-item>
        <el-menu-item index="/word">AI Word</el-menu-item>
        <el-menu-item @click="ideClick">AI Ide</el-menu-item>
        <el-menu-item index="/probot">AI Probot</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/probot-index" v-if="state.active?.includes('probot')">{{
        'Probot 首页'
      }}</el-menu-item>
      <el-menu-item v-if="state.active?.includes('probot')"
        ><ProbotLibrary></ProbotLibrary
      ></el-menu-item>
      <el-menu-item v-if="state.active?.includes('probot')"
        ><ProbotResources></ProbotResources
      ></el-menu-item>
      <el-menu-item index="/manual">{{ t('menu.menu2') }}</el-menu-item>
      <el-menu-item index="/about">{{ t('menu.menu4') }}</el-menu-item>
    </el-menu>
    <div class="header-footer">
      <div class="lang-area">
        <I18nToggle></I18nToggle>
      </div>
      <HeaderAvatar></HeaderAvatar>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { t } from '@/locales'
import { useRoute } from 'vue-router'
import Logo from '../common/Logo.vue'
import I18nToggle from '../common/I18nToggle.vue'
import HeaderAvatar from '../common/HeaderAvatar.vue'
import ProbotLibrary from './ProbotLibrary.vue'
import ProbotResources from './ProbotResources.vue'

const route = useRoute()
const state = reactive({
  active: route.path
})
const menuName = ref<string>('')

const ideClick = () => {
  window.open('/web-ide', '_blank')
}

onMounted(() => {
  state.active = route.path
})

//监听路由
watch(
  () => route,
  (val) => {
    const arr = [
      'AI Code',
      'AI Document',
      'AI Chat',
      'AI Translate',
      'AI Datasource',
      'AI Agent',
      'AI Probot'
    ]
    if (arr.includes(val.name)) {
      menuName.value = val.name
    } else if (val.name?.includes('AI Probot')) {
      menuName.value = 'AI Probot'
    } else {
      menuName.value = t('menu.menu1')
    }
    if (state.active !== val.path) {
      state.active = val.path
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.header-container {
  display: flex;
  align-items: center;
  border-bottom: solid 1px transparent;
  padding: 0 20px 0 26px;
  background-color: transparent;
  .menu-container {
    border-bottom: none;
    flex: 1;
    background-color: transparent;
    :deep(.oz-menu-item) {
      color: inherit;
      line-height: 60px;
      &.is-active {
        border: none;
      }
      &:focus {
        background-color: transparent;
      }
      &:hover {
        color: var(--oz-menu-active-color) !important;
      }
    }
  }
}
.header-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.color-theme {
  font-size: 20px;
  font-weight: bold;
  padding-right: 10px;
  position: relative;
  top: 2px;
}
.lang-area {
  padding-right: 10px;
}
</style>
<style lang="scss">
.header-container .oz-menu--horizontal > .oz-sub-menu .oz-sub-menu__title {
  color: inherit !important;
  line-height: 60px;
  &.is-active {
    border: none;
  }
  &:focus {
    background-color: transparent;
  }
  &:hover {
    color: var(--oz-menu-active-color) !important;
  }
}
</style>
