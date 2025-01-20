<!--
 * @Description:
 * @Date: 2024-01-10 16:51:54
 * @LastEditTime: 2024-08-27 19:34:12
-->

<template>
  <div class="layout-header">
    <div class="header-container">
      <Logo link="/"></Logo>
      <el-menu
        mode="horizontal"
        class="menu-container"
        router
        @select="handleSelect"
        :default-active="state.active"
      >
        <el-menu-item index="/probot-index" v-if="state.active?.includes('probot')">{{
          'Probot 首页'
        }}</el-menu-item>
        <el-menu-item v-if="state.active?.includes('probot')"
          ><ProbotResources></ProbotResources
        ></el-menu-item>
        <el-menu-item v-if="state.active?.includes('probot')"
          ><ProbotLibrary></ProbotLibrary
        ></el-menu-item>
        <el-menu-item index="/probot-index" v-if="showProbot()">{{ 'Probot 首页' }}</el-menu-item>
        <el-menu-item v-if="showProbot()"><ProbotResources></ProbotResources></el-menu-item>
        <el-menu-item v-if="showProbot()"><ProbotLibrary></ProbotLibrary></el-menu-item>
        <el-sub-menu index="1">
          <template #title>多模态</template>
          <!-- <el-menu-item index="/probot-mode-voice">声音组件</el-menu-item> -->
          <el-menu-item index="/probot-mode-image">图像组件</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/about">{{ t('menu.menu4') }}</el-menu-item>
      </el-menu>
    </div>
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

const handleSelect = () => {
  // console.log(key, keyPath)
}

const showProbot = () => {
  return true
}

onMounted(() => {
  state.active = route.path
})

//监听路由
watch(
  () => route,
  (val) => {
    const arr = ['AI Document', 'AI Chat', 'AI Datasource', 'AI Agent', 'AI Probot']
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
.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  // 头部容器
  .header-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 60px;
    padding: 0 20px 0 26px;
    flex: 1 0 auto;
    background-color: transparent;

    // 菜单容器
    .menu-container {
      flex: 1;
      height: 60px;
      line-height: 60px;
      border: none;
      background-color: transparent;

      :deep(.oz-menu-item) {
        line-height: 60px;
        color: inherit;
        border: none;
        transition: all 0.3s;

        &.is-active {
          border: none;
          background-image: linear-gradient(to right, #fff, var(--oz-menu-active-color));
          -webkit-background-clip: text;
          background-clip: text;
          -webkit-text-fill-color: transparent;
        }

        &:hover,
        &:focus {
          background-color: transparent;
          transform: scale(1.1);
          color: #fff !important;
        }
      }
    }
  }

  // 头部右侧
  .header-footer {
    display: flex;
    align-items: center;
    height: 100%;
    padding: 0 20px 0 26px;

    .lang-area {
      padding-right: 10px;
    }
  }
}
</style>

<style lang="scss">
// 子菜单样式
.header-container {
  .oz-menu--horizontal {
    > .oz-sub-menu {
      .oz-sub-menu__title {
        color: inherit !important;
        line-height: 60px;
        border: none;
        transition: all 0.3s;

        &:hover,
        &:focus {
          background-color: transparent !important;
          transform: scale(1.1);
          color: #fff !important;
        }
      }
    }
  }
}
</style>
